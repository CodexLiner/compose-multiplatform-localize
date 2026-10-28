package me.meenagopal24.localization.providers.krivan

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.enums.FileType

/**
 * Encapsulated network client for interacting with the Krivan backend delivery endpoints.
 */
internal class KrivanApiClient(
    private val config: KrivanConfig,
    private val parser: KrivanPayloadParser,
    customClient: HttpClient? = null
) {
    internal val httpClient: HttpClient = customClient ?: HttpClient {
        install(ContentNegotiation) {
            val jsonConfig = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
            json(jsonConfig)
            json(jsonConfig, contentType = ContentType.Text.Plain)
            json(jsonConfig, contentType = ContentType.Application.Json)
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }
        install(DefaultRequest) {
            headers {
                append(KrivanConstants.HEADER_API_KEY, config.apiKey)
                append(KrivanConstants.HEADER_ACCEPT, "application/json, text/xml, */*")
            }
        }
    }

    /**
     * Represents the outcome of a language fetch request.
     */
    sealed interface FetchResult {
        data class Success(val language: LocalLanguage, val newEtag: String?) : FetchResult
        object NotModified : FetchResult
        data class Failure(val cause: Exception) : FetchResult
    }

    /**
     * Fetches language translations from the backend with ETag 304 validation.
     */
    suspend fun fetchTranslations(code: String, cachedEtag: String?): FetchResult {
        val endpoint = when (config.fileType) {
            FileType.XML, FileType.STRING -> "${config.effectiveBaseUrl}${KrivanConstants.buildExportPath(config.projectId)}"
            FileType.JSON -> "${config.effectiveBaseUrl}${KrivanConstants.buildTranslationsPath(config.projectId)}"
        }

        return try {
            val response = httpClient.get(endpoint) {
                parameter("locale", code)

                when (config.fileType) {
                    FileType.XML -> parameter("format", "android_xml")
                    FileType.STRING -> parameter("format", "strings")
                    FileType.JSON -> parameter("format", "flat")
                }

                config.namespace?.let { parameter("namespace", it) }

                if (config.enableEtagCaching && !cachedEtag.isNullOrBlank()) {
                    header(KrivanConstants.HEADER_IF_NONE_MATCH, cachedEtag)
                }
            }

            if (response.status == HttpStatusCode.NotModified) {
                return FetchResult.NotModified
            }

            if (!response.status.isSuccess()) {
                return FetchResult.Failure(IllegalStateException("HTTP ${response.status.value}: ${response.status.description}"))
            }

            val newEtag = response.headers[KrivanConstants.HEADER_ETAG]
            val bodyString = response.body<String>()
            val parsed = parser.parse(bodyString, config.fileType)

            FetchResult.Success(parsed, newEtag)
        } catch (e: Exception) {
            FetchResult.Failure(e)
        }
    }
}

