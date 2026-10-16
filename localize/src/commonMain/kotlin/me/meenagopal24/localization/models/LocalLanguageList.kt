package me.meenagopal24.localization.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalLanguageList(val name: String, val language: LocalLanguage)
