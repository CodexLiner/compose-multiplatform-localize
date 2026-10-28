package me.meenagopal24.localization.providers.krivan

import me.meenagopal24.localization.models.LocalLanguage

/**
 * Engine responsible for applying instant differential hotfixes directly to in-memory translations.
 *
 * Enables sub-50ms instant updates without waiting for full bundle downloads.
 */
internal class KrivanPatchEngine {

    /**
     * Applies a single key mutation to the existing [LocalLanguage].
     *
     * @param current The current [LocalLanguage] state.
     * @param keyName The translation key identifier.
     * @param newValue The replacement value, or null if deleted.
     * @param action The operation performed (create, update, delete, restore).
     * @return The updated [LocalLanguage] with the mutation applied.
     */
    fun applyKeyPatch(
        current: LocalLanguage,
        keyName: String,
        newValue: String?,
        action: String
    ): LocalLanguage {
        val updatedLanguages = current.languages.toMutableMap()

        when (action.lowercase()) {
            "delete" -> {
                updatedLanguages.remove(keyName)
            }
            else -> {
                if (newValue != null) {
                    updatedLanguages[keyName] = newValue
                }
            }
        }

        return LocalLanguage(
            languages = updatedLanguages,
            stringArrays = current.stringArrays
        )
    }
}

