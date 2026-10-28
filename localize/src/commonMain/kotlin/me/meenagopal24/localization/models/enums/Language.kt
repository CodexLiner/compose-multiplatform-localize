package me.meenagopal24.localization.models.enums

/**
 * Standard languages enum corresponding directly to the Krivan backend locale definitions.
 *
 * Each language defines:
 * @property code The canonical BCP-47 locale code (e.g., "en-US", "hi-IN").
 * @property languageName English name of the language (e.g., "English", "Hindi").
 * @property nativeName Native script representation (e.g., "हिन्दी", "मराठी").
 * @property flag Country or regional emoji flag (e.g., "🇮🇳", "🇺🇸").
 * @property isRtl Whether the language script is Right-to-Left (e.g., Arabic, Urdu).
 */
enum class Language(
    val code: String,
    val languageName: String,
    val nativeName: String,
    val flag: String,
    val isRtl: Boolean = false
) {
    // Global & Americas
    ENGLISH_US("en-US", "English (US)", "English (US)", "🇺🇸"),
    ENGLISH_UK("en-GB", "English (UK)", "English (UK)", "🇬🇧"),
    SPANISH_SPAIN("es-ES", "Spanish (Spain)", "Español", "🇪🇸"),
    SPANISH_LATAM("es-MX", "Spanish (Latin America)", "Español (LatAm)", "🇲🇽"),
    FRENCH_FRANCE("fr-FR", "French (France)", "Français", "🇫🇷"),
    FRENCH_CANADA("fr-CA", "French (Canada)", "Français (Canada)", "🇨🇦"),
    GERMAN("de-DE", "German", "Deutsch", "🇩🇪"),
    ITALIAN("it-IT", "Italian", "Italiano", "🇮🇹"),
    PORTUGUESE_BRAZIL("pt-BR", "Portuguese (Brazil)", "Português", "🇧🇷"),
    PORTUGUESE_PORTUGAL("pt-PT", "Portuguese (Portugal)", "Português (Portugal)", "🇵🇹"),
    RUSSIAN("ru-RU", "Russian", "Русский", "🇷🇺"),
    DUTCH("nl-NL", "Dutch", "Nederlands", "🇳🇱"),
    POLISH("pl-PL", "Polish", "Polski", "🇵🇱"),
    SWEDISH("sv-SE", "Swedish", "Svenska", "🇸🇪"),
    TURKISH("tr-TR", "Turkish", "Türkçe", "🇹🇷"),

    // Middle East
    ARABIC("ar-SA", "Arabic", "العربية", "🇸🇦", isRtl = true),
    HEBREW("he-IL", "Hebrew", "עברית", "🇮🇱", isRtl = true),

    // Asia
    JAPANESE("ja-JP", "Japanese", "日本語", "🇯🇵"),
    KOREAN("ko-KR", "Korean", "한국어", "🇰🇷"),
    CHINESE_SIMPLIFIED("zh-CN", "Chinese (Simplified)", "简体中文", "🇨🇳"),
    CHINESE_TRADITIONAL("zh-TW", "Chinese (Traditional)", "繁體中文", "🇹🇼"),
    INDONESIAN("id-ID", "Indonesian", "Bahasa Indonesia", "🇮🇩"),
    VIETNAMESE("vi-VN", "Vietnamese", "Tiếng Việt", "🇻🇳"),
    THAI("th-TH", "Thai", "ไทย", "🇹🇭"),

    // Indian Scheduled & Regional Languages (Official 22 + Regional)
    HINDI("hi-IN", "Hindi", "हिन्दी", "🇮🇳"),
    BENGALI("bn-IN", "Bengali", "বাংলা", "🇮🇳"),
    TELUGU("te-IN", "Telugu", "తెలుగు", "🇮🇳"),
    MARATHI("mr-IN", "Marathi", "मराठी", "🇮🇳"),
    TAMIL("ta-IN", "Tamil", "தமிழ்", "🇮🇳"),
    URDU("ur-IN", "Urdu", "اردو", "🇮🇳", isRtl = true),
    GUJARATI("gu-IN", "Gujarati", "ગુજરાતી", "🇮🇳"),
    KANNADA("kn-IN", "Kannada", "ಕನ್ನಡ", "🇮🇳"),
    MALAYALAM("ml-IN", "Malayalam", "മലയാളം", "🇮🇳"),
    ODIA("or-IN", "Odia", "ଓଡ଼ିଆ", "🇮🇳"),
    PUNJABI("pa-IN", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳"),
    ASSAMESE("as-IN", "Assamese", "অসমীয়া", "🇮🇳"),
    BHOJPURI("bho-IN", "Bhojpuri", "भोजपुरी", "🇮🇳"),
    RAJASTHANI("raj-IN", "Rajasthani", "राजस्थानी", "🇮🇳"),
    MARWARI("mwr-IN", "Marwari", "मारवाड़ी", "🇮🇳"),
    CHHATTISGARHI("hne-IN", "Chhattisgarhi", "छत्तीसगढ़ी", "🇮🇳"),
    HARYANVI("bgc-IN", "Haryanvi", "हरियाणवी", "🇮🇳"),
    MAGAHI("mag-IN", "Magahi", "मगही", "🇮🇳"),
    MAITHILI("mai-IN", "Maithili", "मैथिली", "🇮🇳"),
    SANSKRIT("sa-IN", "Sanskrit", "संस्कृतम्", "🇮🇳"),
    KASHMIRI("ks-IN", "Kashmiri", "کٲشُر", "🇮🇳", isRtl = true),
    NEPALI("ne-IN", "Nepali", "नेपाली", "🇮🇳"),
    SINDHI("sd-IN", "Sindhi", "سنڌي", "🇮🇳", isRtl = true),
    KONKANI("kok-IN", "Konkani", "कोंकणी", "🇮🇳"),
    DOGRI("doi-IN", "Dogri", "डोगरी", "🇮🇳"),
    MANIPURI("mni-IN", "Manipuri", "মৈতৈলোন্", "🇮🇳"),
    BODO("brx-IN", "Bodo", "बड़ो", "🇮🇳"),
    SANTALI("sat-IN", "Santali", "ᱥᱟᱱᱛᱟᱲᱤ", "🇮🇳");

    companion object {
        /**
         * Resolves a [Language] enum from a string code with case-insensitive and prefix fallback.
         * For example: "en-US", "en", "HI-IN", "hi" all resolve reliably.
         */
        fun fromCode(code: String): Language? {
            if (code.isBlank()) return null
            val normalized = code.trim().lowercase().replace('_', '-')

            // 1. Exact match on code (e.g. "hi-in")
            entries.find { it.code.lowercase() == normalized }?.let { return it }

            // 2. Primary subtag match (e.g. "hi" -> HINDI, "en" -> ENGLISH_US)
            val primaryTag = normalized.split('-').firstOrNull() ?: ""
            if (primaryTag.isNotEmpty()) {
                entries.find { it.code.lowercase().startsWith("$primaryTag-") }?.let { return it }
            }

            return null
        }

        /**
         * Resolves a [Language] enum or returns [default] if not recognized.
         */
        fun fromCodeOrDefault(code: String, default: Language = ENGLISH_US): Language {
            return fromCode(code) ?: default
        }
    }
}
