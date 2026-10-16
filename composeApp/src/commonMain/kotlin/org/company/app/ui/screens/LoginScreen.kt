package org.company.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import me.meenagopal24.localization.local.Localize
import me.meenagopal24.localization.local.localStringResources
import kmplocalize.composeapp.generated.resources.Res
import kmplocalize.composeapp.generated.resources.ai_description
import kmplocalize.composeapp.generated.resources.welcome_to_pilloo_ai
import kotlinx.coroutines.launch
import org.company.app.ui.components.AppContainer

@Composable
fun LoginScreen(navController: NavHostController) {
    AppContainer {
        var downloaded by remember { mutableStateOf(false) }
        var selectedLanguage by remember { mutableStateOf<String?>(null) }

        val localLanguage = listOf("en", "hi", "te")

        val forceRenderTrigger = remember { mutableStateOf(0) }

        val scope = rememberCoroutineScope()


        LaunchedEffect(selectedLanguage) {
            selectedLanguage?.let {
                Localize.setCurrentLanguage(selectedLanguage.orEmpty())
                Logger.d("selectedLanguage ${selectedLanguage.toString()} current is   ${Localize.getCurrentLanguage()}")
            }
        }


        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                scope.launch {
                    navController.navigateUp()
                }
            }) {
                Text(if (downloaded) "Downloaded" else "Download Languages")
            }

            Spacer(modifier = Modifier.height(16.dp))
            localLanguage.let { languages ->
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    items(languages.size) { index ->
                        Button(
                            onClick = { selectedLanguage = languages[index] },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(languages[index])
                        }
                    }
                }
            }

            if (localLanguage.isNotEmpty()) {
                key(forceRenderTrigger.value) {
                    Text(localStringResources(Res.string.welcome_to_pilloo_ai))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = localStringResources(Res.string.ai_description),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

            } else {
                Text("Please download languages first.")
            }
        }
    }
}
