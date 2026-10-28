package me.meenagopal24.localization.providers.krivan

import me.meenagopal24.localization.models.enums.FileType

/**
 * Caller configuration for the first-party Krivan Localization provider.
 *
 * Designed with strict encapsulation: users only need to supply their project credentials
 * (`projectId` and `apiKey`). The backend endpoints and environments are encapsulated.
 *
 * @property projectId The unique UUID identifier of your project from the Krivan dashboard. Required.
 * @property apiKey The read-only client API key ("loc_live_..."). Required.
 * @property fileType The resource format to request and parse (defaults to [FileType.JSON]).
 * @property enableLiveOtaSync Automatically opens a lightweight, battery-efficient Server-Sent Events (SSE)
 *                             foreground stream to invalidate and hot-reload localized strings in real-time. Defaults to true.
 * @property subscribedEvents Specific events to listen for (defaults to version publish, activation, and overrides).
 * @property environment Target infrastructure tier (defaults to [KrivanEnvironment.PRODUCTION]).
 * @property enableEtagCaching Whether to send `If-None-Match` and leverage HTTP 304 Not Modified caching. Defaults to true.
 * @property namespace Optional namespace to filter translations by.
 * @property autoReconnect Whether to automatically re-establish the live sync stream if the network drops. Defaults to true.
 * @property reconnectDelayMs Reconnection backoff interval in milliseconds. Defaults to 5000ms.
 */
data class KrivanConfig(
    val projectId: String,
    val apiKey: String,
    val fileType: FileType = FileType.JSON,
    val enableLiveOtaSync: Boolean = true,
    val subscribedEvents: List<String> = KrivanConstants.DEFAULT_SUBSCRIBED_EVENTS,
    val environment: KrivanEnvironment = KrivanEnvironment.PRODUCTION,
    val enableEtagCaching: Boolean = true,
    val namespace: String? = null,
    val autoReconnect: Boolean = true,
    val reconnectDelayMs: Long = 5000L
) {
    init {
        require(projectId.isNotBlank()) { "Krivan: `projectId` must not be blank." }
        require(apiKey.isNotBlank()) { "Krivan: `apiKey` must not be blank." }
    }

    internal val effectiveBaseUrl: String
        get() = environment.baseUrl.trimEnd('/')
}
