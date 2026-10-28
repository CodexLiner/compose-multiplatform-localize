package me.meenagopal24.localization.configuration

import me.meenagopal24.localization.models.enums.Language

/**
 * Configuration defining the default and supported languages for a localization provider.
 *
 * Supports both type-safe [Language] enum definitions and legacy string codes.
 *
 * @property defaultLanguage The default language code (e.g., "en-US").
 * @property supportedLanguages The list of supported language codes (e.g., `listOf("en-US", "hi-IN", "mr-IN")`).
 */
class ProviderConfig(
    var defaultLanguage: String = "",
    var supportedLanguages: List<String> = emptyList(),
) {
    /**
     * Type-safe constructor accepting [Language] enums matching backend locales.
     *
     * Example:
     * ```kotlin
     * val config = ProviderConfig(
     *     default = Language.ENGLISH_US,
     *     supported = listOf(Language.ENGLISH_US, Language.HINDI, Language.MARATHI)
     * )
     * ```
     */
    constructor(
        default: Language,
        supported: List<Language>
    ) : this(
        defaultLanguage = default.code,
        supportedLanguages = supported.map { it.code }
    )

    /**
     * Returns the [defaultLanguage] resolved as a type-safe [Language] enum.
     */
    val defaultLanguageEnum: Language
        get() = Language.fromCodeOrDefault(defaultLanguage)

    /**
     * Returns the list of [supportedLanguages] resolved as type-safe [Language] enums.
     */
    val supportedLanguagesEnum: List<Language>
        get() = supportedLanguages.mapNotNull { Language.fromCode(it) }
}
