package org.company.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.local.Localize
import me.meenagopal24.localization.models.enums.FileType
import me.meenagopal24.localization.models.enums.Language
import me.meenagopal24.localization.providers.krivan.KrivanConfig
import me.meenagopal24.localization.providers.krivan.KrivanEnvironment
import me.meenagopal24.localization.providers.krivan.KrivanLocalizeLanguageProvider
import org.company.app.theme.AppTheme
import org.company.app.ui.navigation.NavigationHost
import org.company.app.ui.navigation.Screens

@Composable
internal fun App() = AppTheme {
    key(Unit) {
        val providerConfig = ProviderConfig(
            default = Language.ENGLISH_US,
            supported = listOf(Language.ENGLISH_US, Language.HINDI, Language.MARATHI)
        )

        val krivanProvider = KrivanLocalizeLanguageProvider(
            krivanConfig = KrivanConfig(
                projectId = "602d53f2-3ccd-4da2-83bc-41d0adc10fbf",
                apiKey = "loc_live_f22057f211205dbfaec4cc1756dcd325ef0adbe135b7ac20",
                environment = KrivanEnvironment.QA,
                fileType = FileType.JSON,
                enableLiveOtaSync = true
            ),
            providerConfig = providerConfig
        )

        Localize.init(krivanProvider)
    }

    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        NavigationHost(
            navHostController = rememberNavController(),
            startDestination = Screens.Home.path
        )
    }
}
