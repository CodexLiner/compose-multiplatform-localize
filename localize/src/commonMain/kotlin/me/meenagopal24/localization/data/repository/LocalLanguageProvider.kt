package me.meenagopal24.localization.data.repository

import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.LocalLanguageList
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Interface defining methods for managing and fetching local language configurations.
 */
internal interface LocalLanguageProvider {

    /**
     * Fetches a specific language by its code.
     *
     * @param code The language code (e.g., "en", "fr").
     * @return A [LocalLanguage] object representing the language, or null if not found.
     */
    suspend fun getLanguage(code: String): LocalLanguage?

    /**
     * Retrieves a list of all available languages in the system.
     *
     * @return A list of [LocalLanguageList] objects, or null entries in the list if some languages are not available.
     */
    suspend fun getLanguages(): List<LocalLanguageList?>

    /**
     * Retrieves the current language's code.
     *
     * @return The language code (e.g., "en", "fr").
     */
    fun getCurrentLanguage(): String

    /**
     * Sets the current language using the provided language code.
     *
     * @param code The language code (e.g., "en", "fr").
     */
    fun setCurrentLanguage(code: String)

    /**
     * Forces an update of the language list, typically from a remote source or a cache refresh.
     */
    fun forceUpdateLanguages()

    /**
     * A [MutableStateFlow] that holds a map of key-value pairs representing localized strings.
     * The key is a string identifier, and the value is its translation in the current language.
     */
    var localStringsState: MutableStateFlow<Map<String, String>>
}