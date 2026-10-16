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

/**
 * Retrieves the localized string array for a given key.
 */
@Composable
actual fun getLocalizedStringArray(id: Int): Array<String> {
    val context = LocalContext.current
    val key = context.resources.getResourceEntryName(id)
    return getOverriddenStringArray(key) { context.resources.getStringArray(id) }
}
