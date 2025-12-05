package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPaywallView
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

    fun registerPaywallEventsListener(
        observer: AdaptyUIPaywallsEventsObserver,
        viewId: String
    )

    fun unregisterPaywallEventsListener(viewId: String)


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
     * Creates an onboarding view from the provided [AdaptyOnboarding] model.
     *
     * @param onboarding The onboarding configuration object.
     * @return [AdaptyResult] containing the created [AdaptyUIOnboardingView] or an error.
     *
     * @see AdaptyUIOnboardingView
     */
    suspend fun createOnboardingView(onboarding: AdaptyOnboarding): AdaptyResult<AdaptyUIOnboardingView>

    /**
     * Presents the provided onboarding view.
     *
     * @param view The onboarding view to present.
     * @return [AdaptyResult] indicating success or error.
     */
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
    suspend fun dismissPaywallView(view: AdaptyUIPaywallView): AdaptyResult<Unit>

    /**
     * Dismisses the currently displayed onboarding view.
     *
     * @param view The onboarding view to dismiss.
     * @return [AdaptyResult] indicating success or error.
     */
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