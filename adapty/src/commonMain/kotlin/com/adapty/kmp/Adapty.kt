package com.adapty.kmp

import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateParameters
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyIosRefundPreference
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyResult
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

public object Adapty : AdaptyContract by AdaptyImpl(adaptyPlugin = adaptyPlugin)

internal interface AdaptyContract {
    suspend fun activate(configuration: AdaptyConfig): AdaptyResult<Unit>
    suspend fun identify(customerUserId: String): AdaptyResult<Unit>
    suspend fun updateProfile(params: AdaptyProfileParameters): AdaptyResult<Unit>
    suspend fun getProfile(): AdaptyResult<AdaptyProfile>
    suspend fun getPaywall(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default,
        loadTimeout: Duration = 5.seconds
    ): AdaptyResult<AdaptyPaywall>

    suspend fun getPaywallProducts(paywall: AdaptyPaywall): AdaptyResult<List<AdaptyPaywallProduct>>

    suspend fun makePurchase(
        product: AdaptyPaywallProduct,
        subscriptionUpdateParams: AdaptyAndroidSubscriptionUpdateParameters? = null,
        isOfferPersonalized: Boolean = false
    ): AdaptyResult<AdaptyPurchaseResult>

    suspend fun restorePurchases(): AdaptyResult<AdaptyProfile>

    suspend fun updateAttribution(attribution: Map<String, Any>, source: String): AdaptyResult<Unit>

    suspend fun setIntegrationIdentifier(key: String, value: String): AdaptyResult<Unit>

    suspend fun reportTransaction(
        transactionId: String,
        variationId: String? = null
    ): AdaptyResult<AdaptyProfile>

    suspend fun logout(): AdaptyResult<Unit>
    fun setOnProfileUpdatedListener(onProfileUpdatedListener: OnProfileUpdatedListener?)

    fun setLogLevel(logLevel: AdaptyLogLevel)

    suspend fun setFallbackPaywalls(assetId: String): AdaptyResult<Unit>

    suspend fun logShowPaywall(paywall: AdaptyPaywall): AdaptyResult<Unit>

    suspend fun getPaywallForDefaultAudience(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default
    ): AdaptyResult<AdaptyPaywall>

    suspend fun isActivated(): Boolean

    suspend fun createWebPaywallUrl(
        paywall: AdaptyPaywall? = null,
        product: AdaptyPaywallProduct? = null
    ): AdaptyResult<String>

    suspend fun openWebPaywall(
        paywall: AdaptyPaywall? = null,
        product: AdaptyPaywallProduct? = null,
        onError: (AdaptyError?) -> Unit = {}
    )

    suspend fun presentCodeRedemptionSheet(onError: (AdaptyError?) -> Unit = {})
    suspend fun updateRefundPreference(preference: AdaptyIosRefundPreference): AdaptyResult<Boolean>
    suspend fun updateCollectingRefundDataConsent(consent: Boolean): AdaptyResult<Boolean>

}