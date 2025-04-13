package org.company.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.paragon.localization.local.localStringResources
import kmplocalize.composeapp.generated.resources.Res
import kmplocalize.composeapp.generated.resources.back_to_home
import kmplocalize.composeapp.generated.resources.kindly_proceed_to
import org.company.app.ui.navigation.Screens

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Text(text = localStringResources(Res.string.kindly_proceed_to), modifier = modifier)
        Button(onClick = {
            navController.navigate(Screens.Login.path)
        }) {
            Text(text = localStringResources(Res.string.back_to_home), modifier = modifier)
        }
    }
}