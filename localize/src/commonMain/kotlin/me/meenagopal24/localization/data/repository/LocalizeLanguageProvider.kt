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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import me.meenagopal24.localization.exceptions.InvalidConfigurationException
import me.meenagopal24.localization.models.LocalLanguageList
import me.meenagopal24.localization.utils.SharedLocalizeJson

/**
 * Abstract base class for providing localized language data.
 *
 * @property config Configuration for the language provider, including app name, supported languages, and default language.
 */
abstract class LocalizeLanguageProvider(val config: ProviderConfig) : LocalLanguageProvider {

    private val providerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
     * Indicates whether languages are currently updating or downloading.
     */
    override var isUpdatingState: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

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
        settings[SELECTED_LANGUAGE_CODE] = code
        applyLocalLanguage(code)
        fetchAndApplyRemoteLanguage(code)
    }

    /**
     * Forces a refresh of all available languages and updates local storage.
     */
    override fun forceUpdateLanguages() {
        launchBackgroundJob {
            val allLanguages = getLanguages().filterNotNull()
            val currentLanguage = getCurrentLanguage()
            val languageMap = allLanguages.associateBy { it.name }

            config.supportedLanguages.forEach { name ->
                val langList =
                    languageMap[name] ?: LocalLanguageList(name = name, language = LocalLanguage())
                saveLanguageToLocalStorage(langList.name, langList.language)
            }

            languageMap[currentLanguage]?.language?.let { updateLanguageState(it) }
        }
    }

    private fun applyLocalLanguage(code: String) {
        getLanguageFromLocalStorage(code)?.let { updateLanguageState(it) }
    }

    private fun fetchAndApplyRemoteLanguage(code: String) {
        launchBackgroundJob {
            getLanguage(code)?.let { remoteLang ->
                if (remoteLang.hasContent()) {
                    saveLanguageToLocalStorage(code, remoteLang)
                    updateLanguageState(remoteLang)
                }
            }
        }
    }

    private fun launchBackgroundJob(block: suspend () -> Unit) {
        providerScope.launch {
            isUpdatingState.value = true
            try {
                block()
            } finally {
                isUpdatingState.value = false
            }
        }
    }

    private fun updateLanguageState(localLang: LocalLanguage) {
        if (localLang.hasContent()) {
            localStringsState.value = localLang.languages
            localStringArraysState.value = localLang.stringArrays
        }
    }

    private fun LocalLanguage.hasContent(): Boolean =
        languages.isNotEmpty() || stringArrays.isNotEmpty()

    /**
     * Fetches the language strings for a given language code from local storage.
     */
    private fun getLanguageFromLocalStorage(code: String): LocalLanguage? {
        return settings.get<String>(code.plus(SELECTED_LANGUAGE_MAP))
            ?.let { SharedLocalizeJson.decodeFromString<LocalLanguage>(it) }
    }

    /**
     * Saves the language strings for a given language code to local storage.
     */
    private fun saveLanguageToLocalStorage(code: String, localLanguage: LocalLanguage) {
        settings[code.plus(SELECTED_LANGUAGE_MAP)] = SharedLocalizeJson.encodeToString(localLanguage)
    }
}
