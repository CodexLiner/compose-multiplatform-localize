package me.meenagopal24.localization.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import me.meenagopal24.localization.data.repository.LocalLanguageProvider
import me.meenagopal24.localization.utils.replaceWithArgs
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

private val String.cleanKey: String
    get() = substringAfter(":")

private inline fun <T> safeGetProvider(block: (LocalLanguageProvider) -> T?): T? = runCatching {
    block(Localize.getProvider())
}.getOrNull()

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
    val overrides = Localize.getProvider().localStringsState.collectAsState()
    return overrides.value[key.cleanKey] ?: fallback()
}

/**
 * Retrieves an overridden localized string array from the local language provider using a key.
 * Falls back to the given default string array if no override is found.
 *
 * @param key The array key used for localization lookup.
 * @param fallback A fallback composable lambda that returns the default string array if no override is available.
 * @return The localized or fallback string array.
 */
@Composable
internal fun getOverriddenStringArray(key: String, fallback: @Composable () -> Array<String>): Array<String> {
    val overrides = Localize.getProvider().localStringArraysState.collectAsState()
    val list = overrides.value[key.cleanKey]
    return if (list.isNullOrEmpty()) fallback() else list.toTypedArray()
}

/**
 * Retrieves a localized string using [StringResource], with optional format arguments applied.
 * If a localized override exists, it is used; otherwise, it falls back to the original string resource.
 *
 * @param resource The Compose [StringResource] key.
 * @param args Optional formatting arguments for the string.
 * @return The final formatted localized string.
 */
@Composable
private fun getLocalizedString(resource: StringResource, args: Array<out Any>? = null): String {
    val base = getOverriddenString(resource.key) { stringResource(resource) }
    return args?.takeIf { it.isNotEmpty() }?.let { base.replaceWithArgs(it.map(Any::toString)) }
        ?: base
}

/**
 * Retrieves a localized string array using [StringArrayResource].
 * If a localized override exists, it is used; otherwise, it falls back to the original string array resource.
 *
 * @param resource The Compose [StringArrayResource] key.
 * @return The localized string array list.
 */
@Composable
private fun getLocalizedStringArray(resource: StringArrayResource): List<String> {
    val overrides = Localize.getProvider().localStringArraysState.collectAsState()
    val list = overrides.value[resource.key.cleanKey]
    return if (list.isNullOrEmpty()) stringArrayResource(resource) else list
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
 * Platform-specific expected function for retrieving a localized string array by integer resource ID.
 *
 * This must be implemented per platform (e.g., Android or iOS).
 *
 * @param id The integer resource ID.
 * @return The localized string array.
 */
@Composable
expect fun getLocalizedStringArray(id: Int): Array<String>

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
fun localStringResources(id: Int, vararg formatArgs: Any): String =
    getLocalizedString(id).replaceWithArgs(formatArgs.map { it.toString() })

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
 * Returns a localized string array using an integer resource ID.
 *
 * @param id The resource ID to fetch.
 * @return The localized string array.
 */
@Composable
fun localStringArrayResources(id: Int): Array<String> = getLocalizedStringArray(id)

/**
 * Returns a localized string array using a [StringArrayResource] key.
 *
 * @param localKey The Compose string array resource key.
 * @return The localized string list.
 */
@Composable
fun localStringArrayResources(localKey: StringArrayResource): List<String> =
    getLocalizedStringArray(localKey)

/**
 * Retrieves a localized string from the state provider directly using a raw string key.
 *
 * @param key The localization key to look up.
 * @return The localized string, or `null` if not found.
 */
internal fun getLocalString(key: String): String? {
    return safeGetProvider { provider ->
        provider.localStringsState.value[key.cleanKey]
    }
}

/**
 * Retrieves a localized string using [StringResource], falling back to the resource default if not overridden.
 *
 * @param resource The Compose [StringResource].
 * @return The localized or default string.
 */
suspend fun getLocalString(resource: StringResource): String =
    getLocalString(resource.key) ?: getString(resource)

/**
 * Retrieves a localized string using [StringResource] with optional format arguments applied.
 *
 * @param resource The Compose [StringResource].
 * @param formatArgs Optional formatting arguments for the string.
 * @return The localized, formatted string.
 */
suspend fun getLocalString(resource: StringResource, vararg formatArgs: Any): String {
    val base = getLocalString(resource)
    return formatArgs.takeIf { it.isNotEmpty() }
        ?.let { base.replaceWithArgs(it.map(Any::toString)) }
        ?: base
}

/**
 * Retrieves a localized string array by its resource entry name.
 *
 * @param resourceEntryName The name of the resource entry.
 * @return The localized string array, or `null` if not found or unsupported.
 */
internal fun getLocalStringArray(resourceEntryName: String?): Array<String>? {
    if (resourceEntryName == null) return null
    return safeGetProvider { provider ->
        val list = provider.localStringArraysState.value[resourceEntryName.cleanKey]
        if (list.isNullOrEmpty()) null else list.toTypedArray()
    }
}
