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
import kotlin.time.Duration.Companion.seconds

public object Adapty : AdaptyContract by adaptyImpl()

internal interface AdaptyContract {
    fun activate(config: AdaptyConfig)
    fun identify(customerUserId: String, onError: (AdaptyError?) -> Unit = {})
    fun updateProfile(params: AdaptyProfileParameters, onError: (AdaptyError?) -> Unit = {})
    fun getProfile(onResult: (Result<AdaptyProfile>) -> Unit)
    fun getPaywall(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywall.FetchPolicy = AdaptyPaywall.FetchPolicy.Default,
        loadTimeout: Duration = 5.seconds,
        onResult: (Result<AdaptyPaywall>) -> Unit,
    )

    fun getPaywallProducts(
        paywall: AdaptyPaywall,
        onResult: (Result<List<AdaptyPaywallProduct>>) -> Unit,
    )

    fun makePurchase(
        product: AdaptyPaywallProduct,
        subscriptionUpdateParams: AdaptySubscriptionUpdateParameters? = null,
        isOfferPersonalized: Boolean = false,
        onResult: (Result<AdaptyPurchaseResult>) -> Unit,
    )

    fun restorePurchases(callback: (Result<AdaptyProfile>) -> Unit)

    fun updateAttribution(
        attribution: Any,
        source: String,
        onError: (AdaptyError?) -> Unit = {},
    )

    fun setIntegrationIdentifier(key: String, value: String, onError: (AdaptyError?) -> Unit = {})

    fun reportTransaction(
        transactionInfo: TransactionInfo,
        variationId: String? = null,
        onResult: (Result<AdaptyProfile>) -> Unit,
    )

    fun logout(onError: (AdaptyError?) -> Unit = {})
    fun setOnProfileUpdatedListener(onProfileUpdatedListener: OnProfileUpdatedListener?)

    var logLevel: AdaptyLogLevel

    fun setLogHandler(logHandler: AdaptyLogHandler)

    fun setFallbackPaywalls(location: FileLocation, onError: (AdaptyError?) -> Unit = {})

    fun logShowPaywall(paywall: AdaptyPaywall, onError: (AdaptyError?) -> Unit = {})

    fun logShowOnboarding(
        name: String?,
        screenName: String?,
        screenOrder: Int,
        onError: (AdaptyError?) -> Unit = {}
    )

    fun getPaywallForDefaultAudience(
        placementId: String,
        locale: String? = null,
        fetchPolicy: AdaptyPaywall.FetchPolicy = AdaptyPaywall.FetchPolicy.Default,
        onResult: (Result<AdaptyPaywall>) -> Unit
    )

    val isActivated: Boolean

}