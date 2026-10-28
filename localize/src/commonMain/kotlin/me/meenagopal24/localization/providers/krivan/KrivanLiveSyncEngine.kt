package me.meenagopal24.localization.providers.krivan

import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.readUTF8Line
import kotlin.concurrent.Volatile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Encapsulated background subscriber for Krivan's Server-Sent Events (SSE) stream.
 *
 * Supports:
 * - Real-time full sync triggers (`version.published`, `version.activated`)
 * - Sub-50ms instant delta patches for hotfix overrides (`override.modified`)
 * - Lifecycle pause/resume (disconnects in background, reconnects in foreground)
 */
internal class KrivanLiveSyncEngine(
    private val config: KrivanConfig,
    private val httpClient: HttpClient,
    private val onUpdateTriggered: () -> Unit,
    private val onPatchReceived: (key: String, value: String?, action: String) -> Unit,
    private val onStateChanged: (KrivanSyncState) -> Unit
) {
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var syncJob: Job? = null
    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var isPaused = false

    /**
     * Starts the background SSE streaming connection.
     */
    fun start() {
        if (syncJob?.isActive == true) return
        isPaused = false

        syncJob = syncScope.launch {
            val eventsParam = config.subscribedEvents.joinToString(",")
            val streamPath = KrivanConstants.buildStreamPath(config.projectId)
            val streamUrl = "${config.effectiveBaseUrl}$streamPath?apiKey=${config.apiKey}&events=$eventsParam"

            while (isActive && !isPaused) {
                try {
                    httpClient.prepareGet(streamUrl) {
                        headers {
                            append(KrivanConstants.HEADER_ACCEPT, "text/event-stream")
                            append(KrivanConstants.HEADER_CACHE_CONTROL, "no-cache")
                        }
                    }.execute { response ->
                        if (!response.status.isSuccess()) {
                            onStateChanged(KrivanSyncState.Error("SSE Connection rejected: HTTP ${response.status.value}"))
                            return@execute
                        }

                        val channel = response.bodyAsChannel()
                        var currentEvent: String? = null

                        while (!channel.isClosedForRead && isActive && !isPaused) {
                            val line = channel.readUTF8Line() ?: break
                            val trimmed = line.trim()

                            if (trimmed.isEmpty() || trimmed.startsWith(KrivanConstants.SSE_PING_PREFIX)) {
                                continue
                            }

                            if (trimmed.startsWith(KrivanConstants.SSE_EVENT_PREFIX)) {
                                currentEvent = trimmed.removePrefix(KrivanConstants.SSE_EVENT_PREFIX).trim()
                            } else if (trimmed.startsWith(KrivanConstants.SSE_DATA_PREFIX)) {
                                val rawData = trimmed.removePrefix(KrivanConstants.SSE_DATA_PREFIX).trim()
                                val eventType = currentEvent ?: "message"

                                handleStreamPayload(eventType, rawData)
                                currentEvent = null
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {
                    onStateChanged(KrivanSyncState.Error("SSE Network drop: ${e.message}", e))
                }

                if (!config.autoReconnect || !isActive || isPaused) {
                    break
                }

                delay(config.reconnectDelayMs)
            }
        }
    }

    /**
     * Pauses the streaming connection (e.g., when the app transitions to background).
     * Saves battery and prevents socket leaks.
     */
    fun pause() {
        isPaused = true
        syncJob?.cancel()
        syncJob = null
        onStateChanged(KrivanSyncState.Idle)
    }

    /**
     * Resumes the streaming connection (e.g., when the app returns to foreground).
     */
    fun resume() {
        if (isPaused) {
            isPaused = false
            start()
        }
    }

    /**
     * Stops the streaming connection completely.
     */
    fun stop() {
        isPaused = true
        syncJob?.cancel()
        syncJob = null
        onStateChanged(KrivanSyncState.Idle)
    }

    private fun handleStreamPayload(eventType: String, rawData: String) {
        when (eventType) {
            "connected" -> {
                val permitted = try {
                    val obj = json.parseToJsonElement(rawData).jsonObject
                    obj["permittedEvents"]?.toString() ?: ""
                    config.subscribedEvents
                } catch (e: Exception) {
                    config.subscribedEvents
                }
                onStateChanged(KrivanSyncState.LiveConnected(permitted))
            }
            KrivanConstants.EVENT_OVERRIDE_MODIFIED -> {
                try {
                    val obj = json.parseToJsonElement(rawData).jsonObject
                    val key = obj["keyName"]?.jsonPrimitive?.content
                        ?: obj["keyId"]?.jsonPrimitive?.content.orEmpty()
                    val value = obj["value"]?.jsonPrimitive?.content
                    val action = obj["action"]?.jsonPrimitive?.content ?: "update"

                    if (key.isNotEmpty() && value != null) {
                        onPatchReceived(key, value, action)
                    } else {
                        onUpdateTriggered()
                    }
                } catch (e: Exception) {
                    onUpdateTriggered()
                }
            }
            else -> {
                if (shouldDispatchEvent(eventType)) {
                    onUpdateTriggered()
                }
            }
        }
    }

    private fun shouldDispatchEvent(eventType: String): Boolean {
        if (config.subscribedEvents.isEmpty()) return true
        return config.subscribedEvents.contains(eventType) || config.subscribedEvents.contains("*")
    }
}
