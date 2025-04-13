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
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.StringValues
import kotlinx.serialization.json.Json
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.LocalLanguageList
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.data.repository.LocalizeLanguageProvider
import io.ktor.http.isSuccess

/**
 * A custom implementation of the language provider for fetching language data from the Tolgee API.
 */
class TolgeeLocalizeLanguageProvider(val tolgeeConfig: TolgeeConfig, providerConfig: ProviderConfig) :
    LocalizeLanguageProvider(providerConfig) {

    /**
     * Retrieves the language data for a specific language code from Tolgee.
     */
    override suspend fun getLanguage(code: String): LocalLanguage {
        return LocalLanguage(downloadLanguage(code))
    }

    /**
     * Retrieves the list of supported languages by downloading their data from Tolgee.
     */
    override suspend fun getLanguages(): List<LocalLanguageList?> {
        return providerConfig.supportedLanguages.map {
            LocalLanguageList(it, LocalLanguage(downloadLanguage(it)))
        }
    }

    /**
     * Downloads the language data for a given code from Tolgee and returns it as a map of key-value pairs.
     */
    private suspend fun downloadLanguage(code: String): Map<String, String> {
        val pathSegments =
            tolgeeConfig.baseURL.plus("v2/projects/").plus(providerConfig.appName).plus("/export")
        val result = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
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
        }.get(pathSegments) {
            url.parameters.apply {
                appendAll(StringValues.build {
                    append("appName", providerConfig.appName)
                    append("languages", code)
                    append("zip", "false")
                    append("projectId", providerConfig.projectId.orEmpty())
                    append("format", providerConfig.fileType.type)
                })
            }
        }

        return if (result.status.isSuccess()) me.meenagopal24.localization.utils.parseXmlString(result.body<String>()) else mapOf()
    }
}