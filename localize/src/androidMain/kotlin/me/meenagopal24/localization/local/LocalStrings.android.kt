package me.meenagopal24.localization.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import me.meenagopal24.localization.utils.replaceWithArgs


/**
 * Retrieves the localized string for a given key.
 */
@Composable
private fun getLocalizedString(id: Int): String {
    val resources = LocalContext.current.resources
    val localStrings = Localize.localLanguageProvider()?.localStringsState?.collectAsState()
    val key = resources.getResourceEntryName(id)
    return localStrings?.value?.getOrElse(key) { stringResource(id) }.orEmpty()
}

/**
 * Retrieves the localized string for a given key, with optional format arguments.
 */
@Composable
fun localStringResources(id: Int) = getLocalizedString(id)

/**
 * Retrieves the localized string for a given key with the provided format arguments.
 */
@Composable
fun localStringResources(id: Int, vararg formatArgs: Any) = getLocalizedString(id).replaceWithArgs(formatArgs.map { it.toString() })

/**
 * Fetches a localized string by its key from the local language provider, or returns null if not found.
 */
internal fun getLocalString(key: String) = Localize.localLanguageProvider()?.localStringsState?.value?.getOrElse(key) { null }

/**
 * Fetches a localized string array by its resource entry name, returns null for now.
 */
internal fun getLocalStringArray(resourceEntryName: String?): Array<String>? = null
