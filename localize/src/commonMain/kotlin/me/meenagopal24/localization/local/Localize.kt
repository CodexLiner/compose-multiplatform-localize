package me.meenagopal24.localization.local

import me.meenagopal24.localization.constants.SELECTED_LANGUAGE_CODE
import me.meenagopal24.localization.data.repository.LocalLanguageProvider
import me.meenagopal24.localization.data.repository.LocalizeLanguageProvider
import me.meenagopal24.localization.exceptions.InitializationException
import me.meenagopal24.localization.utils.initPlatform
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

/**
 * Object responsible for managing the localization settings and language provider.
 */
object Localize {

    internal val settings = Settings()
    private var localLanguageProvider: LocalLanguageProvider? = null

    /**
     * Initializes the Localize object with the given language provider and sets the current language.
     */
    fun init(languageProvider: LocalizeLanguageProvider) {
        localLanguageProvider = languageProvider
        setCurrentLanguage(settings.get<String>(SELECTED_LANGUAGE_CODE) ?: languageProvider.providerConfig.defaultLanguage)
        initPlatform()
    }

    /**
     * Returns the current language provider.
     */
    internal fun localLanguageProvider(): LocalLanguageProvider? = localLanguageProvider

    /**
     * Sets the current language by updating the language provider.
     */
    fun setCurrentLanguage(code: String) {
        isConfigured()
        localLanguageProvider?.setCurrentLanguage(code)
    }

    /**
     * Retrieves the current language from the language provider.
     */
    fun getCurrentLanguage(): String? {
        isConfigured()
        return localLanguageProvider?.getCurrentLanguage()
    }

    /**
     * Forces an update of the languages in the language provider.
     */
    fun forceUpdateLanguages() {
        isConfigured()
        localLanguageProvider?.forceUpdateLanguages()
    }

    /**
     * Ensures that the local language provider is properly configured before performing any actions.
     */
    private fun isConfigured() {
        if (localLanguageProvider == null) throw InitializationException()
    }
}
