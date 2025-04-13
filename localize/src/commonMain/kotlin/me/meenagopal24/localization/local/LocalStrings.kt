package me.meenagopal24.localization.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import me.meenagopal24.localization.utils.replaceWithArgs
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Retrieves an overridden localized string from the local language provider using a key.
 * Falls back to the given default string if no override is found.
 *
 * @param key The string key used for localization lookup.
 * @param fallback A fallback composable lambda that returns the default string if no override is available.
 * @return The localized or fallback string.
 */
@Composable
internal fun getOverriddenString(key: String, fallback: @Composable () -> String): String {
    val localStrings = Localize.localLanguageProvider()?.localStringsState?.collectAsState()
    return localStrings?.value?.getOrElse(key) { fallback() }.orEmpty()
}

/**
 * Retrieves a localized string using [StringResource], with optional format arguments applied.
 * If a localized override exists, it is used; otherwise, it falls back to the original string resource.
 *
 * @param localKey The Compose [StringResource] key.
 * @param formatArgs Optional formatting arguments for the string.
 * @return The final formatted localized string.
 */
@Composable
private fun getLocalizedString(localKey: StringResource, formatArgs: Array<out Any>? = null): String {
    val base = getOverriddenString(localKey.key) { stringResource(localKey) }
    return if (!formatArgs.isNullOrEmpty()) base.replaceWithArgs(formatArgs.map { it.toString() }) else base
}

/**
 * Platform-specific expected function for retrieving a localized string by integer resource ID.
 *
 * This must be implemented per platform (e.g., Android or iOS).
 *
 * @param id The integer resource ID.
 * @return The localized string.
 */
@Composable
expect fun getLocalizedString(id: Int): String

/**
 * Returns a localized string using an integer resource ID.
 *
 * @param id The resource ID to fetch.
 * @return The localized string.
 */
@Composable
fun localStringResources(id: Int) = getLocalizedString(id)

/**
 * Returns a localized string using an integer resource ID and applies format arguments.
 *
 * @param id The resource ID to fetch.
 * @param formatArgs The arguments used to format the string.
 * @return The formatted localized string.
 */
@Composable
fun localStringResources(id: Int, vararg formatArgs: Any): String = getLocalizedString(id).replaceWithArgs(formatArgs.map { it.toString() })

/**
 * Returns a localized string using a [StringResource] key.
 *
 * @param localKey The Compose string resource key.
 * @return The localized string.
 */
@Composable
fun localStringResources(localKey: StringResource): String =
    getLocalizedString(localKey)

/**
 * Returns a localized string using a [StringResource] key and applies format arguments.
 *
 * @param localKey The Compose string resource key.
 * @param formatArgs The arguments used to format the string.
 * @return The formatted localized string.
 */
@Composable
fun localStringResources(localKey: StringResource, vararg formatArgs: Any): String =
    getLocalizedString(localKey, formatArgs)

/**
 * Retrieves a localized string from the state provider directly using a raw string key.
 *
 * @param key The localization key to look up.
 * @return The localized string, or `null` if not found.
 */
internal fun getLocalString(key: String): String? =
    Localize.localLanguageProvider()?.localStringsState?.value?.getOrElse(key) { null }

/**
 * Retrieves a localized string array by its resource entry name.
 * (Currently returns null – can be extended for array support.)
 *
 * @param resourceEntryName The name of the resource entry.
 * @return The localized string array, or `null` if not found or unsupported.
 */
internal fun getLocalStringArray(resourceEntryName: String?): Array<String>? = null