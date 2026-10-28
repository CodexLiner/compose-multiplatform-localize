package me.meenagopal24.localization.providers.krivan

/**
 * Supported environments for the Krivan platform.
 *
 * Developers do not need to provide or know the backend URL; the SDK defaults
 * directly to [PRODUCTION] ("https://api.krivan.app").
 */
enum class KrivanEnvironment(val baseUrl: String) {
    /**
     * Production environment hosted at `https://api.krivan.app` (Default).
     */
    PRODUCTION(KrivanConstants.PROD_BASE_URL),

    /**
     * QA staging environment hosted at `https://api-qa.krivan.app`.
     */
    QA(KrivanConstants.QA_BASE_URL),

    /**
     * Local testing environment (defaults to `http://10.0.2.2:4000` for Android emulator).
     */
    LOCAL(KrivanConstants.LOCAL_BASE_URL)
}

