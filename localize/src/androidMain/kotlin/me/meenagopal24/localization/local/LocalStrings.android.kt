package me.meenagopal24.localization.local

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import java.util.concurrent.ConcurrentHashMap

private val resourceEntryNameCache = ConcurrentHashMap<Int, String>()

/**
 * Fast cached accessor for resource entry names to eliminate repeated JNI reflection.
 */
internal fun Resources.getEntryNameCached(id: Int): String {
    return resourceEntryNameCache.computeIfAbsent(id) { resId ->
        try {
            getResourceEntryName(resId)
        } catch (e: Exception) {
            resId.toString()
        }
    }
}

/**
 * Retrieves the localized string for a given key.
 */
@Composable
actual fun getLocalizedString(id: Int): String {
    val context = LocalContext.current
    val key = context.resources.getEntryNameCached(id)
    return getOverriddenString(key) { stringResource(id) }
}

/**
 * Retrieves the localized string array for a given key.
 */
@Composable
actual fun getLocalizedStringArray(id: Int): Array<String> {
    val context = LocalContext.current
    val key = context.resources.getEntryNameCached(id)
    return getOverriddenStringArray(key) { context.resources.getStringArray(id) }
}
