package me.meenagopal24.localization.providers.custom

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.LocalLanguageList
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.data.repository.LocalizeLanguageProvider
import io.ktor.http.isSuccess

/**
 * A custom implementation of the language provider that fetches language data from an external source.
 */
class CustomLocalizeLanguageProvider(val baseURL: String, providerConfig: ProviderConfig) :
    LocalizeLanguageProvider(providerConfig) {

    /**
     * Retrieves the language data for a given language code by downloading the corresponding JSON.
     */
    override suspend fun getLanguage(code: String): LocalLanguage {
        return downloadLanguage(code)
    }

    /**
     * Retrieves the list of supported languages by downloading the corresponding language data for each.
     */
    override suspend fun getLanguages(): List<LocalLanguageList> {
        return config.supportedLanguages.map {
            LocalLanguageList(it, downloadLanguage(it))
        }
    }

    /**
     * Downloads language data for a specific language code from the configured base URL.
     */
    private suspend fun downloadLanguage(code: String): LocalLanguage {
        val response = HttpClient {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }.get(baseURL.plus("${code}.json"))
        return if (response.status.isSuccess()) LocalLanguage(response.body<Map<String, String>>())
        else LocalLanguage(mapOf())
    }
}