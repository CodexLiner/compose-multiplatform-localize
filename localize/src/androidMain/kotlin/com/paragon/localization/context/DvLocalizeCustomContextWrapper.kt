package com.paragon.localization.context

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources

/**
 * A custom `ContextWrapper` that overrides the `getResources()` method to provide custom resources.
 * This class allows for modifying resources (such as strings) in the given context.
 */
class DvLocalizeCustomContextWrapper(base: Context) : ContextWrapper(base) {
    override fun getResources(): Resources {
        return DvResources(baseContext.resources)
    }
}