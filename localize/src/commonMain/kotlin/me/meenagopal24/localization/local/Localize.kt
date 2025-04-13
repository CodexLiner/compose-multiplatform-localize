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

    internal val settings: Settings = Settings()
    private var languageProvider: LocalLanguageProvider? = null

    /**
     * Initializes the Localize object with the given language provider and sets the current language.
     */
    fun init(languageProvider: LocalizeLanguageProvider) {
        this.languageProvider = languageProvider
        val defaultLanguage = settings.get<String>(SELECTED_LANGUAGE_CODE)
            ?: languageProvider.providerConfig.defaultLanguage
        setCurrentLanguage(defaultLanguage)
        initPlatform()
    }

    /**
     * Returns the current language provider.
     */
    internal fun getProvider(): LocalLanguageProvider = languageProvider ?: throw InitializationException()

    /**
     * Sets the current language by updating the language provider.
     */
    fun setCurrentLanguage(code: String) {
        getProvider().setCurrentLanguage(code)
    }

    /**
     * Retrieves the current language from the language provider.
     */
    fun getCurrentLanguage(): String =
        getProvider().getCurrentLanguage()

    /**
     * Forces an update of the languages in the language provider.
     */
    fun forceUpdateLanguages() {
        getProvider().forceUpdateLanguages()
    }
}
