package me.meenagopal24.localization.context

import android.content.res.Resources
import com.paragon.localization.local.getLocalString
import com.paragon.localization.local.getLocalStringArray

/**
 * Custom `Resources` class that overrides various methods to return localized string resources.
 * This class allows for modifying the retrieval of resources to support dynamic localization.
 *
 * @param resources The base `Resources` object to be wrapped and extended with custom functionality.
 */
class DvResources(resources: Resources) :
    Resources(resources.assets, resources.displayMetrics, resources.configuration) {

    /**
     * Returns a localized string corresponding to the given resource ID. If the localized string
     * is not found, the base string resource is returned.
     */
    override fun getString(id: Int): String {
        return getLocalString(getResourceEntryName(id)) ?: super.getString(id)
    }

    /**
     * Returns a localized string array corresponding to the given resource ID. If the localized
     * string array is not found, the base string array is returned.
     */
    override fun getStringArray(id: Int): Array<String> {
        return getLocalStringArray(getResourceEntryName(id)) ?: super.getStringArray(id)
    }

    /**
     * Returns a localized string corresponding to the given resource ID, formatted with the provided
     * arguments. If the localized string is not found, the base formatted string resource is returned.
     */
    override fun getString(id: Int, vararg formatArgs: Any?): String {
        val formatedString = getLocalString(getResourceEntryName(id))
            ?.let { String.format(it, *formatArgs) }
        return formatedString ?: super.getString(id, *formatArgs)
    }

    /**
     * Returns a localized text (e.g., a `CharSequence`) corresponding to the given resource ID.
     * If the localized text is not found, the base text resource is returned.
     */
    override fun getText(id: Int): CharSequence {
        return getLocalString(getResourceEntryName(id)) ?: super.getText(id)
    }

    /**
     * Returns a localized text (e.g., a `CharSequence`) corresponding to the given resource ID,
     * using a default value if the localized text is not found.
     * If the localized text is not found, the base text resource or the default value is returned.
     */
    override fun getText(id: Int, def: CharSequence?): CharSequence {
        return getLocalString(getResourceEntryName(id)) ?: super.getText(id, def)
    }
}