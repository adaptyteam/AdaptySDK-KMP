package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIFlowView
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

/**
 * Singleton for managing Adapty UI components such as flows and onboardings.
 *
 * This interface is responsible for creating, presenting, and dismissing UI views,
 * as well as observing user interactions and lifecycle events for analytics and customization.
 *
 * Typical usage:
 * - Create and display flow or onboarding views
 * - Register observers for UI events
 * - Handle dialog interactions
 *
 * @see AdaptyUIFlowsEventsObserver
 * @see AdaptyUIOnboardingsEventsObserver
 * @see AdaptyUIFlowView
 * @see AdaptyUIOnboardingView
 */
public object AdaptyUI : AdaptyUIContract by AdaptyUIImpl(adaptyPlugin = adaptyPlugin)

internal interface AdaptyUIContract {


    /**
     * Registers an [AdaptyUIOnboardingsEventsObserver] for a specific onboarding view.
     *
     * Use this when working with **native onboarding views** to listen for lifecycle events,
     * user interactions, and analytics for that particular view instance.
     *
     * Each view should have a unique [viewId] so multiple onboardings can be tracked independently.
     *
     * @param observer The observer that will receive onboarding events for this view.
     * @param viewId A unique identifier for the onboarding view.
     *
     * @see AdaptyUIOnboardingsEventsObserver
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    @Suppress("DEPRECATION")
    fun registerOnboardingEventsListener(
        observer: AdaptyUIOnboardingsEventsObserver,
        viewId: String
    )

    /**
     * Unregisters the onboarding event observer for the specified view.
     *
     * Call this when the onboarding view is dismissed or no longer needed
     * to prevent memory leaks and stop receiving events.
     *
     * @param viewId The unique identifier of the onboarding view.
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    fun unregisterOnboardingEventsListener(viewId: String)

    /**
     * Registers an [AdaptyUIFlowsEventsObserver] for a specific flow view.
     *
     * Use this when working with **native flow views** to listen for lifecycle events,
     * user interactions, and analytics for that particular view instance.
     *
     * Each view should have a unique [viewId] so multiple flows can be tracked independently.
     */
    fun registerFlowEventsListener(
        observer: AdaptyUIFlowsEventsObserver,
        viewId: String
    )

    /** Unregisters the flow event observer for the specified view. */
    fun unregisterFlowEventsListener(viewId: String)

    /**
     * Sets a global observer to receive events from flow views (cross_platform 4.0.0).
     */
    fun setFlowsEventsObserver(observer: AdaptyUIFlowsEventsObserver)

    /**
     * Sets the handler for requests a flow makes of the host app — OS permissions and in-app
     * review. Register one only if your flows use those features; see
     * [AdaptyUISystemRequestsHandler] for what happens when none is set.
     */
    fun setSystemRequestsHandler(handler: AdaptyUISystemRequestsHandler)

    /**
     * Sets the resolver that drives purchases and restores started from flow views in **observer
     * mode**. Register one only if you use observer mode; see
     * [AdaptyUIObserverModeResolver].
     */
    fun setObserverModeResolver(resolver: AdaptyUIObserverModeResolver)

    /**
     * Requests an in-app review (cross_platform 4.0.0).
     *
     */
    fun requestAppReview()

    /**
     * Opens a URL natively (cross_platform 4.0.0), honoring [openIn]
     * (`EXTERNAL_BROWSER` → external browser, `IN_APP_BROWSER` → in-app browser).
     *
     * This backs the default `flowViewDidPerformAction` handling of
     * [com.adapty.kmp.models.AdaptyUIAction.OpenUrlAction].
     */
    fun openWebUrl(url: String, openIn: AdaptyWebPresentation = AdaptyWebPresentation.EXTERNAL_BROWSER)


    /**
     * Sets an observer to receive events from AdaptyUI onboardings.
     *
     * Implement [AdaptyUIOnboardingsEventsObserver] to handle onboarding lifecycle events,
     * user interactions, custom actions, and analytics callbacks.
     *
     * Call this method before presenting an onboarding to ensure events are captured.
     *
     * Example:
     * ```
     * Adapty.setOnboardingsEventsObserver(object : AdaptyUIOnboardingsEventsObserver {
     *     override fun onboardingViewOnPaywallAction(
     *         view: AdaptyUIOnboardingView,
     *         meta: AdaptyUIOnboardingMeta,
     *         actionId: String
     *     ) {
     *         // Open a paywall
     *     }
     * })
     * ```
     *
     * @param observer The [AdaptyUIOnboardingsEventsObserver] instance to receive onboarding events.
     *
     * @see AdaptyUIOnboardingsEventsObserver
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    @Suppress("DEPRECATION")
    fun setOnboardingsEventsObserver(observer: AdaptyUIOnboardingsEventsObserver)


    /**
     * Creates a flow view that can be presented to the user (cross_platform 4.0.0).
     *
     * @param flow The [AdaptyFlow] model used to build the view.
     * @return [AdaptyResult] containing the created [AdaptyUIFlowView] or an error.
     */
    suspend fun createFlowView(
        flow: AdaptyFlow,
        loadTimeout: Duration? = null,
        preloadProducts: Boolean = false,
        customTags: Map<String, String>? = null,
        customTimers: Map<String, LocalDateTime>? = null,
        customAssets: Map<String, AdaptyCustomAsset>? = null,
        productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null
    ): AdaptyResult<AdaptyUIFlowView>

    /** Presents the provided flow view (cross_platform 4.0.0). */
    suspend fun presentFlowView(
        view: AdaptyUIFlowView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
    ): AdaptyResult<Unit>

    /** Dismisses the provided flow view (cross_platform 4.0.0). */
    suspend fun dismissFlowView(view: AdaptyUIFlowView): AdaptyResult<Unit>

    /**
     * Creates an onboarding view from the provided [AdaptyOnboarding] model.
     *
     * @param onboarding The onboarding configuration object.
     * @param externalUrlsPresentation Specifies how and where external URLs opened from the onboarding
     * should be presented. Defaults to [AdaptyWebPresentation.IN_APP_BROWSER].
     * @return [AdaptyResult] containing the created [AdaptyUIOnboardingView] or an error.
     *
     * @see AdaptyUIOnboardingView
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    suspend fun createOnboardingView(
        onboarding: AdaptyOnboarding,
        externalUrlsPresentation: AdaptyWebPresentation = AdaptyWebPresentation.IN_APP_BROWSER
    ): AdaptyResult<AdaptyUIOnboardingView>

    /**
     * Presents the provided onboarding view.
     *
     * @param view The onboarding view to present.
     * @return [AdaptyResult] indicating success or error.
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    suspend fun presentOnboardingView(
        view: AdaptyUIOnboardingView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
    ): AdaptyResult<Unit>

    /**
     * Dismisses the currently displayed onboarding view.
     *
     * @param view The onboarding view to dismiss.
     * @return [AdaptyResult] indicating success or error.
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    suspend fun dismissOnboardingView(view: AdaptyUIOnboardingView): AdaptyResult<Unit>

    /**
     * Displays a simple dialog within the context of a paywall or onboarding view.
     *
     * This can be used to show confirmations, alerts, or prompts with
     * primary and optional secondary actions.
     *
     * @param viewId The unique identifier of the view that owns the dialog.
     * @param title The dialog title.
     * @param content The dialog message body.
     * @param primaryActionTitle Text for the primary action button.
     * @param secondaryActionTitle Optional text for the secondary action button.
     *
     * @return [AdaptyResult] with the [AdaptyUIDialogActionType] representing the user’s choice.
     *
     * @see AdaptyUIDialogActionType
     */
    suspend fun showDialog(
        viewId: String,
        title: String,
        content: String,
        primaryActionTitle: String,
        secondaryActionTitle: String? = null
    ): AdaptyResult<AdaptyUIDialogActionType>
}