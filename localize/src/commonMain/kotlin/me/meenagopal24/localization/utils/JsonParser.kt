package me.meenagopal24.localization.utils

import kotlinx.serialization.json.Json
import me.meenagopal24.localization.models.LocalLanguage

/**
 * Parses a JSON string into a [LocalLanguage] containing single strings and string arrays using kotlinx.serialization.
 */
fun parseJsonToLocalLanguage(jsonString: String): LocalLanguage {
    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
    return try {
        json.decodeFromString<LocalLanguage>(jsonString)
    } catch (e: Exception) {
        LocalLanguage()
    }
}

/**
 * Parses Apple .strings format into [LocalLanguage].
 */
fun parseStringsToLocalLanguage(content: String): LocalLanguage {
    val map = mutableMapOf<String, String>()
    val regex = """"([^"\\]*(\\.[^"\\]*)*)"\s*=\s*"([^"\\]*(\\.[^"\\]*)*)"\s*;""".toRegex()
    regex.findAll(content).forEach { match ->
        val key = match.groupValues[1].substringAfter(":")
        val value = match.groupValues[3]
        map[key] = value
    }
    return LocalLanguage(languages = map)
}
