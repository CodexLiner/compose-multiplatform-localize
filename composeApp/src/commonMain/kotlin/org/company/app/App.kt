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
        // Type-safe Language enums matching the Krivan backend definitions
        val providerConfig = ProviderConfig(
            default = Language.ENGLISH_US,
            supported = listOf(Language.ENGLISH_US, Language.HINDI, Language.MARATHI)
        )

        // Initialize with first-party Krivan Provider
        val krivanProvider = KrivanLocalizeLanguageProvider(
            krivanConfig = KrivanConfig(
                projectId = "51c6efc1-3690-4a0f-b963-0ceaf9124f47",
                apiKey = "loc_live_teststream1234567890abcdef123456",
                environment = KrivanEnvironment.LOCAL, // Connects to local dev backend (10.0.2.2:4000)
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
