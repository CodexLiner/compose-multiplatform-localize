package me.meenagopal24.localization.providers.krivan

import io.ktor.client.HttpClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
 * Designed with strict encapsulation so callers only need to provide their project
 * credentials (`projectId` and `apiKey`). All networking, HTTP 304 ETag caching,
 * format parsing, and real-time Server-Sent Events (SSE) stream synchronization are handled
 * automatically under the hood.
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

    private val parser = KrivanPayloadParser()
    private val apiClient = KrivanApiClient(krivanConfig, parser, customClient)
    private val liveSyncEngine = KrivanLiveSyncEngine(
        config = krivanConfig,
        httpClient = apiClient.httpClient,
        onUpdateTriggered = { forceUpdateLanguages() }
    )

    init {
        if (krivanConfig.enableLiveOtaSync) {
            liveSyncEngine.start()
        }
    }

    /**
     * Retrieves the language data for the given [code] from Krivan.
     * Leverages HTTP ETag 304 validation to eliminate redundant data transfer.
     */
    override suspend fun getLanguage(code: String): LocalLanguage {
        val etagKey = "${KrivanConstants.ETAG_STORAGE_PREFIX}${krivanConfig.projectId}_$code"
        val cachedEtag = if (krivanConfig.enableEtagCaching) settings.getStringOrNull(etagKey) else null

        return when (val result = apiClient.fetchTranslations(code, cachedEtag)) {
            is KrivanApiClient.FetchResult.Success -> {
                if (krivanConfig.enableEtagCaching && !result.newEtag.isNullOrBlank()) {
                    settings.putString(etagKey, result.newEtag)
                }
                result.language
            }
            is KrivanApiClient.FetchResult.NotModified -> {
                // HTTP 304: Content is identical to cache; deserialize from local storage
                val localStored = settings.getStringOrNull(code.plus(SELECTED_LANGUAGE_MAP))
                if (!localStored.isNullOrBlank()) {
                    parser.parseCachedJson(localStored)
                } else {
                    LocalLanguage()
                }
            }
            is KrivanApiClient.FetchResult.Failure -> {
                // Network error or unexpected response: gracefully fallback to empty
                LocalLanguage()
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
