package me.meenagopal24.localization.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object LocalLanguageSerializer : KSerializer<LocalLanguage> {
    private val delegateSerializer = LocalLanguageSurrogate.serializer()

    @Serializable
    private data class LocalLanguageSurrogate(
        val languages: Map<String, String> = emptyMap(),
        val stringArrays: Map<String, List<String>> = emptyMap()
    )

    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: LocalLanguage) {
        val surrogate = LocalLanguageSurrogate(
            languages = value.languages,
            stringArrays = value.stringArrays
        )
        encoder.encodeSerializableValue(delegateSerializer, surrogate)
    }

    override fun deserialize(decoder: Decoder): LocalLanguage {
        val jsonInput = decoder as? JsonDecoder
            ?: return decoder.decodeSerializableValue(delegateSerializer).let {
                LocalLanguage(it.languages, it.stringArrays)
            }

        val jsonElement = jsonInput.decodeJsonElement()
        if (jsonElement !is JsonObject) return LocalLanguage()

        val languagesObj = jsonElement["languages"]
        val stringArraysObj = jsonElement["stringArrays"]

        if (languagesObj is JsonObject || stringArraysObj is JsonObject) {
            val languages = if (languagesObj is JsonObject) {
                val map = HashMap<String, String>(languagesObj.size)
                for ((k, v) in languagesObj) {
                    val content = (v as? JsonPrimitive)?.content
                    if (content != null) {
                        val idx = k.indexOf(':')
                        val cleanKey = if (idx == -1) k else k.substring(idx + 1)
                        map[cleanKey] = content
                    }
                }
                map
            } else emptyMap()

            val stringArrays = if (stringArraysObj is JsonObject) {
                val map = HashMap<String, List<String>>(stringArraysObj.size)
                for ((k, v) in stringArraysObj) {
                    if (v is JsonArray) {
                        val list = ArrayList<String>(v.size)
                        for (elem in v) {
                            val content = (elem as? JsonPrimitive)?.content ?: elem.toString().removeSurrounding("\"")
                            list.add(content)
                        }
                        val idx = k.indexOf(':')
                        val cleanKey = if (idx == -1) k else k.substring(idx + 1)
                        map[cleanKey] = list
                    }
                }
                map
            } else emptyMap()

            return LocalLanguage(languages = languages, stringArrays = stringArrays)
        } else {
            val languages = HashMap<String, String>(jsonElement.size)
            val stringArrays = HashMap<String, List<String>>()

            for ((k, v) in jsonElement) {
                val idx = k.indexOf(':')
                val cleanKey = if (idx == -1) k else k.substring(idx + 1)
                when (v) {
                    is JsonPrimitive -> {
                        languages[cleanKey] = v.content
                    }
                    is JsonArray -> {
                        val list = ArrayList<String>(v.size)
                        for (elem in v) {
                            val content = (elem as? JsonPrimitive)?.content ?: elem.toString().removeSurrounding("\"")
                            list.add(content)
                        }
                        stringArrays[cleanKey] = list
                    }
                    else -> {}
                }
            }
            return LocalLanguage(languages = languages, stringArrays = stringArrays)
        }
    }
}
