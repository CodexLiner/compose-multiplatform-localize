package me.meenagopal24.localization.providers.tolgee

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.StringValues
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.data.repository.LocalizeLanguageProvider
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.LocalLanguageList

/**
 * A custom implementation of the language provider for fetching language data from the Tolgee API.
 *
 * @param tolgeeConfig Configuration containing Tolgee API key, project ID, and base URL.
 * @param providerConfig Configuration for supported and default languages.
 * @param client Optional custom [HttpClient] instance to use for network requests.
 */
class TolgeeLocalizeLanguageProvider(
    val tolgeeConfig: TolgeeConfig,
    providerConfig: ProviderConfig,
    client: HttpClient? = null
) : LocalizeLanguageProvider(providerConfig) {

    private val httpClient: HttpClient = client ?: HttpClient {
        install(ContentNegotiation) {
            val jsonConfig = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
            json(jsonConfig)
            json(jsonConfig, contentType = ContentType.Text.Plain)
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(DefaultRequest) {
            headers {
                append("X-API-Key", tolgeeConfig.apiKey)
                append("Accept", "application/json")
            }
        }
    }

    /**
     * Retrieves the language data for a specific language code from Tolgee.
     */
    override suspend fun getLanguage(code: String): LocalLanguage {
        return downloadLanguage(code)
    }

    /**
     * Retrieves the list of supported languages concurrently in parallel.
     */
    override suspend fun getLanguages(): List<LocalLanguageList> = coroutineScope {
        config.supportedLanguages.map { code ->
            async { LocalLanguageList(code, downloadLanguage(code)) }
        }.awaitAll()
    }

    /**
     * Downloads the language data for a given code from Tolgee and returns it as a [LocalLanguage].
     */
    private suspend fun downloadLanguage(code: String): LocalLanguage {
        val baseUrl = tolgeeConfig.baseURL.orEmpty()
        val pathSegments = if (baseUrl.endsWith("/")) {
            "${baseUrl}v2/projects/${tolgeeConfig.appName}/export"
        } else {
            "${baseUrl}/v2/projects/${tolgeeConfig.appName}/export"
        }

        val result = try {
            httpClient.get(pathSegments) {
                url.parameters.apply {
                    appendAll(StringValues.build {
                        append("appName", tolgeeConfig.appName)
                        append("languages", code)
                        append("zip", "false")
                        append("projectId", tolgeeConfig.projectId.orEmpty())
                        append("format", tolgeeConfig.fileType.type)
                    })
                }
            }
        } catch (e: Exception) {
            return LocalLanguage()
        }

        if (!result.status.isSuccess()) return LocalLanguage()

        val bodyString = result.body<String>()
        return LocalLanguage.parse(bodyString, tolgeeConfig.fileType)
    }
}
