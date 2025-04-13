package org.company.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.meenagopal24.localization.configuration.ProviderConfig
import me.meenagopal24.localization.data.repository.LocalLanguageProviderImpl
import me.meenagopal24.localization.local.Localize
import me.meenagopal24.localization.local.localStringResources
import me.meenagopal24.localization.providers.tolgee.TolgeeLocalLanguageProvider
import kmplocalize.composeapp.generated.resources.Res
import kmplocalize.composeapp.generated.resources.enter_valid_name_email
import kmplocalize.composeapp.generated.resources.kindly_proceed_to
import me.meenagopal24.localization.providers.custom.CustomLocalLanguageProvider

import org.company.app.theme.AppTheme
import org.company.app.theme.ScrimLight
import org.company.app.ui.components.AppContainer
import org.company.app.ui.navigation.NavigationHost
import org.company.app.ui.navigation.Screens

@Composable
internal fun App() = AppTheme {
    rememberNavController()
    key(Unit) {
        val providerConfig = ProviderConfig.Builder(
            appName = "5",
            defaultLanguage = "hi",
            supportedLanguages = listOf("en", "hi", "ar", "fr", "uk"),
            baseURL = "https://meenagopal24.me/resources/",
            apiKey = "tgpak_gvpwwzbxnj2xa5jqgnxts4dlonzte33lmjuda2bxnbqw4",
        ).build()

        Localize.init(CustomLocalLanguageProvider(providerConfig))
    }

   Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
       NavigationHost(navHostController = rememberNavController() , startDestination = Screens.Home.path)
   }
}
