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
            val languages = (languagesObj as? JsonObject)?.mapNotNull { (k, v) ->
                val cleanKey = k.substringAfter(":")
                val content = (v as? JsonPrimitive)?.content ?: return@mapNotNull null
                cleanKey to content
            }?.toMap().orEmpty()

            val stringArrays = (stringArraysObj as? JsonObject)?.mapNotNull { (k, v) ->
                val cleanKey = k.substringAfter(":")
                val list = (v as? JsonArray)?.mapNotNull { elem ->
                    (elem as? JsonPrimitive)?.content ?: elem.toString().removeSurrounding("\"")
                } ?: return@mapNotNull null
                cleanKey to list
            }?.toMap().orEmpty()

            return LocalLanguage(languages = languages, stringArrays = stringArrays)
        } else {
            val languages = mutableMapOf<String, String>()
            val stringArrays = mutableMapOf<String, List<String>>()

            for ((k, v) in jsonElement) {
                val cleanKey = k.substringAfter(":")
                when (v) {
                    is JsonPrimitive -> {
                        languages[cleanKey] = v.content
                    }
                    is JsonArray -> {
                        stringArrays[cleanKey] = v.mapNotNull { elem ->
                            (elem as? JsonPrimitive)?.content ?: elem.toString().removeSurrounding("\"")
                        }
                    }
                    else -> {}
                }
            }
            return LocalLanguage(languages = languages, stringArrays = stringArrays)
        }
    }
}
