package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.internal.plugin.constants.Constants.DEFAULT_LOAD_TIMEOUT
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyInstallationStatus
import com.adapty.kmp.models.AdaptyIosRefundPreference
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyResult
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
     * Fetches a paywall by placement ID. Read more on the [Adapty Documentation](https://docs.adapty.io/v2.0/docs/displaying-products)
     *
     * @param placementId Identifier of the paywall placement in Adapty Dashboard.
     * @param locale Optional locale for localized content. (https://docs.adapty.io/docs/paywall#localizations).
     * @param fetchPolicy Determines whether to fetch from cache or server. by default SDK will try
     * to load data from server and will return cached data in case of failure.
     * Otherwise use `[AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad]` to return cached data if it exists
     * @param loadTimeout Maximum duration to wait for server response.
     * @return [AdaptyResult] containing [AdaptyPaywall].
     */
    suspend fun getPaywall(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default,
        loadTimeout: Duration = DEFAULT_LOAD_TIMEOUT
    ): AdaptyResult<AdaptyPaywall>

    /**
     * Retrieves the products for a given paywall.
     *
     * @param paywall The [AdaptyPaywall] object.
     * @return [AdaptyResult] containing a list of [AdaptyPaywallProduct].
     */
    suspend fun getPaywallProducts(paywall: AdaptyPaywall): AdaptyResult<List<AdaptyPaywallProduct>>

    /**
     * Fetches an onboarding flow by placement ID.
     *
     * @param placementId Identifier of the onboarding placement.
     * @param locale Optional locale for localized content.
     * @param fetchPolicy Fetch strategy (server or cache).
     * @param loadTimeout Maximum duration to wait for server response.
     * @return [AdaptyResult] containing [AdaptyOnboarding].
     */
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
    suspend fun getOnboardingForDefaultAudience(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default
    ): AdaptyResult<AdaptyOnboarding>


    /**
     * Performs a purchase for the specified product. Read more on the [Adapty Documentation](https://docs.adapty.io/docs/making-purchases)
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
     * Restores previous purchases for the current user.
     *
     * @return [AdaptyResult] containing [AdaptyProfile] with updated access levels.
     * Generally, you have to check only access level status to determine whether the user has premium access to the app.
     */
    suspend fun restorePurchases(): AdaptyResult<AdaptyProfile>

    /**
     * Updates attribution (conversion) data for the current profile.
     * Read more on the [Adapty Documentation](https://docs.adapty.io/docs/attribution-integration)
     *
     * @param attribution Map of key-value attribution data.
     * @param source Source of attribution (e.g., "Facebook", "AppsFlyer").
     * @return [AdaptyResult] indicating success or failure.
     */
    suspend fun updateAttribution(attribution: Map<String, Any>, source: String): AdaptyResult<Unit>

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
     * @param variationId A string identifier of variation. You can get it using variationId property of AdaptyPaywall.
     * @return [AdaptyResult] containing [AdaptyProfile].
     */
    suspend fun reportTransaction(
        transactionId: String,
        variationId: String? = null
    ): AdaptyResult<AdaptyProfile>

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
     * Logs a paywall view for analytics purposes.
     * Call this method to notify Adapty SDK, that particular paywall was shown to user.
     * Adapty helps you to measure the performance of the paywalls.
     * We automatically collect all the metrics related to purchases except for paywall views.
     * This is because only you know when the paywall was shown to a customer.
     * Whenever you show a paywall to your user, call .logShowPaywall(paywall) to log the event,
     * and it will be accumulated in the paywall metrics.
     *
     * Read more on the [Adapty Documentation](https://docs.adapty.io/v2.0/docs/ios-displaying-products#paywall-analytics)
     * */
    suspend fun logShowPaywall(paywall: AdaptyPaywall): AdaptyResult<Unit>

    /**
     * Fetches paywall for the default audience.
     * This method enables you to retrieve the paywall from the Default Audience without
     * having to wait for the Adapty SDK to send all the user information required for segmentation to the server.
     *
     *
     * @param placementId Identifier of the paywall placement. This is the value you specified when you created the placement in the Adapty Dashboard.
     * @param locale Optional locale.
     * @param fetchPolicy Fetch strategy.
     *
     * @return [AdaptyResult] containing [AdaptyOnboarding].
     */
    suspend fun getPaywallForDefaultAudience(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default
    ): AdaptyResult<AdaptyPaywall>

    /** Checks whether the SDK is activated. */
    suspend fun isActivated(): Boolean

    /** Creates a URL for a web paywall or product. */
    suspend fun createWebPaywallUrl(
        paywall: AdaptyPaywall? = null,
        product: AdaptyPaywallProduct? = null
    ): AdaptyResult<String>

    /**
     * Opens a web paywall or product.
     * openWebPaywall(product) that generates URLs by paywall and adds the product data to URLs as well.
     * openWebPaywall(paywall) that generates URLs by paywall without adding the product data to URLs.
     * Use it when your products in the Adapty paywall differ from those in the web paywall.
     * */
    suspend fun openWebPaywall(
        paywall: AdaptyPaywall? = null,
        product: AdaptyPaywallProduct? = null
    ): AdaptyResult<Unit>

    /** Ios ONLY. Presents a code redemption sheet on iOS that enables the user to redeem codes provided by your app. */
    suspend fun presentCodeRedemptionSheet(): AdaptyResult<Unit>

    /** Ios ONLY. Updates the refund preference for iOS. */
    suspend fun updateRefundPreference(preference: AdaptyIosRefundPreference): AdaptyResult<Boolean>

    /** Ios ONLY. Updates the user's consent for collecting refund data on iOS. */
    suspend fun updateCollectingRefundDataConsent(consent: Boolean): AdaptyResult<Boolean>

}