package me.meenagopal24.localization.configuration

import com.paragon.localization.exceptions.InvalidConfigurationException

class ProviderConfig private constructor() {
    var apiKey: String = ""
    var appName: String = ""
    var baseURL: String? = null
    var defaultLanguage: String = ""
    var supportedLanguages: List<String> = emptyList()
    var fileType: me.meenagopal24.localization.models.enums.FileType = me.meenagopal24.localization.models.enums.FileType.XML
    var projectId: String? = null

    /**
     * Builder for constructing a ProviderConfig instance.
     *
     * @param appName The application name.
     * @param apiKey Optional API key for the provider.
     * @param baseURL Optional base URL for the provider.
     * @param projectId Optional project ID.
     * @param defaultLanguage The default language for the app.
     * @param supportedLanguages The list of supported languages.
     */
    class Builder(
        private val appName: String,
        private var apiKey: String? = null,
        private val baseURL: String? = null,
        private var projectId: String? = null,
        private val defaultLanguage: String,
        private val supportedLanguages: List<String>,
    ) {

        /**
         * Builds the ProviderConfig object with the provided parameters.
         * Throws [InvalidConfigurationException] if required fields are invalid.
         */
        fun build(): ProviderConfig {
            if (appName.isEmpty()) throw InvalidConfigurationException("Localize: `appName` cannot be empty")
            if (supportedLanguages.isEmpty()) throw InvalidConfigurationException("Localize: `supportedLanguages` cannot be empty, at least one language must be supported")

            return ProviderConfig().apply {
                appName = this@Builder.appName
                baseURL = this@Builder.baseURL
                defaultLanguage = this@Builder.defaultLanguage
                apiKey = this@Builder.apiKey.orEmpty()
                supportedLanguages = this@Builder.supportedLanguages
                projectId = this@Builder.projectId
            }
        }
    }
}