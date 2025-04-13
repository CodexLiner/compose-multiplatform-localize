package me.meenagopal24.localization.configuration

class ProviderConfig(
    var appName: String = "",
    var defaultLanguage: String = "",
    var supportedLanguages: List<String> = emptyList(),
    var fileType: me.meenagopal24.localization.models.enums.FileType = me.meenagopal24.localization.models.enums.FileType.XML,
    var projectId: String? = null
)