package com.adapty.kmp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import com.adapty.internal.crossplatform.CrossplatformHelper
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.utils.FileLocation


internal var applicationContext: Context? = null
    private set

internal class AdaptyContextInitializer : Initializer<Unit>,
    Application.ActivityLifecycleCallbacks {

    private val crossplatformHelper by lazy { CrossplatformHelper.shared }
    private var currentActivity: Activity? = null

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
            transformFallbackPaywallLocation = { location ->
                FileLocation.fromAsset(location)
            }
        )
        crossplatformHelper.setActivity { currentActivity }
        (applicationContext as? Application)?.registerActivityLifecycleCallbacks(this)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity === activity) {
            currentActivity = null
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}