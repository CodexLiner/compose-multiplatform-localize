package com.paragon.localization.utils

import nl.adaptivity.xmlutil.serialization.XML
import com.paragon.localization.models.Resources

/**
 * Parses an XML string into a map of key-value pairs, converting text with HTML tags into CDATA sections.
 */
internal fun parseXmlString(xml: String): Map<String, String> {
    val regex = """<string\s+name="[^"]+">([^<]+(<[^>]+>[^<]*</[^>]+>[^<]*)*)</string>""".toRegex()
    val modifiedString = xml.replace(regex) { matchResult ->
        val originalText = matchResult.groups[1]?.value ?: ""
        // Wrap the content in CDATA if it contains HTML tags
        if (originalText.contains("<") && originalText.contains(">")) {
            "<string name=\"${matchResult.groups[0]?.value?.substringAfter("name=\"")?.substringBefore("\"")}\"><![CDATA[$originalText]]></string>"
        } else matchResult.value
    }

    val decoder = XML {
        autoPolymorphic = true
    }
    val resources = decoder.decodeFromString(Resources.serializer(), modifiedString)
    return resources.string.associate { it.name to it.value }
}
