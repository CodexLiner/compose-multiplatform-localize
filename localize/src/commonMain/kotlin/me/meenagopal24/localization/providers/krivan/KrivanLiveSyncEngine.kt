package me.meenagopal24.localization.providers.krivan

import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Encapsulated background subscriber for Krivan's Server-Sent Events (SSE) stream.
 *
 * Maintains a battery-efficient HTTP/2 real-time connection. Whenever translation releases
 * or instant key overrides are saved in the Krivan dashboard, invokes [onUpdateTriggered].
 */
internal class KrivanLiveSyncEngine(
    private val config: KrivanConfig,
    private val httpClient: HttpClient,
    private val onUpdateTriggered: () -> Unit
) {
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var syncJob: Job? = null

    /**
     * Starts the background SSE streaming connection if not already running.
     */
    fun start() {
        if (syncJob?.isActive == true) return

        syncJob = syncScope.launch {
            val eventsParam = config.subscribedEvents.joinToString(",")
            val streamPath = KrivanConstants.buildStreamPath(config.projectId)
            val streamUrl = "${config.effectiveBaseUrl}$streamPath?apiKey=${config.apiKey}&events=$eventsParam"

            while (isActive) {
                try {
                    httpClient.prepareGet(streamUrl) {
                        headers {
                            append(KrivanConstants.HEADER_ACCEPT, "text/event-stream")
                            append(KrivanConstants.HEADER_CACHE_CONTROL, "no-cache")
                        }
                    }.execute { response ->
                        if (!response.status.isSuccess()) {
                            return@execute
                        }

                        val channel = response.bodyAsChannel()
                        var currentEvent: String? = null

                        while (!channel.isClosedForRead && isActive) {
                            val line = channel.readUTF8Line() ?: break
                            val trimmed = line.trim()

                            // Skip empty lines and keep-alive ping comments (": ping")
                            if (trimmed.isEmpty() || trimmed.startsWith(KrivanConstants.SSE_PING_PREFIX)) {
                                continue
                            }

                            if (trimmed.startsWith(KrivanConstants.SSE_EVENT_PREFIX)) {
                                currentEvent = trimmed.removePrefix(KrivanConstants.SSE_EVENT_PREFIX).trim()
                            } else if (trimmed.startsWith(KrivanConstants.SSE_DATA_PREFIX)) {
                                val eventType = currentEvent ?: "message"
                                if (shouldDispatchEvent(eventType)) {
                                    onUpdateTriggered()
                                }
                                currentEvent = null
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    break
                } catch (e: Exception) {
                    // Suppress network drops; the loop handles reconnection
                }

                if (!config.autoReconnect || !isActive) {
                    break
                }

                delay(config.reconnectDelayMs)
            }
        }
    }

    /**
     * Stops the streaming connection and cancels the background coroutine job.
     */
    fun stop() {
        syncJob?.cancel()
        syncJob = null
    }

    private fun shouldDispatchEvent(eventType: String): Boolean {
        if (config.subscribedEvents.isEmpty()) return true
        return config.subscribedEvents.contains(eventType) || config.subscribedEvents.contains("*")
    }
}

