package me.meenagopal24.localization.providers.krivan

/**
 * Internal constants, route paths, and default configurations for the Krivan Localization platform.
 */
internal object KrivanConstants {
    const val PROD_BASE_URL = "https://api.krivan.app"
    const val QA_BASE_URL = "https://api-qa.krivan.app"
    const val LOCAL_BASE_URL = "http://10.0.2.2:4000"

    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_API_KEY = "X-API-Key"
    const val HEADER_IF_NONE_MATCH = "If-None-Match"
    const val HEADER_ETAG = "ETag"
    const val HEADER_ACCEPT = "Accept"
    const val HEADER_CACHE_CONTROL = "Cache-Control"

    const val ETAG_STORAGE_PREFIX = "krivan_etag_"
    const val SSE_PING_PREFIX = ":"
    const val SSE_EVENT_PREFIX = "event:"
    const val SSE_DATA_PREFIX = "data:"

    const val EVENT_VERSION_PUBLISHED = "version.published"
    const val EVENT_VERSION_ACTIVATED = "version.activated"
    const val EVENT_OVERRIDE_MODIFIED = "override.modified"
    const val EVENT_PROPOSAL_SUBMITTED = "proposal.submitted"

    val DEFAULT_SUBSCRIBED_EVENTS = listOf(
        EVENT_VERSION_PUBLISHED,
        EVENT_VERSION_ACTIVATED,
        EVENT_OVERRIDE_MODIFIED
    )

    fun buildTranslationsPath(projectId: String): String =
        "/api/v1/projects/$projectId/translations"

    fun buildExportPath(projectId: String): String =
        "/api/v1/projects/$projectId/export"

    fun buildStreamPath(projectId: String): String =
        "/api/v1/projects/$projectId/stream"
}

