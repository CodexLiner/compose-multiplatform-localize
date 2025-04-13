package org.company.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.data.repository.LocalizeLanguageProvider
import me.meenagopal24.localization.local.Localize
import me.meenagopal24.localization.models.LocalLanguage
import me.meenagopal24.localization.models.LocalLanguageList
import me.meenagopal24.localization.providers.custom.CustomLocalizeLanguageProvider

import org.company.app.theme.AppTheme
import org.company.app.ui.navigation.NavigationHost
import org.company.app.ui.navigation.Screens


@Composable
internal fun App() = AppTheme {
    key(Unit) {
        val providerConfig = ProviderConfig(defaultLanguage = "hi", supportedLanguages = listOf("en", "hi", "ar", "fr", "uk"))
        Localize.init(CustomLocalizeLanguageProvider(baseURL = "https://meenagopal24.me/resources/", providerConfig = providerConfig))
    }

   Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
       NavigationHost(navHostController = rememberNavController() , startDestination = Screens.Home.path)
   }
}
