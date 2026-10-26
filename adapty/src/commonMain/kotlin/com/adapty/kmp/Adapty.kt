@file:Suppress("DEPRECATION") // references the deprecated onboarding API

package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.internal.plugin.constants.Constants.DEFAULT_LOAD_TIMEOUT
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyExternalAttributionProvider
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyFlowPaywall
import com.adapty.kmp.models.AdaptyInstallationStatus
import com.adapty.kmp.models.AdaptyIosRefundPreference
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPromotedProduct
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyStoreMessageType
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlin.time.Duration

/**
 * Main entry point for interacting with the Adapty SDK.
 *
 * The `Adapty` object provides access to all core SDK functionality, including:
 * - Activating and configuring the SDK
 * - Managing user profiles and purchases
 * - Fetching and presenting paywalls and onboardings
 * - Observing subscription updates and installation details
 *
 * Each public method corresponds to a specific SDK capability.
 * Most functions will return [AdaptyResult] that can be used to handle success and errors.
 */
public object Adapty : AdaptyContract by AdaptyImpl(adaptyPlugin = adaptyPlugin)

internal interface AdaptyContract {

    /**
     * Initializes the Adapty SDK with the specified [AdaptyConfig].
     *
     * This should be called once during app startup.
     *
     * @param configuration SDK configuration object.
     * @return [AdaptyResult] indicating success or failure.
     */
    suspend fun activate(configuration: AdaptyConfig): AdaptyResult<Unit>

    /**
     * Associates the current user with a custom user identifier in your system.
     *
     * Use this after registration/login to link the anonymous user with
     * an authenticated account. If you don’t have a user id on SDK configuration,
     * you can set it later at any time with `identify(customerUserId: String)` method.
     * The most common cases are after registration/authorization when the user switches from being
     * an anonymous user to an authenticated user.
     *
     * @return [AdaptyResult] indicating success or failure.
     */
    suspend fun identify(
        customerUserId: String,
        iosAppAccountToken: String? = null,
        androidObfuscatedAccountId: String? = null
    ): AdaptyResult<Unit>

    /**
     * Updates the user's profile attributes such as email, phone, or custom fields.
     * You can then use attributes to create user [segments](https://docs.adapty.io/v2.0/docs/segments)
     * or just view them in CRM.
     *
     * @param params [AdaptyProfileParameters] Profile parameters to update.
     * @return [AdaptyResult] indicating success or failure.
     */
    suspend fun updateProfile(params: AdaptyProfileParameters): AdaptyResult<Unit>

    /**
     * Retrieves the latest user profile.
     *
     * The profile contains subscription info, access levels, and non-subscription purchases.
     * The getProfile method provides the most up-to-date result as it always tries to query the API.
     * If for some reason (e.g. no internet connection), the Adapty SDK fails to retrieve information from the server,
     * the data from cache will be returned. It is also important to note that the Adapty SDK
     * updates AdaptyProfile cache on a regular basis, in order to keep this information as up-to-date as possible.
     *
     * @return [AdaptyResult] containing [AdaptyProfile].
     */
    suspend fun getProfile(): AdaptyResult<AdaptyProfile>

    /**
     * Returns the current installation status of the app.
     *
     * Useful for determining first app launch, or attribution status.
     *
     * @return [AdaptyResult] containing [AdaptyInstallationStatus].
     */
    suspend fun getCurrentInstallationStatus(): AdaptyResult<AdaptyInstallationStatus>

    /**
     * Fetches a flow by placement ID. Read more on the [Adapty Documentation](https://docs.adapty.io/v2.0/docs/displaying-products)
     *
     * @param placementId Identifier of the placement in Adapty Dashboard.
     * @param fetchPolicy Determines whether to fetch from cache or server. By default the SDK will try
     * to load data from the server and will return cached data in case of failure.
     * Otherwise use `[AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad]` to return cached data if it exists.
     * @param loadTimeout Maximum duration to wait for server response.
     * @return [AdaptyResult] containing [AdaptyFlow].
     */
    suspend fun getFlow(
        placementId: String,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default,
        loadTimeout: Duration = DEFAULT_LOAD_TIMEOUT
    ): AdaptyResult<AdaptyFlow>

    /**
     * Retrieves the products for a given flow.
     *
     * @param flow The [AdaptyFlow] object.
     * @return [AdaptyResult] containing a list of [AdaptyPaywallProduct].
     */
    suspend fun getPaywallProducts(flow: AdaptyFlow): AdaptyResult<List<AdaptyPaywallProduct>>

    /**
     * Fetches an onboarding flow by placement ID.
     *
     * @param placementId Identifier of the onboarding placement.
     * @param locale Optional locale for localized content.
     * @param fetchPolicy Fetch strategy (server or cache).
     * @param loadTimeout Maximum duration to wait for server response.
     * @return [AdaptyResult] containing [AdaptyOnboarding].
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    suspend fun getOnboarding(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default,
        loadTimeout: Duration = DEFAULT_LOAD_TIMEOUT
    ): AdaptyResult<AdaptyOnboarding>

    /**
     * Fetches onboarding flow for the default audience.
     * This method enables you to retrieve the onboarding from the Default Audience without
     * having to wait for the Adapty SDK to send all the user information required for segmentation to the server.
     *
     *
     * @param placementId Identifier of the onboarding placement. This is the value you specified when you created the placement in the Adapty Dashboard.
     * @param locale Optional locale.
     * @param fetchPolicy Fetch strategy.
     *
     * @return [AdaptyResult] containing [AdaptyOnboarding].
     */
    @Deprecated(
        "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
        level = DeprecationLevel.WARNING
    )
    suspend fun getOnboardingForDefaultAudience(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default
    ): AdaptyResult<AdaptyOnboarding>


    /**
     * Performs a purchase for the specified product. Read more on the [Adapty Documentation](https://docs.adapty.io/docs/making-purchases)
     *
     * On iOS 26.4+, if the product carries [com.adapty.kmp.models.AdaptyProductSubscription.pricingTerms], its billing plan
     * (e.g. monthly installments of a commitment subscription) is sent along automatically — pick
     * the plan by picking the product. On Android, and on iOS below 26.4, the product is bought
     * up front.
     *
     * @param product The product to purchase.
     * @param parameters Optional parameters for purchase configuration.
     * @return [AdaptyResult] containing [AdaptyPurchaseResult].
     */
    suspend fun makePurchase(
        product: AdaptyPaywallProduct,
        parameters: AdaptyPurchaseParameters? = null
    ): AdaptyResult<AdaptyPurchaseResult>

    /**
     * Completes a purchase that was initiated from the App Store (StoreKit 2 promoted purchase).
     *
     * Call this with the product delivered to [OnPromotedPurchaseListener] once your app is ready
     * to complete the purchase. iOS only — on other platforms the call fails with an error.
     *
     * Pass the product object received by the listener as is: on iOS it carries the StoreKit
     * purchase intent required to complete the purchase (and, on iOS 26.4+, the billing plan from
     * [com.adapty.kmp.models.AdaptyProductSubscription.pricingTerms]), so a copied or hand-built product fails.
     *
     * @param product The promoted product to purchase.
     * @return [AdaptyResult] containing [AdaptyPurchaseResult].
     */
    suspend fun makePromotedPurchase(product: AdaptyPromotedProduct): AdaptyResult<AdaptyPurchaseResult>

    /**
     * Restores previous purchases for the current user.
     *
     * @return [AdaptyResult] containing [AdaptyProfile] with updated access levels.
     * Generally, you have to check only access level status to determine whether the user has premium access to the app.
     */
    suspend fun restorePurchases(): AdaptyResult<AdaptyProfile>

    /**
     * Updates external attribution (conversion) data for the current profile.
     * Read more on the [Adapty Documentation](https://docs.adapty.io/docs/attribution-integration)
     *
     * Renamed from `updateAttribution` in 4.1.0 — `source` is now `provider`.
     *
     * @param attribution Map of key-value attribution data.
     * @param provider [AdaptyExternalAttributionProvider] the attribution provider. Use one of its
     * constants, or construct it with a raw id for providers added after this SDK release.
     * @return [AdaptyResult] indicating success or failure.
     */
    suspend fun updateExternalAttribution(
        attribution: Map<String, Any>,
        provider: AdaptyExternalAttributionProvider
    ): AdaptyResult<Unit>

    /**
     * Sets an integration identifier for the profile.
     *
     * Useful for linking third-party analytics or CRM IDs.
     *
     * @param key Identifier key.
     * @param value Identifier value.
     * @return [AdaptyResult] indicating success or failure.
     */
    suspend fun setIntegrationIdentifier(key: String, value: String): AdaptyResult<Unit>

    /**
     * In Observer mode, Adapty SDK doesn’t know, where the purchase was made from.
     * If you display products using our [Paywalls](https://docs.adapty.io/v2.0/docs/paywall) or [A/B Tests](https://docs.adapty.io/v2.0/docs/ab-test),
     * you can manually assign variation to the purchase. After doing this, you’ll be able to see metrics in Adapty Dashboard.
     *
     * Reports a transaction to Adapty for analytics purposes.
     *
     * @param transactionId Transaction identifier from the store. A string identifier of your purchased transaction
     * [SKPaymentTransaction](https://developer.apple.com/documentation/storekit/skpaymenttransaction) (SK1) or
     * [Transaction](https://developer.apple.com/documentation/storekit/transaction) (SK2) for iOS or
     * string identifier (`purchase.getOrderId()`) of the purchase,
     * where the purchase is an instance of the billing library Purchase class for Android.
     * @param variationId A string identifier of variation. You can get it using variationId property of AdaptyFlow.
     * @return [AdaptyResult] containing [Unit].
     */
    suspend fun reportTransaction(
        transactionId: String,
        variationId: String? = null
    ): AdaptyResult<Unit>

    /** Logs out the current user. */
    suspend fun logout(): AdaptyResult<Unit>

    /**
     * Sets a listener to automatically receive profile updates in your app.
     *
     * Adapty will automatically invoke this listener whenever the user's subscription
     * status changes, and also on app startup with cached profile data (even if offline).
     *
     * Use this to keep your app’s UI or local data in sync with the latest subscription state.
     *
     * Passing `null` removes the existing listener.
     *
     * Example:
     * ```
     * Adapty.setOnProfileUpdatedListener(OnProfileUpdatedListener { profile ->
     *     // Handle updated profile data here
     * })
     * ```
     *
     * @param onProfileUpdatedListener the listener that receives profile updates,
     * or `null` to remove the current listener.
     *
     * @see OnProfileUpdatedListener
     */
    fun setOnProfileUpdatedListener(onProfileUpdatedListener: OnProfileUpdatedListener?)

    /**
     * Sets a listener that receives StoreKit 2 promoted purchases (iOS only).
     *
     * Called when the user starts a purchase directly from the App Store product page. Complete
     * it by calling [makePromotedPurchase] with the received product when your app is ready.
     *
     * Register this to support promoted purchases at all. A purchase that arrives before you
     * register is held and delivered on registration — see [OnPromotedPurchaseListener].
     *
     * Passing `null` removes the existing listener.
     *
     * @param onPromotedPurchaseListener the listener that receives promoted products,
     * or `null` to remove the current listener.
     *
     * @see OnPromotedPurchaseListener
     */
    fun setOnPromotedPurchaseListener(onPromotedPurchaseListener: OnPromotedPurchaseListener?)

    /**
     * Sets a listener to receive installation details related to
     * Adapty’s **User Acquisition** feature.
     *
     * User Acquisition helps you connect ad spend with subscription revenue,
     * giving you a complete view of your app’s economy in one place.
     *
     * > This is a one-way integration — to view revenue data in User Acquisition,
     * > you must enable the integration in the Adapty Dashboard under
     * > **Integrations → Adapty**, and turn on the toggle.
     * > No API keys, tokens, or identifiers are required — just update
     * > and configure the Adapty SDK.
     *
     * Once events start firing, you can view:
     * - Event name
     * - Status
     * - Environment
     * - Date/time
     *
     * Adapty sends three main groups of events by default:
     * - Trials
     * - Subscriptions
     * - Issues
     *
     * For a full list of supported events, see:
     * [Adapty Events Documentation](https://adapty.io/docs/events)
     *
     * Passing `null` removes the existing listener.
     *
     * Example:
     * ```
     * Adapty.setOnInstallationDetailsListener(object : OnInstallationDetailsListener {
     *     override fun onInstallationDetailsSuccess(details: AdaptyInstallationDetails) {
     *         // Handle successful installation details retrieval
     *     }
     *
     *     override fun onInstallationDetailsFailure(error: AdaptyError) {
     *         // Handle error
     *     }
     * })
     * ```
     *
     * @param onInstallationDetailsListener the listener that receives installation details updates,
     * or `null` to remove the current listener.
     *
     * @see OnInstallationDetailsListener
     */
    fun setOnInstallationDetailsListener(onInstallationDetailsListener: OnInstallationDetailsListener?)


    /** Sets the SDK log level. */
    fun setLogLevel(logLevel: AdaptyLogLevel)

    /**
     * Sets fallback paywalls from a local asset. You should pass exactly the same payload
     * you’re getting from Adapty backend. You can copy it from Adapty Dashboard. Adapty allows
     * you to provide fallback paywalls that will be used when a user opens the app for the
     * first time and there’s no internet connection or in the rare case when Adapty backend is down
     * and there’s no cache on the device.
     *
     * Read more on the [Adapty Documentation](https://docs.adapty.io/v2.0/docs/ios-displaying-products#fallback-paywalls)
     *
     */
    suspend fun setFallback(assetId: String): AdaptyResult<Unit>

    /**
     * Logs a flow view for analytics purposes.
     * Call this method to notify the Adapty SDK that a particular flow was shown to the user.
     * Adapty helps you measure the performance of your flows: we automatically collect all the
     * metrics related to purchases except for views, because only you know when a flow was shown
     * to a customer. Whenever you show a flow, call `logShowFlow(flow)` to log the event, and it
     * will be accumulated in the metrics.
     *
     * Read more on the [Adapty Documentation](https://docs.adapty.io/v2.0/docs/ios-displaying-products#paywall-analytics)
     *
     * @param flow The [AdaptyFlow] that was shown to the user.
     */
    suspend fun logShowFlow(flow: AdaptyFlow): AdaptyResult<Unit>

    /**
     * Fetches a flow for the default audience.
     * This method enables you to retrieve the flow from the Default Audience without having to wait
     * for the Adapty SDK to send all the user information required for segmentation to the server.
     *
     * @param placementId Identifier of the placement. This is the value you specified when you created the placement in the Adapty Dashboard.
     * @param fetchPolicy Fetch strategy.
     * @return [AdaptyResult] containing [AdaptyFlow].
     */
    suspend fun getFlowForDefaultAudience(
        placementId: String,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default
    ): AdaptyResult<AdaptyFlow>

    /**
     * Warms up the cache for the given flow placements.
     *
     * Sends the same request as [getFlow] for each placement — all placements are loaded in
     * parallel — and stores the result in the SDK cache. Nothing is returned. This is useful,
     * for example, for cross-placement A/B tests, where you want the flows for several
     * placements to be available before the user reaches them.
     *
     * What preloading does **not** do:
     * - It does not load UI configurations (view configs) or media (e.g. paywall images) in the
     *   background — only the placement data is cached, so the first flow view creation still
     *   goes to the network for those.
     * - It has no fetch policy: preloading itself always goes to the network.
     *
     * To benefit from the warmed cache, call [getFlow] with a cache-using fetch policy passed
     * **explicitly** — [AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad] or
     * [AdaptyPaywallFetchPolicy.ReturnCacheDataIfNotExpiredElseLoad]. Then [getFlow] is served
     * from the cache and sends one request fewer. With the default policy
     * ([AdaptyPaywallFetchPolicy.ReloadRevalidatingCacheData]) [getFlow] still goes to the
     * network, and the preloaded data only serves as a fallback.
     *
     * Example:
     * ```
     * Adapty.preloadFlows(placementIds = listOf("onboarding_flow", "settings_paywall"))
     *
     * val flow = Adapty.getFlow(
     *     placementId = "onboarding_flow",
     *     fetchPolicy = AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad
     * )
     * ```
     *
     * Requires the SDK to be activated.
     *
     * @param placementIds Identifiers of the placements to preload, as specified in the Adapty Dashboard.
     * @param loadTimeout Maximum duration to wait for server response.
     * @return [AdaptyResult] that succeeds once every placement has been stored in the cache.
     * Fails with a single aggregated [com.adapty.kmp.models.AdaptyError] if any placement failed
     * to load; the placements that did load stay in the cache.
     */
    suspend fun preloadFlows(
        placementIds: List<String>,
        loadTimeout: Duration = DEFAULT_LOAD_TIMEOUT
    ): AdaptyResult<Unit>

    /**
     * Warms up the cache for the given flow placements using the Default Audience (All Users).
     *
     * The same trade-offs as [getFlowForDefaultAudience] apply: faster fetching without waiting
     * for the user information required for segmentation, but no targeting by country,
     * attribution or custom attributes. Unlike [preloadFlows], there is no load timeout.
     *
     * See [preloadFlows] for what preloading does and does not cover — including that a later
     * flow fetch only reads the warmed cache when [AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad]
     * or [AdaptyPaywallFetchPolicy.ReturnCacheDataIfNotExpiredElseLoad] is passed explicitly.
     *
     * Requires the SDK to be activated.
     *
     * @param placementIds Identifiers of the placements to preload, as specified in the Adapty Dashboard.
     * @return [AdaptyResult] that succeeds once every placement has been stored in the cache.
     * Fails with a single aggregated [com.adapty.kmp.models.AdaptyError] if any placement failed
     * to load; the placements that did load stay in the cache.
     */
    suspend fun preloadFlowsForDefaultAudience(
        placementIds: List<String>
    ): AdaptyResult<Unit>

    /** Checks whether the SDK is activated. */
    suspend fun isActivated(): Boolean

    /**
     * Creates a URL for a web paywall from an [AdaptyFlowPaywall].
     *
     * The URL is generated from the paywall without attaching product data. This is useful when
     * the products configured in the Adapty paywall differ from those used on the web.
     */
    suspend fun createWebPaywallUrl(flowPaywall: AdaptyFlowPaywall): AdaptyResult<String>

    /**
     * Creates a URL for a web paywall from a specific [AdaptyPaywallProduct].
     *
     * The generated URL includes the product data.
     */
    suspend fun createWebPaywallUrl(product: AdaptyPaywallProduct): AdaptyResult<String>

    /**
     * Opens a web-based paywall from an [AdaptyFlowPaywall].
     *
     * The SDK generates a web URL and opens it either in the device's external browser or in an
     * in-app browser, without attaching product data.
     *
     * @param flowPaywall The flow paywall used to generate the web URL.
     * @param openIn Where the web paywall should be opened. Defaults to [AdaptyWebPresentation.EXTERNAL_BROWSER].
     * @return [AdaptyResult] indicating whether the paywall was successfully opened.
     */
    suspend fun openWebPaywall(
        flowPaywall: AdaptyFlowPaywall,
        openIn: AdaptyWebPresentation = AdaptyWebPresentation.EXTERNAL_BROWSER
    ): AdaptyResult<Unit>

    /**
     * Opens a web-based paywall for a specific [AdaptyPaywallProduct]. The generated URL includes
     * the product data.
     *
     * @param product The specific product to open.
     * @param openIn Where the web paywall should be opened. Defaults to [AdaptyWebPresentation.EXTERNAL_BROWSER].
     * @return [AdaptyResult] indicating whether the paywall was successfully opened.
     */
    suspend fun openWebPaywall(
        product: AdaptyPaywallProduct,
        openIn: AdaptyWebPresentation = AdaptyWebPresentation.EXTERNAL_BROWSER
    ): AdaptyResult<Unit>

    /** Ios ONLY. Presents a code redemption sheet on iOS that enables the user to redeem codes provided by your app. */
    suspend fun presentCodeRedemptionSheet(): AdaptyResult<Unit>

    /** Ios ONLY. Updates the refund preference for iOS. */
    suspend fun updateRefundPreference(preference: AdaptyIosRefundPreference): AdaptyResult<Boolean>

    /** Ios ONLY. Updates the user's consent for collecting refund data on iOS. */
    suspend fun updateCollectingRefundDataConsent(consent: Boolean): AdaptyResult<Boolean>

    /**
     * Gets the types of store messages currently waiting in the SDK queue.
     *
     * iOS 16+ only. The queue is kept only when the SDK is activated with
     * [com.adapty.kmp.models.AdaptyStoreMessagesHandling.MANUAL]
     * (see [AdaptyConfig.Builder.withStoreMessagesHandling]); in `AUTO` mode and before activation the
     * result is always empty. The result is a snapshot of unique types in no particular order — there
     * is no event for newly arrived messages.
     *
     * An empty list means [showStoreMessages] has nothing to show right now. `null` means the store
     * cannot report pending messages: on Android Google Play decides on show, so this returns
     * `AdaptyResult.Success(null)` without calling the native SDK — call [showStoreMessages] there.
     * Below iOS 16 the native method is unavailable and the call returns an error.
     *
     * Example:
     * ```
     * Adapty.getPendingStoreMessageTypes().onSuccess { types ->
     *     when {
     *         // The store can't report pending messages (Android): let it decide
     *         types == null -> Adapty.showStoreMessages()
     *         AdaptyStoreMessageType.BILLING_ISSUE in types -> {
     *             // Explain the billing issue in your own UI first, then show the system message
     *             Adapty.showStoreMessages(iosFilter = listOf(AdaptyStoreMessageType.BILLING_ISSUE))
     *         }
     *     }
     * }
     * ```
     *
     * @return [AdaptyResult] with the pending types, or `null` where the store cannot report them (Android).
     */
    suspend fun getPendingStoreMessageTypes(): AdaptyResult<List<AdaptyStoreMessageType>?>

    /**
     * Shows pending store messages: App Store messages on iOS, Google Play in-app messages on Android.
     *
     * Intended for [com.adapty.kmp.models.AdaptyStoreMessagesHandling.MANUAL]
     * (see [AdaptyConfig.Builder.withStoreMessagesHandling]), but works in both modes.
     *
     * **iOS (16+):** messages are shown one by one; a successfully shown message leaves the queue, a
     * message that failed to show stays for a retry. Pass `null` [iosFilter] (the default) to show
     * every pending message — including types unknown to the SDK — or an empty list to show none.
     * Errors: [com.adapty.kmp.models.AdaptyErrorCode.OPERATION_IN_PROGRESS] if another show is
     * running, [com.adapty.kmp.models.AdaptyErrorCode.RESOLVER_FAILURE] if no foreground window scene
     * is available. Below iOS 16 the native method is unavailable and the call returns an error.
     *
     * **Android:** shows every applicable Google Play in-app message; [iosFilter] is ignored and not
     * sent. Google Play decides whether a message exists: there are no test messages, so this can't be
     * verified with a test account, and in production a message is shown at most about once per day.
     *
     * Example:
     * ```
     * // Show everything that is pending, e.g. after onboarding is finished
     * Adapty.showStoreMessages()
     *
     * // iOS: show just the price increase consent; Android shows everything
     * Adapty.showStoreMessages(iosFilter = listOf(AdaptyStoreMessageType.PRICE_INCREASE_CONSENT))
     * ```
     *
     * @param iosFilter iOS only. The message types to show, or `null` to show all pending messages.
     * @return [AdaptyResult] that succeeds once the messages have been processed.
     */
    suspend fun showStoreMessages(iosFilter: List<AdaptyStoreMessageType>? = null): AdaptyResult<Unit>

}