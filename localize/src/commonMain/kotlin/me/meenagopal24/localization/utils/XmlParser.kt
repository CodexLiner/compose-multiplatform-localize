package me.meenagopal24.localization.utils

import nl.adaptivity.xmlutil.serialization.XML
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.Resources

/**
 * Parses an XML string into a [LocalLanguage] containing single strings and string arrays,
 * converting text with HTML tags into CDATA sections.
 */
fun parseXmlToLocalLanguage(xml: String): LocalLanguage {
    val stringRegex = """<string\s+name="[^"]+">([^<]+(<[^>]+>[^<]*</[^>]+>[^<]*)*)</string>""".toRegex()
    var modifiedString = xml.replace(stringRegex) { matchResult ->
        val originalText = matchResult.groups[1]?.value ?: ""
        if (originalText.contains("<") && originalText.contains(">")) {
            val name = matchResult.value.substringAfter("name=\"").substringBefore("\"")
            "<string name=\"$name\"><![CDATA[$originalText]]></string>"
        } else matchResult.value
    }

    val itemRegex = """<item>([^<]+(<[^>]+>[^<]*</[^>]+>[^<]*)*)</item>""".toRegex()
    modifiedString = modifiedString.replace(itemRegex) { matchResult ->
        val originalText = matchResult.groups[1]?.value ?: ""
        if (originalText.contains("<") && originalText.contains(">")) {
            "<item><![CDATA[$originalText]]></item>"
        } else matchResult.value
    }

    val decoder = XML {
        autoPolymorphic = true
    }
    val resources = decoder.decodeFromString(Resources.serializer(), modifiedString)
    val stringsMap = resources.string.associate { it.name.substringAfter(":") to it.value }
    val stringArraysMap = resources.stringArray.associate { array ->
        array.name.substringAfter(":") to array.item.map { it.value }
    }
    return LocalLanguage(languages = stringsMap, stringArrays = stringArraysMap)
}

/**
 * Parses an XML string into a map of key-value pairs, converting text with HTML tags into CDATA sections.
 */
fun parseXmlString(xml: String): Map<String, String> {
    return parseXmlToLocalLanguage(xml).languages
}
