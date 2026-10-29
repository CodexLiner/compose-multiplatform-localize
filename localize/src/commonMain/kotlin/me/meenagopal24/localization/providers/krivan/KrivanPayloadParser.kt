package me.meenagopal24.localization.providers.krivan

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.enums.FileType
import me.meenagopal24.localization.utils.SharedLocalizeJson

/**
 * Encapsulated parser for handling translation payloads from the Krivan backend.
 */
internal class KrivanPayloadParser(
    private val json: Json = SharedLocalizeJson
) {
    /**
     * Parses the raw network response body into a [LocalLanguage] domain model according to [FileType].
     */
    fun parse(bodyString: String, fileType: FileType): LocalLanguage {
        if (bodyString.isBlank()) return LocalLanguage()

        return when (fileType) {
            FileType.XML -> LocalLanguage.fromXml(bodyString)
            FileType.STRING -> LocalLanguage.fromStrings(bodyString)
            FileType.JSON -> parseJsonPayload(bodyString)
        }
    }

    /**
     * Parses JSON responses, supporting both flat key-value pairs and wrapped `{ "translations": { ... } }` envelopes.
     */
    private fun parseJsonPayload(bodyString: String): LocalLanguage {
        return try {
            val element = json.parseToJsonElement(bodyString)
            if (element is JsonObject && element.containsKey("translations") && element["translations"] is JsonObject) {
                val translationsObj = element["translations"] as JsonObject
                val map = HashMap<String, String>(translationsObj.size)
                for ((k, v) in translationsObj) {
                    if (v is JsonPrimitive) {
                        val idx = k.indexOf(':')
                        val cleanKey = if (idx == -1) k else k.substring(idx + 1)
                        map[cleanKey] = v.content
                    }
                }
                LocalLanguage(languages = map)
            } else {
                LocalLanguage.fromJson(bodyString)
            }
        } catch (e: Exception) {
            LocalLanguage.fromJson(bodyString)
        }
    }

    /**
     * Deserializes a stored JSON string from local persistent storage.
     */
    fun parseCachedJson(cachedJson: String): LocalLanguage {
        return try {
            json.decodeFromString<LocalLanguage>(cachedJson)
        } catch (e: Exception) {
            LocalLanguage()
        }
    }
}

