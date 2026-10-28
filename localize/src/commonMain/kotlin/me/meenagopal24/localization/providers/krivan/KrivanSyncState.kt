package me.meenagopal24.localization.providers.krivan

/**
 * Real-time synchronization state emitted by [KrivanLocalizeLanguageProvider].
 */
sealed interface KrivanSyncState {
    /**
     * Engine is idle and waiting.
     */
    data object Idle : KrivanSyncState

    /**
     * Currently downloading translations for [languageCode].
     */
    data class Fetching(val languageCode: String) : KrivanSyncState

    /**
     * Translations successfully loaded.
     * @property languageCode The language code that was loaded.
     * @property fromCache Whether the data was resolved via HTTP 304 / local cache.
     */
    data class Success(val languageCode: String, val fromCache: Boolean) : KrivanSyncState

    /**
     * The background SSE real-time stream is active and connected.
     * @property permittedEvents Events configured for this project.
     */
    data class LiveConnected(val permittedEvents: List<String>) : KrivanSyncState

    /**
     * An instant patch (hotfix override) was applied directly in-memory.
     * @property key The translation key patched.
     * @property action The mutation action (create, update, delete).
     */
    data class PatchApplied(val key: String, val action: String) : KrivanSyncState

    /**
     * An error occurred during fetching or streaming.
     * @property message The error message.
     * @property cause The underlying exception, if available.
     */
    data class Error(val message: String, val cause: Throwable? = null) : KrivanSyncState
}

