package me.meenagopal24.localization.utils

import nl.adaptivity.xmlutil.serialization.XML
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.Resources

private val StringXmlRegex = Regex("""<string\s+name="[^"]+">([^<]+(<[^>]+>[^<]*</[^>]+>[^<]*)*)</string>""")
private val ItemXmlRegex = Regex("""<item>([^<]+(<[^>]+>[^<]*</[^>]+>[^<]*)*)</item>""")
private val SharedXmlDecoder = XML { autoPolymorphic = true }

/**
 * Parses an XML string into a [LocalLanguage] containing single strings and string arrays,
 * converting text with HTML tags into CDATA sections.
 */
fun parseXmlToLocalLanguage(xml: String): LocalLanguage {
    var modifiedString = xml.replace(StringXmlRegex) { matchResult ->
        val originalText = matchResult.groups[1]?.value ?: ""
        if (originalText.contains("<") && originalText.contains(">")) {
            val name = matchResult.value.substringAfter("name=\"").substringBefore("\"")
            "<string name=\"$name\"><![CDATA[$originalText]]></string>"
        } else matchResult.value
    }

    modifiedString = modifiedString.replace(ItemXmlRegex) { matchResult ->
        val originalText = matchResult.groups[1]?.value ?: ""
        if (originalText.contains("<") && originalText.contains(">")) {
            "<item><![CDATA[$originalText]]></item>"
        } else matchResult.value
    }

    val resources = SharedXmlDecoder.decodeFromString(Resources.serializer(), modifiedString)
    val stringsMap = resources.string.associate {
        val idx = it.name.indexOf(':')
        val cleanKey = if (idx == -1) it.name else it.name.substring(idx + 1)
        cleanKey to it.value
    }
    val stringArraysMap = resources.stringArray.associate { array ->
        val idx = array.name.indexOf(':')
        val cleanKey = if (idx == -1) array.name else array.name.substring(idx + 1)
        cleanKey to array.item.map { it.value }
    }
    return LocalLanguage(languages = stringsMap, stringArrays = stringArraysMap)
}

/**
 * Parses an XML string into a map of key-value pairs, converting text with HTML tags into CDATA sections.
 */
fun parseXmlString(xml: String): Map<String, String> {
    return parseXmlToLocalLanguage(xml).languages
}
