package me.meenagopal24.localization.providers.tolgee

data class TolgeeConfig(
    var apiKey: String = "", var baseURL: String? = null,
    var fileType: me.meenagopal24.localization.models.enums.FileType = me.meenagopal24.localization.models.enums.FileType.XML,
    var projectId: String? = null,
    var appName: String = "",
)