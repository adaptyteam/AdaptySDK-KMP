package com.adapty.kmp

import com.adapty.kmp.errors.AdaptyError
import com.adapty.kmp.listeners.OnProfileUpdatedListener
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptySubscriptionUpdateParameters
import com.adapty.kmp.utils.AdaptyLogHandler
import com.adapty.kmp.utils.AdaptyLogLevel
import com.adapty.kmp.utils.FileLocation
import com.adapty.kmp.utils.TransactionInfo
import kotlin.time.Duration

internal class AdaptyImpl : AdaptyContract {
    override fun activate(config: AdaptyConfig) {

    }

    override fun identify(customerUserId: String, onError: (AdaptyError?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateProfile(params: AdaptyProfileParameters, onError: (AdaptyError?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getProfile(onResult: (Result<AdaptyProfile>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getPaywall(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywall.FetchPolicy,
        loadTimeout: Duration,
        onResult: (Result<AdaptyPaywall>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getPaywallProducts(
        paywall: AdaptyPaywall,
        onResult: (Result<List<AdaptyPaywallProduct>>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun makePurchase(
        product: AdaptyPaywallProduct,
        subscriptionUpdateParams: AdaptySubscriptionUpdateParameters?,
        isOfferPersonalized: Boolean,
        onResult: (Result<AdaptyPurchaseResult>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun restorePurchases(callback: (Result<AdaptyProfile>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateAttribution(
        attribution: Any,
        source: String,
        onError: (AdaptyError?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun setIntegrationIdentifier(
        key: String,
        value: String,
        onError: (AdaptyError?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun reportTransaction(
        transactionInfo: TransactionInfo,
        variationId: String?,
        onResult: (Result<AdaptyProfile>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun logout(onError: (AdaptyError?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun setOnProfileUpdatedListener(onProfileUpdatedListener: OnProfileUpdatedListener?) {
        TODO("Not yet implemented")
    }

    override var logLevel: AdaptyLogLevel
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun setLogHandler(logHandler: AdaptyLogHandler) {
        TODO("Not yet implemented")
    }

    override fun setFallbackPaywalls(location: FileLocation, onError: (AdaptyError?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun logShowPaywall(paywall: AdaptyPaywall, onError: (AdaptyError?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun logShowOnboarding(
        name: String?,
        screenName: String?,
        screenOrder: Int,
        onError: (AdaptyError?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getPaywallForDefaultAudience(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywall.FetchPolicy,
        onResult: (Result<AdaptyPaywall>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override val isActivated: Boolean
        get() = TODO("Not yet implemented")
}