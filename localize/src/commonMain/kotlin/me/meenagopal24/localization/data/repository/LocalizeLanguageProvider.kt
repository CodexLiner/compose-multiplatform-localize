package me.meenagopal24.localization.data.repository

import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.constants.SELECTED_LANGUAGE_CODE
import me.meenagopal24.localization.constants.SELECTED_LANGUAGE_MAP
import me.meenagopal24.localization.local.Localize.settings
import me.meenagopal24.localization.models.LocalLanguage
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
import me.meenagopal24.localization.exceptions.InvalidConfigurationException

/**
 * Abstract base class for providing localized language data.
 *
 * @property config Configuration for the language provider, including app name, supported languages, and default language.
 */
abstract class LocalizeLanguageProvider(val config: ProviderConfig) : LocalLanguageProvider {

    init {
        require(config.supportedLanguages.isNotEmpty()) {
            "Localize: `supportedLanguages` cannot be empty, at least one language must be supported"
        }
        require(config.defaultLanguage.isNotBlank()) {
            "Localize: `defaultLanguage` must be a language code."
        }
    }

    /**
     * Holds the current localized strings for the selected language.
     */
    override var localStringsState = MutableStateFlow<Map<String, String>>(emptyMap())

    /**
     * Retrieves the current language code from local storage.
     */
    override fun getCurrentLanguage(): String = settings.get<String>(SELECTED_LANGUAGE_CODE).orEmpty()

    /**
     * Sets the current language and updates the local language strings in storage.
     *
     * @param code The language code to set.
     */
    override fun setCurrentLanguage(code: String) {
        runBlocking {
            val languageStrings = getLanguageFromLocalStorage(code) ?: getLanguage(code)?.languages
            if (!languageStrings.isNullOrEmpty()) {
                settings[SELECTED_LANGUAGE_CODE] = code
                saveLanguageToLocalStorage(code, languageStrings)
                localStringsState.value = languageStrings
            }
        }
    }

    /**
     * Forces a refresh of all available languages and updates local storage.
     * This is useful when manually syncing the latest translations.
     */
    override fun forceUpdateLanguages() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentLang = getCurrentLanguage()
            val allLanguages = getLanguages().filterNotNull()

            allLanguages.forEach { (name, lang) ->
                if (lang.languages.isNotEmpty()) {
                    saveLanguageToLocalStorage(name + SELECTED_LANGUAGE_MAP, lang.languages)
                }
            }

            val selectedStrings = allLanguages.find { it.name == currentLang }?.language?.languages
            selectedStrings?.let {
                localStringsState.value = it
            }
        }
    }

    /**
     * Fetches the language strings for a given language code from local storage.
     *
     * @param code The language code.
     * @return A map of localized strings, or `null` if not found.
     */
    private fun getLanguageFromLocalStorage(code: String): Map<String, String>? {
        val stored = settings.get<String>(code + SELECTED_LANGUAGE_MAP)
        return stored?.let { Json.decodeFromString<LocalLanguage>(it).languages }
    }

    /**
     * Saves the language strings for a given language code to local storage.
     *
     * @param code The language code.
     * @param strings The localized strings to store.
     */
    private fun saveLanguageToLocalStorage(code: String, strings: Map<String, String>) {
        settings[code + SELECTED_LANGUAGE_MAP] = Json.encodeToString(LocalLanguage(strings))
    }
}
