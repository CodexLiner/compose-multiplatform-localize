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
import com.paragon.localization.configuration.ProviderConfig
import com.paragon.localization.data.repository.LocalLanguageProviderImpl
import com.paragon.localization.local.Localize
import com.paragon.localization.local.localStringResources
import com.paragon.localization.providers.tolgee.TolgeeLocalLanguageProvider
import kmplocalize.composeapp.generated.resources.Res
import kmplocalize.composeapp.generated.resources.enter_valid_name_email
import kmplocalize.composeapp.generated.resources.kindly_proceed_to

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

        Localize.init(TolgeeLocalLanguageProvider(providerConfig))
    }

   Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
       NavigationHost(navHostController = rememberNavController() , startDestination = Screens.Home.path)
   }


//    AppContainer {
//        var downloaded by remember { mutableStateOf(false) }
//        var selectedLanguage by remember { mutableStateOf<String?>(null) }
//
//        var localLanguage  = listOf("en" , "hi" , "ar" , "fr" , "uk")
//
//        val forceRenderTrigger = remember { mutableStateOf(0) }
//
//        val scope = rememberCoroutineScope()
//
//
//        LaunchedEffect(selectedLanguage) {
//            selectedLanguage?.let {
//                Localize.setCurrentLanguage(selectedLanguage.orEmpty())
//                Logger.d("selectedLanguage ${selectedLanguage.toString()} current is   ${Localize.getCurrentLanguage()}")
////                forceRenderTrigger.value++
//            }
//        }
//
//
//        Column(
//            modifier = Modifier.fillMaxSize().padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Button(onClick = {
//                scope.launch {
//                    Localize.forceUpdateLanguages()
//                }
//            }) {
//                Text(if (downloaded) "Downloaded" else "Download Languages")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//            localLanguage.let { languages ->
//                LazyColumn(
//                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
//                ) {
//                    items(languages.toList().size) { index ->
//                        Button(
//                            onClick = { selectedLanguage = languages.toList()[index] },
//                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
//                        ) {
//                            Text(languages.toList()[index])
//                        }
//                    }
//                }
//            }
//
//            if (localLanguage?.isNotEmpty() == true) {
//                key(forceRenderTrigger.value) {
//                    Text(localStringResources(Res.string.enter_valid_name_email , "ndsdn"))
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text(
//                        text = localStringResources(Res.string.kindly_proceed_to , "hello"),
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                }
//
//            } else {
//                Text("Please download languages first.")
//            }
//        }
//    }
}
