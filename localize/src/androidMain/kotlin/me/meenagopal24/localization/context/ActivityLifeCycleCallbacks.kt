package me.meenagopal24.localization.context

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import com.paragon.localization.local.Localize
import java.util.Locale

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
class ActivityLifeCycleCallbacks : Application.ActivityLifecycleCallbacks {
     override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mutateStringResources(activity)
        activity.mutateLocal(Localize.getCurrentLanguage() ?: Locale.getDefault().language)
    }

    /**
     * Modifies the string resources of the given context by replacing the default resources with custom ones.
     */
    private fun mutateStringResources(context: Context) {
        runCatching {
            ContextThemeWrapper::class.java.getDeclaredField("mResources").apply {
                isAccessible = true
                set(context, (DvLocalizeCustomContextWrapper(context).resources))
            }
        }.getOrElse { it.printStackTrace() }
    }

    /**
     * Modifies the locale of the given context based on the provided language code.
     */
    private fun Context.mutateLocal(localCode: String): Context {
        val newLocale = localCode.getLocaleForLanguageCode()
        val configuration = Configuration(this.resources.configuration)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        return this.createConfigurationContext(configuration)
    }

    private fun String.getLocaleForLanguageCode() = runCatching {
        val localeData = this.split("-").toTypedArray()
        Locale(localeData[0], localeData[1])
    }.getOrElse { Locale(Locale.getDefault().language) }

}