package com.paragon.localization.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.paragon.localization.context.ActivityLifeCycleCallbacks

/**
 * A singleton object that holds a reference to the `Context` for platform-specific initialization.
 * This is used to store the application context and is accessed during localization setup.
 */
@SuppressLint("StaticFieldLeak")
private object LocaliseContext {
    var context: Context? = null
}

/**
 * An implementation of the `Initializer` interface that initializes the `LocaliseContext` with the provided
 * `Context` when the application starts. This class ensures the context is available for platform-specific
 * localization setup.
 */
class LocaliseInitializer : Initializer<Unit> {

    /**
     * Initializes the `LocaliseContext` with the provided `Context` to make it available for localization operations.
     *
     * @param context The application context to be stored in the `LocaliseContext`.
     */
    override fun create(context: Context) { LocaliseContext.context = context }

    /**
     * Specifies the list of dependencies for this initializer. In this case, there are no dependencies.
     *
     * @return An empty list since there are no dependencies for this initializer.
     */
    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}

/**
 * Initializes platform-specific components for localization by registering the `ActivityLifecycleCallbacks`
 * to handle language changes and resource updates.
 *
 * This function should be called during application startup to set up the necessary localization hooks.
 */
actual fun initPlatform() {
    (LocaliseContext.context as? Application)?.registerActivityLifecycleCallbacks(ActivityLifeCycleCallbacks())
}