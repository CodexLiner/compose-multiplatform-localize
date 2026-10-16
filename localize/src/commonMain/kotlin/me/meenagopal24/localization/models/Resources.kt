package me.meenagopal24.localization.models

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("resources", namespace = "", prefix = "")
data class Resources(
    @XmlElement(true) val string: List<StringResource> = emptyList(),
    @XmlElement(true)
    @XmlSerialName("string-array", namespace = "", prefix = "")
    val stringArray: List<StringArrayResource> = emptyList()
)

@Serializable
@XmlSerialName("string", namespace = "", prefix = "")
data class StringResource(
    @XmlValue(true) val value: String = "",
    val name: String = ""
)

@Serializable
@XmlSerialName("string-array", namespace = "", prefix = "")
data class StringArrayResource(
    val name: String = "",
    @XmlElement(true)
    @XmlSerialName("item", namespace = "", prefix = "")
    val item: List<ItemResource> = emptyList()
)

@Serializable
@XmlSerialName("item", namespace = "", prefix = "")
data class ItemResource(
    @XmlValue(true) val value: String = ""
)
