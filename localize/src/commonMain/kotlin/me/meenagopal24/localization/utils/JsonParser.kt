package me.meenagopal24.localization.utils

import kotlinx.serialization.json.Json
import me.meenagopal24.localization.models.LocalLanguage

/**
 * Shared singleton [Json] instance for parsing localization payloads.
 * Avoids repeated descriptor creation and serializer registry allocations.
 */
internal val SharedLocalizeJson = Json {
    prettyPrint = false
    isLenient = true
    ignoreUnknownKeys = true
}

private val AppleStringsRegex = Regex(""""([^"\\]*(\\.[^"\\]*)*)"\s*=\s*"([^"\\]*(\\.[^"\\]*)*)"\s*;""")

/**
 * Parses a JSON string into a [LocalLanguage] containing single strings and string arrays using kotlinx.serialization.
 */
fun parseJsonToLocalLanguage(jsonString: String): LocalLanguage {
    return try {
        SharedLocalizeJson.decodeFromString<LocalLanguage>(jsonString)
    } catch (e: Exception) {
        LocalLanguage()
    }
}

/**
 * Parses Apple .strings format into [LocalLanguage].
 */
fun parseStringsToLocalLanguage(content: String): LocalLanguage {
    val map = mutableMapOf<String, String>()
    AppleStringsRegex.findAll(content).forEach { match ->
        val rawKey = match.groupValues[1]
        val idx = rawKey.indexOf(':')
        val key = if (idx == -1) rawKey else rawKey.substring(idx + 1)
        val value = match.groupValues[3]
        map[key] = value
    }
    return LocalLanguage(languages = map)
}
