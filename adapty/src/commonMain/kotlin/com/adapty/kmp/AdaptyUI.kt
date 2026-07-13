package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIFlowView
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPaywallView
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration

/**
 * Singleton for managing Adapty UI components such as paywalls and onboardings.
 *
 * This interface is responsible for creating, presenting, and dismissing UI views,
 * as well as observing user interactions and lifecycle events for analytics and customization.
 *
 * Typical usage:
 * - Create and display paywall or onboarding views
 * - Register observers for UI events
 * - Handle dialog interactions
 *
 * @see AdaptyUIPaywallsEventsObserver
 * @see AdaptyUIOnboardingsEventsObserver
 * @see AdaptyUIPaywallView
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
    fun unregisterOnboardingEventsListener(viewId: String)

    @Deprecated(
        "Use registerFlowEventsListener(observer, viewId) as of 4.0.0.",
        ReplaceWith("registerFlowEventsListener(observer, viewId)"),
        DeprecationLevel.WARNING
    )
    fun registerPaywallEventsListener(
        observer: AdaptyUIPaywallsEventsObserver,
        viewId: String
    )

    @Deprecated(
        "Use unregisterFlowEventsListener(viewId) as of 4.0.0.",
        ReplaceWith("unregisterFlowEventsListener(viewId)"),
        DeprecationLevel.WARNING
    )
    fun unregisterPaywallEventsListener(viewId: String)

    /**
     * Registers an [AdaptyUIFlowsEventsObserver] for a specific flow view (cross_platform 4.0.0).
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
     * Sets an observer to receive events from paywalls displayed.
     *
     * Implement [AdaptyUIPaywallsEventsObserver] to handle lifecycle events and user interactions
     * within paywalls — for example, when the view appears, disappears, starts or finishes a purchase,
     * performs an action, or encounters an error.
     *
     * This method should be called before displaying a paywall to ensure all events are captured.
     *
     * Example:
     * ```
     * Adapty.setPaywallsEventsObserver(object : AdaptyUIPaywallsEventsObserver {
     *     override fun paywallViewDidFinishPurchase(
     *         view: AdaptyUIPaywallView,
     *         product: AdaptyPaywallProduct,
     *         purchaseResult: AdaptyPurchaseResult
     *     ) {
     *         // Handle successful purchase or dismissal here
     *     }
     * })
     * ```
     *
     * @param observer an instance of [AdaptyUIPaywallsEventsObserver] used to receive paywall lifecycle and interaction events.
     *
     * @see AdaptyUIPaywallsEventsObserver
     */
    @Deprecated(
        "Use setFlowsEventsObserver(observer) as of 4.0.0.",
        ReplaceWith("setFlowsEventsObserver(observer)"),
        DeprecationLevel.WARNING
    )
    fun setPaywallsEventsObserver(observer: AdaptyUIPaywallsEventsObserver)


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
    fun setOnboardingsEventsObserver(observer: AdaptyUIOnboardingsEventsObserver)


    /**
     * Creates a paywall view that can be presented to the user.
     *
     * @param paywall The [AdaptyPaywall] model used to build the view.
     * @param loadTimeout Optional timeout for loading the view.
     * @param preloadProducts If true, paywall products are preloaded before presentation.
     * @param customTags Optional custom tags to inject into the paywall.
     * @param customTimers Optional custom timers to pass for paywall rendering.
     * @param customAssets Optional map of asset identifiers to custom assets.
     * @param productPurchaseParams Optional parameters for product purchase flow.
     *
     * @return [AdaptyResult] containing the created [AdaptyUIPaywallView] or an error.
     *
     * @see AdaptyUIPaywallView
     */
    @Deprecated(
        "Use createFlowView(flow, ...) as of 4.0.0.",
        ReplaceWith("createFlowView(flow, loadTimeout, preloadProducts, customTags, customTimers, customAssets, productPurchaseParams)"),
        DeprecationLevel.WARNING
    )
    suspend fun createPaywallView(
        paywall: AdaptyPaywall,
        loadTimeout: Duration? = null,
        preloadProducts: Boolean = false,
        customTags: Map<String, String>? = null,
        customTimers: Map<String, LocalDateTime>? = null,
        customAssets: Map<String, AdaptyCustomAsset>? = null,
        productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null
    ): AdaptyResult<AdaptyUIPaywallView>

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
     * Presents the provided paywall view.
     *
     * @param view The paywall view to present.
     * @return [AdaptyResult] indicating success or error.
     */
    @Deprecated(
        "Use presentFlowView(view, ...) as of 4.0.0.",
        ReplaceWith("presentFlowView(view, iosPresentationStyle)"),
        DeprecationLevel.WARNING
    )
    suspend fun presentPaywallView(
        view: AdaptyUIPaywallView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
    ): AdaptyResult<Unit>

    /**
     * Dismisses the currently displayed paywall view.
     *
     * @param view The paywall view to dismiss.
     * @return [AdaptyResult] indicating success or error.
     */
    @Deprecated(
        "Use dismissFlowView(view) as of 4.0.0.",
        ReplaceWith("dismissFlowView(view)"),
        DeprecationLevel.WARNING
    )
    suspend fun dismissPaywallView(view: AdaptyUIPaywallView): AdaptyResult<Unit>

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