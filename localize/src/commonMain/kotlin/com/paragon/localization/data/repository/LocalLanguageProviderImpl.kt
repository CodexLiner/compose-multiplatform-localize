package com.paragon.localization.data.repository

import com.paragon.localization.configuration.ProviderConfig
import com.paragon.localization.constants.SELECTED_LANGUAGE_CODE
import com.paragon.localization.constants.SELECTED_LANGUAGE_MAP
import com.paragon.localization.local.Localize.settings
import com.paragon.localization.models.LocalLanguage
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class LocalLanguageProviderImpl(val providerConfig: ProviderConfig) : LocalLanguageProvider {

    /**
     * Holds the current localized strings for the selected language.
     */
    override var localStringsState: MutableStateFlow<Map<String, String>> =
        MutableStateFlow(mapOf())

    /**
     * Retrieves the current language code from local storage.
     */
    override fun getCurrentLanguage(): String {
        return settings.get<String>(SELECTED_LANGUAGE_CODE).orEmpty()
    }

    /**
     * Sets the current language and updates the local language strings in storage.
     */
    override fun setCurrentLanguage(code: String) {
        runBlocking {
            val strings = getLanguageFromLocalStorage(code) ?: getLanguage(code)?.languages
            strings?.let {
                if (strings.isNotEmpty()) {
                    settings[SELECTED_LANGUAGE_CODE] = code
                    saveLanguageToLocalStorage(code, strings)
                    localStringsState.value = strings
                }
            }
        }
    }

    /**
     * Forces a refresh of all available languages and updates local storage.
     */
    override fun forceUpdateLanguages() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentLanguage = getCurrentLanguage()
            val allLanguages = getLanguages().filterNotNull()
            val selectedLanguage =
                allLanguages.find { it.name == currentLanguage }?.language?.languages
            allLanguages.forEach { (name, language) ->
                if (language.languages.isNotEmpty()) {
                    saveLanguageToLocalStorage(name.plus(SELECTED_LANGUAGE_MAP), language.languages)
                }
            }
            selectedLanguage?.let {
                localStringsState.value = selectedLanguage
            }
        }
    }

    /**
     * Fetches the language strings for a given language code from local storage.
     */
    private fun getLanguageFromLocalStorage(code: String): Map<String, String>? {
        return settings.get<String>(code.plus(SELECTED_LANGUAGE_MAP))
            ?.let { Json.decodeFromString<LocalLanguage>(it).languages }
    }

    /**
     * Saves the language strings for a given language code to local storage.
     */
    private fun saveLanguageToLocalStorage(code: String, strings: Map<String, String>) {
        settings[code.plus(SELECTED_LANGUAGE_MAP)] = Json.encodeToString(LocalLanguage(strings))
    }
}
