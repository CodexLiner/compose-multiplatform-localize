package org.company.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kmplocalize.composeapp.generated.resources.Res
import kmplocalize.composeapp.generated.resources.allow_camera_to_take_picture
import kmplocalize.composeapp.generated.resources.app_name
import kmplocalize.composeapp.generated.resources.back_to_home
import kmplocalize.composeapp.generated.resources.basic_information
import kmplocalize.composeapp.generated.resources.btn_continue
import kmplocalize.composeapp.generated.resources.btn_register
import kmplocalize.composeapp.generated.resources.btn_scan_qr
import kmplocalize.composeapp.generated.resources.btn_submit
import kmplocalize.composeapp.generated.resources.cancel
import kmplocalize.composeapp.generated.resources.checked_in
import kmplocalize.composeapp.generated.resources.cyclone
import kmplocalize.composeapp.generated.resources.enter_the_6_digit_visit_code
import kmplocalize.composeapp.generated.resources.enter_the_visit_code_sent
import kmplocalize.composeapp.generated.resources.enter_valid_email_to_continue
import kmplocalize.composeapp.generated.resources.enter_valid_name_email
import kmplocalize.composeapp.generated.resources.help_us_find_host
import kmplocalize.composeapp.generated.resources.i_am_done
import kmplocalize.composeapp.generated.resources.invalid_otp
import kmplocalize.composeapp.generated.resources.kindly_proceed_to
import kmplocalize.composeapp.generated.resources.lb_action_message
import kmplocalize.composeapp.generated.resources.lb_additional_info
import kmplocalize.composeapp.generated.resources.lb_checked_in
import kmplocalize.composeapp.generated.resources.lb_empty_email
import kmplocalize.composeapp.generated.resources.lb_empty_password
import kmplocalize.composeapp.generated.resources.lb_enter_6_digit_otp
import kmplocalize.composeapp.generated.resources.lb_incorrect_email_password
import kmplocalize.composeapp.generated.resources.lb_log_in_to_your_account
import kmplocalize.composeapp.generated.resources.lb_login
import kmplocalize.composeapp.generated.resources.lb_otp_sent_to
import kmplocalize.composeapp.generated.resources.lb_please_enter_your_details
import kmplocalize.composeapp.generated.resources.lb_position_qr_code
import kmplocalize.composeapp.generated.resources.lb_redirecting_home
import kmplocalize.composeapp.generated.resources.lb_scan_qr
import kmplocalize.composeapp.generated.resources.lb_showing_result
import kmplocalize.composeapp.generated.resources.lb_visit_details
import kmplocalize.composeapp.generated.resources.lb_welcome_message
import kmplocalize.composeapp.generated.resources.lb_whom_are_you_visiting
import kmplocalize.composeapp.generated.resources.no_internet_connection
import kmplocalize.composeapp.generated.resources.no_internet_connection_description
import kmplocalize.composeapp.generated.resources.notified_your_host
import kmplocalize.composeapp.generated.resources.open_github
import kmplocalize.composeapp.generated.resources.please_get_in_touch
import kmplocalize.composeapp.generated.resources.provide_valid_number
import kmplocalize.composeapp.generated.resources.records_not_found
import kmplocalize.composeapp.generated.resources.registration_complete
import kmplocalize.composeapp.generated.resources.run
import kmplocalize.composeapp.generated.resources.search_by_name
import kmplocalize.composeapp.generated.resources.show_it_to_front_desk
import kmplocalize.composeapp.generated.resources.stop
import kmplocalize.composeapp.generated.resources.tell_us_about_yourself
import kmplocalize.composeapp.generated.resources.theme
import kmplocalize.composeapp.generated.resources.this_qr_code_has_expired
import kmplocalize.composeapp.generated.resources.visit_code_invalid
import kmplocalize.composeapp.generated.resources.your_details
import me.meenagopal24.localization.local.Localize
import me.meenagopal24.localization.local.localStringResources
import me.meenagopal24.localization.models.enums.Language
import org.jetbrains.compose.resources.StringResource

private data class LocalizationResourceItem(
    val resource: StringResource,
    val formatArg: String? = null
)

private val demoItems = listOf(
    LocalizationResourceItem(Res.string.app_name),
    LocalizationResourceItem(Res.string.lb_welcome_message),
    LocalizationResourceItem(Res.string.lb_action_message),
    LocalizationResourceItem(Res.string.btn_continue),
    LocalizationResourceItem(Res.string.btn_submit),
    LocalizationResourceItem(Res.string.lb_checked_in),
    LocalizationResourceItem(Res.string.checked_in),
    LocalizationResourceItem(Res.string.lb_scan_qr),
    LocalizationResourceItem(Res.string.btn_scan_qr),
    LocalizationResourceItem(Res.string.btn_register),
    LocalizationResourceItem(Res.string.lb_position_qr_code),
    LocalizationResourceItem(Res.string.lb_visit_details),
    LocalizationResourceItem(Res.string.this_qr_code_has_expired),
    LocalizationResourceItem(Res.string.allow_camera_to_take_picture),
    LocalizationResourceItem(Res.string.lb_enter_6_digit_otp),
    LocalizationResourceItem(Res.string.enter_the_6_digit_visit_code),
    LocalizationResourceItem(Res.string.enter_the_visit_code_sent),
    LocalizationResourceItem(Res.string.visit_code_invalid),
    LocalizationResourceItem(Res.string.lb_otp_sent_to, "host@company.com"),
    LocalizationResourceItem(Res.string.invalid_otp),
    LocalizationResourceItem(Res.string.please_get_in_touch),
    LocalizationResourceItem(Res.string.lb_redirecting_home),
    LocalizationResourceItem(Res.string.back_to_home),
    LocalizationResourceItem(Res.string.lb_whom_are_you_visiting),
    LocalizationResourceItem(Res.string.help_us_find_host),
    LocalizationResourceItem(Res.string.lb_showing_result, "5"),
    LocalizationResourceItem(Res.string.your_details),
    LocalizationResourceItem(Res.string.tell_us_about_yourself),
    LocalizationResourceItem(Res.string.basic_information),
    LocalizationResourceItem(Res.string.lb_additional_info),
    LocalizationResourceItem(Res.string.search_by_name),
    LocalizationResourceItem(Res.string.enter_valid_name_email),
    LocalizationResourceItem(Res.string.enter_valid_email_to_continue),
    LocalizationResourceItem(Res.string.kindly_proceed_to, "Reception Desk A"),
    LocalizationResourceItem(Res.string.records_not_found),
    LocalizationResourceItem(Res.string.registration_complete),
    LocalizationResourceItem(Res.string.notified_your_host),
    LocalizationResourceItem(Res.string.show_it_to_front_desk),
    LocalizationResourceItem(Res.string.provide_valid_number),
    LocalizationResourceItem(Res.string.lb_login),
    LocalizationResourceItem(Res.string.lb_log_in_to_your_account),
    LocalizationResourceItem(Res.string.lb_please_enter_your_details),
    LocalizationResourceItem(Res.string.lb_incorrect_email_password),
    LocalizationResourceItem(Res.string.lb_empty_email),
    LocalizationResourceItem(Res.string.lb_empty_password),
    LocalizationResourceItem(Res.string.no_internet_connection),
    LocalizationResourceItem(Res.string.no_internet_connection_description),
    LocalizationResourceItem(Res.string.cyclone),
    LocalizationResourceItem(Res.string.open_github),
    LocalizationResourceItem(Res.string.run),
    LocalizationResourceItem(Res.string.stop),
    LocalizationResourceItem(Res.string.theme),
    LocalizationResourceItem(Res.string.i_am_done),
    LocalizationResourceItem(Res.string.cancel)
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
                    text = localStringResources(Res.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = localStringResources(Res.string.lb_welcome_message),
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
                        text = localStringResources(Res.string.lb_action_message),
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
                    localStringResources(item.resource, item.formatArg)
                } else {
                    localStringResources(item.resource)
                }

                val keyName = item.resource.key.substringAfter(":")

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
                                    text = keyName,
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
