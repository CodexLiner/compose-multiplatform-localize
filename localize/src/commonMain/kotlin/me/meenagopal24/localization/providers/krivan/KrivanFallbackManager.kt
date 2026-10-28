package me.meenagopal24.localization.providers.krivan

import me.meenagopal24.localization.models.LocalLanguage

/**
 * Offline-first fallback repository.
 *
 * Resolves translations using a 3-tier hierarchy:
 * 1. Persistent disk storage (last known healthy state)
 * 2. Pre-bundled baseline defaults (shipped in code or assets)
 * 3. Empty fallback
 */
internal class KrivanFallbackManager(
    private val preBundledLanguages: Map<String, LocalLanguage> = emptyMap()
) {
    /**
     * Retrieves pre-bundled strings for [languageCode] if configured.
     */
    fun getBundledLanguage(languageCode: String): LocalLanguage? {
        return preBundledLanguages[languageCode]
    }

    /**
     * Resolves the best available offline copy when the network is unreachable.
     */
    fun resolveOfflineCopy(
        languageCode: String,
        cachedDiskCopy: LocalLanguage?
    ): LocalLanguage {
        if (cachedDiskCopy != null && (cachedDiskCopy.languages.isNotEmpty() || cachedDiskCopy.stringArrays.isNotEmpty())) {
            return cachedDiskCopy
        }
        return getBundledLanguage(languageCode) ?: LocalLanguage()
    }
}

