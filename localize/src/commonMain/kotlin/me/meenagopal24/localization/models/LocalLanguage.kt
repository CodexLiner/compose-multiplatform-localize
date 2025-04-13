package me.meenagopal24.localization.models

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class LocalLanguageList(val name: String, val language: LocalLanguage)

@Serializable
data class LocalLanguage(val languages: Map<String, String>)

@Serializable
@XmlSerialName("resources", namespace = "", prefix = "")
data class Resources(@XmlElement(true) val string: List<StringResource>)

@Serializable
@XmlSerialName("string", namespace = "", prefix = "")
data class StringResource(@XmlValue(true) val value: String, val name: String)

