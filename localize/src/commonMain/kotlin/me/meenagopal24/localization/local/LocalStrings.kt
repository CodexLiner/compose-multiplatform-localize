package me.meenagopal24.localization.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import me.meenagopal24.localization.utils.replaceWithArgs
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Retrieves the localized string for a given key, with optional format arguments.
 */
@Composable
fun getLocalizedString(localKey: StringResource, formatArgs: Array<out Any>? = null): String {
    val localString = Localize.localLanguageProvider()?.localStringsState?.collectAsState()
    val localized = localString?.value?.getOrElse(localKey.key) { stringResource(localKey) }.orEmpty()
    return if (formatArgs != null) localized.replaceWithArgs(formatArgs.map { it.toString() }) else localized
}

/**
 * Retrieves the localized string for a given key without format arguments.
 */
@Composable
fun localStringResources(localKey: StringResource) = getLocalizedString(localKey)

/**
 * Retrieves the localized string for a given key with the provided format arguments.
 */
@Composable
fun localStringResources(localKey: StringResource, vararg formatArgs: Any) = getLocalizedString(localKey, formatArgs)

/**
 * Fetches a localized string by its key from the local language provider, or returns null if not found.
 */
internal fun getLocalString(key: String) = Localize.localLanguageProvider()?.localStringsState?.value?.getOrElse(key) { null }

/**
 * Fetches a localized string array by its resource entry name, returns null for now.
 */
internal fun getLocalStringArray(resourceEntryName: String?): Array<String>? {
    return null
}
