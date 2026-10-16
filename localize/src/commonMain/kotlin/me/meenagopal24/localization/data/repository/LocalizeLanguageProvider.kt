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
import me.meenagopal24.localization.models.LocalLanguageList

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
    override var localStringsState: MutableStateFlow<Map<String, String>> =
        MutableStateFlow(mapOf())

    /**
     * Holds the current localized string arrays for the selected language.
     */
    override var localStringArraysState: MutableStateFlow<Map<String, List<String>>> =
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
            val localLang = getLanguageFromLocalStorage(code)
            val lang = if (localLang != null && (localLang.languages.isNotEmpty() || localLang.stringArrays.isNotEmpty())) {
                localLang
            } else {
                getLanguage(code)
            }
            lang?.let {
                if (it.languages.isNotEmpty() || it.stringArrays.isNotEmpty()) {
                    settings[SELECTED_LANGUAGE_CODE] = code
                    saveLanguageToLocalStorage(code, it)
                    localStringsState.value = it.languages
                    localStringArraysState.value = it.stringArrays
                }
            }
        }
    }

    /**
     * Forces a refresh of all available languages and updates local storage.
     */
    override fun forceUpdateLanguages() {
        CoroutineScope(Dispatchers.IO).launch {
            val allLanguages = getLanguages().filterNotNull()
            val currentLanguage = getCurrentLanguage()

            val languageMap = allLanguages.associateBy { it.name }

            val supportedLanguageList = config.supportedLanguages.map { name ->
                languageMap[name] ?: LocalLanguageList(
                    name = name, language = LocalLanguage()
                )
            }
            supportedLanguageList.forEach { item ->
                saveLanguageToLocalStorage(
                    item.name, item.language
                )
            }
            languageMap[currentLanguage]?.language?.let {
                localStringsState.value = it.languages
                localStringArraysState.value = it.stringArrays
            }
        }
    }


    /**
     * Fetches the language strings for a given language code from local storage.
     */
    private fun getLanguageFromLocalStorage(code: String): LocalLanguage? {
        return settings.get<String>(code.plus(SELECTED_LANGUAGE_MAP))
            ?.let { Json.decodeFromString<LocalLanguage>(it) }
    }

    /**
     * Saves the language strings for a given language code to local storage.
     */
    private fun saveLanguageToLocalStorage(code: String, localLanguage: LocalLanguage) {
        settings[code.plus(SELECTED_LANGUAGE_MAP)] = Json.encodeToString(localLanguage)
    }
}
