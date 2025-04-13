package me.meenagopal24.localization.local

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

/**
 * Retrieves the localized string for a given key.
 */
@Composable
actual fun getLocalizedString(id: Int): String {
    val context = LocalContext.current
    val key = context.resources.getResourceEntryName(id)
    return getOverriddenString(key) { stringResource(id) }
}
