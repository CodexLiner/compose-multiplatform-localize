package me.meenagopal24.localization.models

import kotlinx.serialization.Serializable
import me.meenagopal24.localization.models.enums.FileType
import me.meenagopal24.localization.utils.parseJsonToLocalLanguage
import me.meenagopal24.localization.utils.parseStringsToLocalLanguage
import me.meenagopal24.localization.utils.parseXmlToLocalLanguage

@ConsistentCopyVisibility
@Serializable(with = LocalLanguageSerializer::class)
data class LocalLanguage internal constructor(
    val languages: Map<String, String> = emptyMap(),
    val stringArrays: Map<String, List<String>> = emptyMap()
) {
    companion object {
        /**
         * Creates an empty [LocalLanguage].
         */
        fun empty(): LocalLanguage = LocalLanguage()

        /**
         * Parses a JSON string (flat or structured) into a [LocalLanguage].
         */
        fun fromJson(jsonString: String): LocalLanguage = parseJsonToLocalLanguage(jsonString)

        /**
         * Parses an XML string into a [LocalLanguage].
         */
        fun fromXml(xmlString: String): LocalLanguage = parseXmlToLocalLanguage(xmlString)

        /**
         * Parses Apple .strings format into a [LocalLanguage].
         */
        fun fromStrings(content: String): LocalLanguage = parseStringsToLocalLanguage(content)

        /**
         * Parses raw content string into a [LocalLanguage] according to [FileType].
         */
        fun parse(content: String, fileType: FileType): LocalLanguage = when (fileType) {
            FileType.JSON -> fromJson(content)
            FileType.XML -> fromXml(content)
            FileType.STRING -> fromStrings(content)
        }
    }
}
