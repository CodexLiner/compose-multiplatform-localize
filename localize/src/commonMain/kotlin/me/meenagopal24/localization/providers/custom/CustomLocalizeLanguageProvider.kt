package me.meenagopal24.localization.providers.custom

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.data.repository.LocalizeLanguageProvider
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.LocalLanguageList

/**
 * A custom implementation of the language provider that fetches language data from an external source.
 *
 * @param baseURL The base URL to fetch language JSON or XML files from.
 * @param providerConfig The configuration for supported/default languages.
 * @param contentType The expected ContentType for response body parsing (defaults to ContentType.Any).
 * @param client Optional custom [HttpClient] instance to use for network requests.
 */
class CustomLocalizeLanguageProvider(
    val baseURL: String,
    providerConfig: ProviderConfig,
    val contentType: ContentType = ContentType.Any,
    client: HttpClient? = null
) : LocalizeLanguageProvider(providerConfig) {

    private val httpClient: HttpClient = client ?: HttpClient {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            val jsonConfig = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
            json(jsonConfig)
            if (contentType != ContentType.Application.Json) {
                json(jsonConfig, contentType = contentType)
            }
        }
    }

    /**
     * Retrieves the language data for a given language code by downloading the corresponding file.
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
     * Downloads language data for a specific language code from the configured base URL.
     */
    private suspend fun downloadLanguage(code: String): LocalLanguage {
        val url = if (baseURL.endsWith("/")) "$baseURL$code" else "$baseURL/$code"
        val response = try {
            httpClient.get(url)
        } catch (e: Exception) {
            return LocalLanguage()
        }

        if (!response.status.isSuccess()) return LocalLanguage()

        val bodyString = response.body<String>()
        return if (bodyString.trimStart().startsWith("<")) {
            LocalLanguage.fromXml(bodyString)
        } else {
            LocalLanguage.fromJson(bodyString)
        }
    }
}
