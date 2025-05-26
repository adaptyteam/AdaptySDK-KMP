package com.adapty.kmp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import com.adapty.internal.crossplatform.CrossplatformHelper
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler


internal var applicationContext: Context? = null
    private set

internal class AdaptyContextInitializer : Initializer<Unit>,
    Application.ActivityLifecycleCallbacks {

    private val crossplatformHelper by lazy { CrossplatformHelper.shared }

    override fun create(context: Context) {
        applicationContext = context.applicationContext
        CrossplatformHelper.init(
            context = applicationContext!!,
            onNewEvent = { eventName, eventDataJsonString ->
                AdaptyPluginEventHandler.onNewEvent(
                    eventName = eventName,
                    eventDataJsonString = eventDataJsonString
                )
            },
            //TODO Check if transformFallbackPaywallLocation in init is necessary transformFallbackPaywallLocation =
        )
        (applicationContext as? Application)?.registerActivityLifecycleCallbacks(this)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        onActivityUpdated(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        onActivityUpdated(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        onActivityUpdated(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        onActivityUpdated(null)
    }

    override fun onActivityStopped(activity: Activity) {
        onActivityUpdated(null)
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        onActivityUpdated(null)
    }

    private fun onActivityUpdated(activity: Activity?) {
        if (activity == null) crossplatformHelper.setActivity(null)
        else crossplatformHelper.setActivity { activity }
    }


}