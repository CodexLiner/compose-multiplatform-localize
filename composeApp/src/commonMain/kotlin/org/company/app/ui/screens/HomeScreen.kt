package org.company.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.meenagopal24.localization.local.Localize
import me.meenagopal24.localization.local.localStringResources
import me.meenagopal24.localization.models.enums.Language

private data class LocalizationDemoItem(
    val key: String,
    val formatArg: String? = null
)

private val demoItems = listOf(
    LocalizationDemoItem("app_name"),
    LocalizationDemoItem("lb_welcome_message"),
    LocalizationDemoItem("lb_action_message"),
    LocalizationDemoItem("btn_continue"),
    LocalizationDemoItem("btn_submit"),
    LocalizationDemoItem("lb_checked_in"),
    LocalizationDemoItem("checked_in"),
    LocalizationDemoItem("lb_scan_qr"),
    LocalizationDemoItem("btn_scan_qr"),
    LocalizationDemoItem("btn_register"),
    LocalizationDemoItem("lb_position_qr_code"),
    LocalizationDemoItem("lb_visit_details"),
    LocalizationDemoItem("this_qr_code_has_expired"),
    LocalizationDemoItem("allow_camera_to_take_picture"),
    LocalizationDemoItem("lb_enter_6_digit_otp"),
    LocalizationDemoItem("enter_the_6_digit_visit_code"),
    LocalizationDemoItem("enter_the_visit_code_sent"),
    LocalizationDemoItem("visit_code_invalid"),
    LocalizationDemoItem("lb_otp_sent_to", "host@company.com"),
    LocalizationDemoItem("invalid_otp"),
    LocalizationDemoItem("please_get_in_touch"),
    LocalizationDemoItem("lb_redirecting_home"),
    LocalizationDemoItem("back_to_home"),
    LocalizationDemoItem("lb_whom_are_you_visiting"),
    LocalizationDemoItem("help_us_find_host"),
    LocalizationDemoItem("lb_showing_result", "5"),
    LocalizationDemoItem("your_details"),
    LocalizationDemoItem("tell_us_about_yourself"),
    LocalizationDemoItem("basic_information"),
    LocalizationDemoItem("lb_additional_info"),
    LocalizationDemoItem("search_by_name"),
    LocalizationDemoItem("enter_valid_name_email"),
    LocalizationDemoItem("enter_valid_email_to_continue"),
    LocalizationDemoItem("kindly_proceed_to", "Reception Desk A"),
    LocalizationDemoItem("records_not_found"),
    LocalizationDemoItem("registration_complete"),
    LocalizationDemoItem("notified_your_host"),
    LocalizationDemoItem("show_it_to_front_desk"),
    LocalizationDemoItem("provide_valid_number"),
    LocalizationDemoItem("lb_login"),
    LocalizationDemoItem("lb_log_in_to_your_account"),
    LocalizationDemoItem("lb_please_enter_your_details"),
    LocalizationDemoItem("lb_incorrect_email_password"),
    LocalizationDemoItem("lb_empty_email"),
    LocalizationDemoItem("lb_empty_password"),
    LocalizationDemoItem("no_internet_connection"),
    LocalizationDemoItem("no_internet_connection_description"),
    LocalizationDemoItem("cyclone"),
    LocalizationDemoItem("open_github"),
    LocalizationDemoItem("run"),
    LocalizationDemoItem("stop"),
    LocalizationDemoItem("theme"),
    LocalizationDemoItem("i_am_done"),
    LocalizationDemoItem("cancel")
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val languages = listOf(
        Language.ENGLISH_US,
        Language.HINDI,
        Language.MARATHI
    )

    var currentLangCode by remember {
        mutableStateOf(Localize.getCurrentLanguage().ifBlank { Language.ENGLISH_US.code })
    }
    val isUpdating by Localize.isUpdating.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = localStringResources("app_name"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = localStringResources("lb_welcome_message"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(visible = isUpdating) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = localStringResources("lb_action_message"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Text(
            text = "Active Locale: ${currentLangCode.uppercase()}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            languages.forEach { lang ->
                val isSelected = currentLangCode.equals(lang.code, ignoreCase = true)
                if (isSelected) {
                    Button(
                        onClick = {
                            currentLangCode = lang.code
                            Localize.setCurrentLanguage(lang)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${lang.flag} ${lang.nativeName}")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            currentLangCode = lang.code
                            Localize.setCurrentLanguage(lang)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${lang.flag} ${lang.nativeName}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total Translations (${demoItems.size} Keys)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(demoItems) { item ->
                val localizedValue = if (item.formatArg != null) {
                    localStringResources(item.key, item.formatArg)
                } else {
                    localStringResources(item.key)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = item.key,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = localizedValue,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
