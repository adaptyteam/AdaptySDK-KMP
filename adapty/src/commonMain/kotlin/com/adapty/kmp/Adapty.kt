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

public object Adapty : AdaptyContract by AdaptyImpl(adaptyPlugin = adaptyPlugin)

internal interface AdaptyContract {
    suspend fun activate(configuration: AdaptyConfig): AdaptyResult<Unit>
    suspend fun identify(
        customerUserId: String,
        iosAppAccountToken: String? = null,
        androidObfuscatedAccountId: String? = null
    ): AdaptyResult<Unit>

    suspend fun updateProfile(params: AdaptyProfileParameters): AdaptyResult<Unit>
    suspend fun getProfile(): AdaptyResult<AdaptyProfile>
    suspend fun getCurrentInstallationStatus(): AdaptyResult<AdaptyInstallationStatus>
    suspend fun getPaywall(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default,
        loadTimeout: Duration = DEFAULT_LOAD_TIMEOUT
    ): AdaptyResult<AdaptyPaywall>

    suspend fun getPaywallProducts(paywall: AdaptyPaywall): AdaptyResult<List<AdaptyPaywallProduct>>

    suspend fun getOnboarding(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default,
        loadTimeout: Duration = DEFAULT_LOAD_TIMEOUT
    ): AdaptyResult<AdaptyOnboarding>

    suspend fun getOnboardingForDefaultAudience(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywallFetchPolicy = AdaptyPaywallFetchPolicy.Default
    ): AdaptyResult<AdaptyOnboarding>

    suspend fun makePurchase(
        product: AdaptyPaywallProduct,
        parameters: AdaptyPurchaseParameters? = null
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
    fun setOnInstallationDetailsListener(onInstallationDetailsListener: OnInstallationDetailsListener?)

    fun setLogLevel(logLevel: AdaptyLogLevel)

    suspend fun setFallback(assetId: String): AdaptyResult<Unit>

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
        product: AdaptyPaywallProduct? = null
    ): AdaptyResult<Unit>

    suspend fun presentCodeRedemptionSheet(): AdaptyResult<Unit>
    suspend fun updateRefundPreference(preference: AdaptyIosRefundPreference): AdaptyResult<Boolean>
    suspend fun updateCollectingRefundDataConsent(consent: Boolean): AdaptyResult<Boolean>

}