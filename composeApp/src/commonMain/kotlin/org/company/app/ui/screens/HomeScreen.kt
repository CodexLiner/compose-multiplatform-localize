package org.company.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kmplocalize.composeapp.generated.resources.Res
import kmplocalize.composeapp.generated.resources.ai_description
import kmplocalize.composeapp.generated.resources.generate
import kmplocalize.composeapp.generated.resources.hint_list
import kmplocalize.composeapp.generated.resources.share_pdf_screen_message
import kmplocalize.composeapp.generated.resources.welcome_to_pilloo_ai
import me.meenagopal24.localization.local.Localize
import me.meenagopal24.localization.local.localStringArrayResources
import me.meenagopal24.localization.local.localStringResources

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val languages = listOf(
        "en" to "English",
        "hi" to "हिंदी",
        "te" to "తెలుగు",
        "test" to "Test"
    )

    var currentLangCode by remember { mutableStateOf(Localize.getCurrentLanguage().ifBlank { "en" }) }

    // Fetch string array in composable scope
    val hintItems: List<String> = localStringArrayResources(Res.array.hint_list)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Header Section ---
        Text(
            text = "Localization Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- Language Selector Buttons ---
        Text(
            text = "Select Language (Active: ${currentLangCode.uppercase()})",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            languages.forEach { (code, label) ->
                val isSelected = currentLangCode == code
                if (isSelected) {
                    Button(
                        onClick = {
                            currentLangCode = code
                            Localize.setCurrentLanguage(code)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            currentLangCode = code
                            Localize.setCurrentLanguage(code)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // --- Demo Content List ---
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Single Strings Demo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📌 Single Strings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = localStringResources(Res.string.welcome_to_pilloo_ai),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = localStringResources(Res.string.ai_description),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {}) {
                                Text(localStringResources(Res.string.generate))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = localStringResources(Res.string.share_pdf_screen_message),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Section 2: String Array Demo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📋 String Array (hint_list) [Total: ${hintItems.size}]",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = localStringArrayResources(Res.array.hint_list).toList().joinToString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            itemsIndexed(hintItems) { index, hint ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}.",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
