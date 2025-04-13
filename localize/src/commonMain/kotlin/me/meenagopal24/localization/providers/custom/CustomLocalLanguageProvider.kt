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
import com.paragon.localization.models.LocalLanguage
import com.paragon.localization.models.LocalLanguageList
import com.paragon.localization.configuration.ProviderConfig
import com.paragon.localization.data.repository.LocalLanguageProviderImpl
import io.ktor.http.isSuccess

/**
 * A custom implementation of the language provider that fetches language data from an external source.
 */
class CustomLocalLanguageProvider(providerConfig: ProviderConfig) :
    LocalLanguageProviderImpl(providerConfig) {

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
        return providerConfig.supportedLanguages.map {
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
        }.get(
            providerConfig.baseURL.orEmpty().plus("${code}.json")
        )
        return if (response.status.isSuccess()) LocalLanguage(response.body<Map<String, String>>())
        else LocalLanguage(mapOf())
    }
}