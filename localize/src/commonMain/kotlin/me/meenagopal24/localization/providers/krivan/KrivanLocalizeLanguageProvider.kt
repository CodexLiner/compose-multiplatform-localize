package me.meenagopal24.localization.providers.krivan

import io.ktor.client.HttpClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.constants.SELECTED_LANGUAGE_MAP
import me.meenagopal24.localization.data.repository.LocalizeLanguageProvider
import me.meenagopal24.localization.local.Localize.settings
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.LocalLanguageList
import me.meenagopal24.localization.models.enums.FileType

/**
 * Official first-party Language Provider for the Krivan / Localize platform.
 *
 * Implements enterprise mobile architectural patterns:
 * 1. Zero-Config URL Resolution: Backend endpoints are encapsulated without requiring user base URLs.
 * 2. Lifecycle-Aware Streaming: Pause/resume methods for foreground/background battery management.
 * 3. Instant In-Memory Key Patching: Sub-50ms hotfix propagation for emergency overrides.
 * 4. Offline-First Resilience: 3-tier fallback hierarchy (Memory -> Local Disk -> Pre-bundled defaults).
 * 5. Typed Sync Observable: Exposes [syncState] Flow for UI indicators and telemetry.
 *
 * @param krivanConfig Configuration containing project credentials and sync preferences.
 * @param providerConfig Provider configuration containing default and supported languages.
 * @param customClient Optional custom [HttpClient] instance.
 */
class KrivanLocalizeLanguageProvider(
    val krivanConfig: KrivanConfig,
    providerConfig: ProviderConfig,
    customClient: HttpClient? = null
) : LocalizeLanguageProvider(providerConfig) {

    /**
     * Minimal constructor requiring only mandatory credentials and language configurations.
     *
     * @param projectId The unique project UUID from your Krivan dashboard.
     * @param apiKey The client read-only API key ("loc_live_...").
     * @param providerConfig The default and supported language codes.
     * @param fileType The format to request and parse (defaults to [FileType.JSON]).
     * @param enableLiveOtaSync When true, automatically syncs changes in real-time. Defaults to true.
     */
    constructor(
        projectId: String,
        apiKey: String,
        providerConfig: ProviderConfig,
        fileType: FileType = FileType.JSON,
        enableLiveOtaSync: Boolean = true
    ) : this(
        krivanConfig = KrivanConfig(
            projectId = projectId,
            apiKey = apiKey,
            fileType = fileType,
            enableLiveOtaSync = enableLiveOtaSync
        ),
        providerConfig = providerConfig
    )

    private val _syncState = MutableStateFlow<KrivanSyncState>(KrivanSyncState.Idle)
    /**
     * Observable stream of real-time synchronization states.
     */
    val syncState: StateFlow<KrivanSyncState> = _syncState.asStateFlow()

    private val parser = KrivanPayloadParser()
    private val patchEngine = KrivanPatchEngine()
    private val fallbackManager = KrivanFallbackManager(krivanConfig.preBundledDefaults)
    private val apiClient = KrivanApiClient(krivanConfig, parser, customClient)

    private val liveSyncEngine = KrivanLiveSyncEngine(
        config = krivanConfig,
        httpClient = apiClient.httpClient,
        onUpdateTriggered = { forceUpdateLanguages() },
        onPatchReceived = { key, value, action ->
            applyInstantPatch(key, value, action)
        },
        onStateChanged = { state ->
            _syncState.value = state
        }
    )

    init {
        if (krivanConfig.enableLiveOtaSync) {
            liveSyncEngine.start()
        }
    }

    /**
     * Retrieves the language data for the given [code] from Krivan.
     * Leverages HTTP ETag 304 validation and offline fallback hierarchy.
     */
    override suspend fun getLanguage(code: String): LocalLanguage {
        _syncState.value = KrivanSyncState.Fetching(code)

        val etagKey = "${KrivanConstants.ETAG_STORAGE_PREFIX}${krivanConfig.projectId}_$code"
        val cachedEtag = if (krivanConfig.enableEtagCaching) settings.getStringOrNull(etagKey) else null

        val localStored = settings.getStringOrNull(code.plus(SELECTED_LANGUAGE_MAP))
        val cachedDiskLanguage = if (!localStored.isNullOrBlank()) parser.parseCachedJson(localStored) else null

        return when (val result = apiClient.fetchTranslations(code, cachedEtag)) {
            is KrivanApiClient.FetchResult.Success -> {
                if (krivanConfig.enableEtagCaching && !result.newEtag.isNullOrBlank()) {
                    settings.putString(etagKey, result.newEtag)
                }
                _syncState.value = KrivanSyncState.Success(code, fromCache = false)
                result.language
            }
            is KrivanApiClient.FetchResult.NotModified -> {
                _syncState.value = KrivanSyncState.Success(code, fromCache = true)
                fallbackManager.resolveOfflineCopy(code, cachedDiskLanguage)
            }
            is KrivanApiClient.FetchResult.Failure -> {
                _syncState.value = KrivanSyncState.Error(
                    message = "Failed to fetch $code: ${result.cause.message}",
                    cause = result.cause
                )
                fallbackManager.resolveOfflineCopy(code, cachedDiskLanguage)
            }
        }
    }

    /**
     * Retrieves all configured [ProviderConfig.supportedLanguages] in parallel.
     */
    override suspend fun getLanguages(): List<LocalLanguageList> = coroutineScope {
        config.supportedLanguages.map { code ->
            async { LocalLanguageList(code, getLanguage(code)) }
        }.awaitAll()
    }

    /**
     * Applies an instant in-memory patch for hotfix overrides without downloading the whole bundle.
     */
    private fun applyInstantPatch(key: String, value: String?, action: String) {
        val currentMap = localStringsState.value
        val currentLanguage = LocalLanguage(languages = currentMap)
        val patched = patchEngine.applyKeyPatch(currentLanguage, key, value, action)

        localStringsState.value = patched.languages
        _syncState.value = KrivanSyncState.PatchApplied(key, action)
    }

    /**
     * Lifecycle hook: Call when the host application transitions to the background.
     * Pauses the SSE stream to eliminate background battery drain and comply with OS policies.
     */
    fun onAppBackgrounded() {
        liveSyncEngine.pause()
    }

    /**
     * Lifecycle hook: Call when the host application returns to the foreground.
     * Re-establishes the SSE stream and validates active version cache freshness.
     */
    fun onAppForegrounded() {
        liveSyncEngine.resume()
        forceUpdateLanguages()
    }

    /**
     * Manually starts the background Live OTA Sync streaming connection.
     */
    fun startLiveOtaSync() {
        liveSyncEngine.start()
    }

    /**
     * Manually stops the background Live OTA Sync streaming connection.
     */
    fun stopLiveOtaSync() {
        liveSyncEngine.stop()
    }
}
