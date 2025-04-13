package com.adapty.kmp

import android.content.Context
import com.adapty.kmp.errors.AdaptyError
import com.adapty.kmp.errors.asAdaptyError
import com.adapty.kmp.listeners.OnProfileUpdatedListener
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptySubscriptionUpdateParameters
import com.adapty.kmp.models.asAdaptyConfigAndroid
import com.adapty.kmp.models.asAdaptyPaywall
import com.adapty.kmp.models.asAdaptyPaywallFetchPolicy
import com.adapty.kmp.models.asAdaptyPaywallProduct
import com.adapty.kmp.models.asAdaptyProfile
import com.adapty.kmp.models.asAdaptyProfileParametersAndroid
import com.adapty.kmp.utils.AdaptyLogHandler
import com.adapty.kmp.utils.AdaptyLogLevel
import com.adapty.kmp.utils.FileLocation
import com.adapty.kmp.utils.TransactionInfo
import com.adapty.kmp.utils.asAdaptyFileLocationAndroid
import com.adapty.kmp.utils.asAdaptyLogHandlerAndroid
import com.adapty.kmp.utils.asAdaptyLogLevel
import com.adapty.kmp.utils.asAdaptyLogLevelAndroid
import com.adapty.kmp.utils.asAdaptyTransactionInfoAndroid
import com.adapty.kmp.utils.asResult
import kotlin.time.Duration
import com.adapty.Adapty as AdaptyAndroid
import com.adapty.utils.TimeInterval as AdaptyTimeIntervalAndroid

internal class AdaptyImpl(private val context: Context) : AdaptyContract {
    override fun activate(config: AdaptyConfig) {
        AdaptyAndroid.activate(
            context = context,
            config = config.asAdaptyConfigAndroid()
        )
    }

    override fun identify(customerUserId: String, onError: (AdaptyError?) -> Unit) {
        AdaptyAndroid.identify(
            customerUserId = customerUserId,
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }

    override fun updateProfile(params: AdaptyProfileParameters, onError: (AdaptyError?) -> Unit) {
        AdaptyAndroid.updateProfile(
            params = params.asAdaptyProfileParametersAndroid(),
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }

    override fun getProfile(onResult: (Result<AdaptyProfile>) -> Unit) {
        AdaptyAndroid.getProfile(
            callback = { adaptyAndroidResult ->
                val result = adaptyAndroidResult.asResult { it.asAdaptyProfile() }
                onResult(result)
            }
        )
    }

    override fun getPaywall(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywall.FetchPolicy,
        loadTimeout: Duration,
        onResult: (Result<AdaptyPaywall>) -> Unit
    ) {
        AdaptyAndroid.getPaywall(
            placementId = placementId,
            locale = locale,
            fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicy(),
            loadTimeout = AdaptyTimeIntervalAndroid.from(duration = loadTimeout),
            callback = { adaptyAndroidResult ->
                val result = adaptyAndroidResult.asResult { it.asAdaptyPaywall() }
                onResult(result)
            }
        )
    }

    override fun getPaywallProducts(
        paywall: AdaptyPaywall,
        onResult: (Result<List<AdaptyPaywallProduct>>) -> Unit
    ) {
        AdaptyAndroid.getPaywallProducts(
            paywall = paywall.asAdaptyPaywall(),
            callback = { adaptyAndroidResult ->
                val result = adaptyAndroidResult.asResult { list ->
                    list.map { adaptyPaywallProductAndroid ->
                        adaptyPaywallProductAndroid.asAdaptyPaywallProduct()
                    }
                }
                onResult(result)
            }
        )
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
        AdaptyAndroid.restorePurchases(
            callback = { adaptyAndroidResult ->
                val result = adaptyAndroidResult.asResult { it.asAdaptyProfile() }
                callback(result)
            }
        )
    }

    override fun updateAttribution(
        attribution: Any,
        source: String,
        onError: (AdaptyError?) -> Unit
    ) {
        AdaptyAndroid.updateAttribution(
            attribution = attribution,
            source = source,
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }

    override fun setIntegrationIdentifier(
        key: String,
        value: String,
        onError: (AdaptyError?) -> Unit
    ) {
        AdaptyAndroid.setIntegrationIdentifier(
            key = key,
            value = value,
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }

    override fun reportTransaction(
        transactionInfo: TransactionInfo,
        variationId: String?,
        onResult: (Result<AdaptyProfile>) -> Unit
    ) {
        AdaptyAndroid.reportTransaction(
            transactionInfo = transactionInfo.asAdaptyTransactionInfoAndroid(),
            variationId = variationId,
            callback = { adaptyAndroidResult ->
                val result = adaptyAndroidResult.asResult { it.asAdaptyProfile() }
                onResult(result)
            }
        )
    }

    override fun logout(onError: (AdaptyError?) -> Unit) {
        AdaptyAndroid.logout(
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }

    override fun setOnProfileUpdatedListener(onProfileUpdatedListener: OnProfileUpdatedListener?) {
        AdaptyAndroid.setOnProfileUpdatedListener(
            onProfileUpdatedListener = {
                onProfileUpdatedListener?.onProfileReceived(it.asAdaptyProfile())
            }
        )
    }

    override var logLevel: AdaptyLogLevel
        get() = AdaptyAndroid.logLevel.asAdaptyLogLevel()
        set(value) {
            AdaptyAndroid.logLevel = value.asAdaptyLogLevelAndroid()
        }

    override fun setLogHandler(logHandler: AdaptyLogHandler) {
        AdaptyAndroid.setLogHandler(logHandler.asAdaptyLogHandlerAndroid())
    }

    override fun setFallbackPaywalls(location: FileLocation, onError: (AdaptyError?) -> Unit) {
        AdaptyAndroid.setFallbackPaywalls(
            location = location.asAdaptyFileLocationAndroid(),
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }


    override fun logShowPaywall(paywall: AdaptyPaywall, onError: (AdaptyError?) -> Unit) {
        AdaptyAndroid.logShowPaywall(
            paywall = paywall.asAdaptyPaywall(),
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }

    override fun logShowOnboarding(
        name: String?,
        screenName: String?,
        screenOrder: Int,
        onError: (AdaptyError?) -> Unit
    ) {
        AdaptyAndroid.logShowOnboarding(
            name = name,
            screenName = screenName,
            screenOrder = screenOrder,
            callback = { error ->
                onError.invoke(error.asAdaptyError())
            }
        )
    }

    override fun getPaywallForDefaultAudience(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywall.FetchPolicy,
        onResult: (Result<AdaptyPaywall>) -> Unit
    ) {
        AdaptyAndroid.getPaywallForDefaultAudience(
            placementId = placementId,
            locale = locale,
            fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicy(),
            callback = { adaptyAndroidResult ->
                val result = adaptyAndroidResult.asResult { it.asAdaptyPaywall() }
                onResult(result)
            }
        )
    }


    override val isActivated: Boolean get() = AdaptyAndroid.isActivated
}