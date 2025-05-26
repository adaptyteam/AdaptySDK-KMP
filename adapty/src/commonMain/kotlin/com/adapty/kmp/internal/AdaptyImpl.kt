package com.adapty.kmp.internal

import com.adapty.kmp.AdaptyContract
import com.adapty.kmp.OnProfileUpdatedListener
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.asAdaptyResult
import com.adapty.kmp.internal.plugin.awaitExecute
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.execute
import com.adapty.kmp.internal.plugin.request.AdaptyConfigurationRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallProductsRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIdentifyRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateCollectingRefundDataRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateRefundPreferenceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowOnboardingRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyMakePurchaseRequest
import com.adapty.kmp.internal.plugin.request.AdaptyOnboardingScreenParametersRequest
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyReportTransactionRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetFallbackPaywallsRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetIntegrationIdentifierRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetLogLevelRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateAttributionRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateProfileRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPaywallRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyConfigurationRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyIosRefundPreferenceRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyLogLevelRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywall
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallFetchPolicyRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallProductRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallRequest
import com.adapty.kmp.internal.plugin.request.asAdaptySubscriptionUpdateParametersRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyUpdateProfileRequest
import com.adapty.kmp.internal.plugin.request.toAdaptyCustomAttributesRequest
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallProductResponse
import com.adapty.kmp.internal.plugin.response.AdaptyProfileResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPurchaseResultResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallProduct
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.plugin.response.asAdaptyPurchaseResult
import com.adapty.kmp.internal.utils.createJsonInstance
import com.adapty.kmp.isAndroidPlatform
import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateParameters
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyIosRefundPreference
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.onError
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.jvm.JvmName
import kotlin.time.Duration

internal class AdaptyImpl(private val adaptyPlugin: AdaptyPlugin) : AdaptyContract {

    private val appMainScope = MainScope()

    private var onProfileUpdatedListener: OnProfileUpdatedListener? = null
        @JvmName("setProfileUpdatedListenerFromJava")
        set(value) {
            appMainScope.launch {
                AdaptyPluginEventHandler.profileFlow
                    .catch {
                        logger.log("AdaptyImpl, onProfileUpdatedListener, error: $it")
                    }
                    .onEach { value?.onProfileReceived(it) }
                    .collect()
            }
            field = value
        }


    override fun activate(configuration: AdaptyConfig, onError: (AdaptyError?) -> Unit) {
        adaptyPlugin.initialize()
        adaptyPlugin.execute<AdaptyConfigurationRequest, Boolean>(
            method = AdaptyPluginMethod.ACTIVATE,
            request = configuration.asAdaptyConfigurationRequest(),
            onResult = { result ->
                //TODO verify that if error is null should call onError?
                onError(result.getErrorOrNull())
            }
        )
    }

    override fun identify(customerUserId: String, onError: (AdaptyError?) -> Unit) =
        adaptyPlugin.execute<AdaptyIdentifyRequest, Boolean>(
            method = AdaptyPluginMethod.IDENTIFY,
            request = AdaptyIdentifyRequest(customerUserId = customerUserId),
            onResult = { result ->
                onError(result.getErrorOrNull())
            }
        )


    override fun updateProfile(params: AdaptyProfileParameters, onError: (AdaptyError?) -> Unit) =
        adaptyPlugin.execute<AdaptyUpdateProfileRequest, Boolean>(
            method = AdaptyPluginMethod.UPDATE_PROFILE,
            request = params.asAdaptyUpdateProfileRequest(),
            onResult = { result ->
                onError(result.getErrorOrNull())
            }
        )

    override suspend fun getProfile(): AdaptyResult<AdaptyProfile> =
        adaptyPlugin.awaitExecute<Unit, AdaptyProfileResponse>(
            method = AdaptyPluginMethod.GET_PROFILE,
            request = Unit,
        ).asAdaptyResult { it.asAdaptyProfile() }


    override suspend fun getPaywall(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywallFetchPolicy,
        loadTimeout: Duration
    ): AdaptyResult<AdaptyPaywall> =
        adaptyPlugin.awaitExecute<AdaptyGetPaywallRequest, AdaptyPaywallRequestResponse>(
            method = AdaptyPluginMethod.GET_PAYWALL,
            request = AdaptyGetPaywallRequest(
                placementId = placementId,
                locale = locale,
                fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicyRequest(),
                loadTimeoutInSeconds = loadTimeout.inWholeSeconds
            )
        ).asAdaptyResult { it.asAdaptyPaywall() }

    override suspend fun getPaywallProducts(paywall: AdaptyPaywall): AdaptyResult<List<AdaptyPaywallProduct>> =
        adaptyPlugin.awaitExecute<AdaptyGetPaywallProductsRequest, List<AdaptyPaywallProductResponse>>(
            method = AdaptyPluginMethod.GET_PAYWALL_PRODUCTS,
            request = AdaptyGetPaywallProductsRequest(paywall = paywall.asAdaptyPaywallRequest())
        ).asAdaptyResult { list -> list.map { it.asAdaptyPaywallProduct() } }


    override suspend fun makePurchase(
        product: AdaptyPaywallProduct,
        subscriptionUpdateParams: AdaptyAndroidSubscriptionUpdateParameters?,
        isOfferPersonalized: Boolean
    ): AdaptyResult<AdaptyPurchaseResult> =
        adaptyPlugin.awaitExecute<AdaptyMakePurchaseRequest, AdaptyPurchaseResultResponse>(
            method = AdaptyPluginMethod.MAKE_PURCHASE,
            request = AdaptyMakePurchaseRequest(
                paywallProduct = product.asAdaptyPaywallProductRequest(),
                subscriptionUpdateParams = subscriptionUpdateParams?.asAdaptySubscriptionUpdateParametersRequest(),
                isOfferPersonalized = isOfferPersonalized
            )
        ).asAdaptyResult { it.asAdaptyPurchaseResult() }

    override suspend fun restorePurchases(): AdaptyResult<AdaptyProfile> =
        adaptyPlugin.awaitExecute<Unit, AdaptyProfileResponse>(
            method = AdaptyPluginMethod.RESTORE_PURCHASES,
            request = Unit
        ).asAdaptyResult { it.asAdaptyProfile() }

    override fun updateAttribution(
        attribution: Map<String, Any>,
        source: String,
        onError: (AdaptyError?) -> Unit
    ) = adaptyPlugin.execute<AdaptyUpdateAttributionRequest, Boolean>(
        method = AdaptyPluginMethod.UPDATE_ATTRIBUTION,
        request = AdaptyUpdateAttributionRequest(
            attribution = createJsonInstance().encodeToString(attribution.toAdaptyCustomAttributesRequest()),
            source = source
        ),
        onResult = { result ->
            onError(result.getErrorOrNull())
        }
    )

    override fun setIntegrationIdentifier(
        key: String,
        value: String,
        onError: (AdaptyError?) -> Unit
    ) = adaptyPlugin.execute<AdaptySetIntegrationIdentifierRequest, Boolean>(
        method = AdaptyPluginMethod.SET_INTEGRATION_IDENTIFIER,
        request = AdaptySetIntegrationIdentifierRequest(
            keyValues = mapOf(key to value)
        ),
        onResult = { result ->
            onError(result.getErrorOrNull())
        }
    )

    override suspend fun reportTransaction(
        transactionId: String,
        variationId: String?
    ): AdaptyResult<AdaptyProfile> =
        adaptyPlugin.awaitExecute<AdaptyReportTransactionRequest, AdaptyProfileResponse>(
            method = AdaptyPluginMethod.REPORT_TRANSACTION,
            request = AdaptyReportTransactionRequest(
                transactionId = transactionId,
                variationId = variationId
            )
        ).asAdaptyResult { it.asAdaptyProfile() }

    override fun logout(onError: (AdaptyError?) -> Unit) = adaptyPlugin.execute<Unit, Boolean>(
        method = AdaptyPluginMethod.LOGOUT,
        request = Unit,
        onResult = { result ->
            onError(result.getErrorOrNull())
        }
    )

    override fun setOnProfileUpdatedListener(onProfileUpdatedListener: OnProfileUpdatedListener?) {
        this.onProfileUpdatedListener = onProfileUpdatedListener
    }


    override fun setLogLevel(logLevel: AdaptyLogLevel) {
        logger = when (logLevel) {
            AdaptyLogLevel.DEBUG, AdaptyLogLevel.VERBOSE, AdaptyLogLevel.INFO -> ConsoleLogger
            else -> EmptyLogger
        }

        adaptyPlugin.execute<AdaptySetLogLevelRequest, Unit>(
            method = AdaptyPluginMethod.SET_LOG_LEVEL,
            request = AdaptySetLogLevelRequest(value = logLevel.asAdaptyLogLevelRequest()),
            onResult = {}
        )
    }

    override fun setFallbackPaywalls(assetId: String, onError: (AdaptyError?) -> Unit) =
        adaptyPlugin.execute<AdaptySetFallbackPaywallsRequest, Boolean>(
            method = AdaptyPluginMethod.SET_FALLBACK_PAYWALLS,
            request = AdaptySetFallbackPaywallsRequest(assetId = assetId),
            onResult = { result ->
                onError(result.getErrorOrNull())
            }
        )


    override fun logShowPaywall(paywall: AdaptyPaywall, onError: (AdaptyError?) -> Unit) =
        adaptyPlugin.execute<AdaptyLogShowPaywallRequest, Boolean>(
            method = AdaptyPluginMethod.LOG_SHOW_PAYWALL,
            request = AdaptyLogShowPaywallRequest(paywall = paywall.asAdaptyPaywallRequest()),
            onResult = { result ->
                onError(result.getErrorOrNull())
            }
        )

    override fun logShowOnboarding(
        name: String?,
        screenName: String?,
        screenOrder: Int,
        onError: (AdaptyError?) -> Unit
    ) = adaptyPlugin.execute<AdaptyLogShowOnboardingRequest, Boolean>(
        method = AdaptyPluginMethod.LOG_SHOW_ONBOARDING,
        request = AdaptyLogShowOnboardingRequest(
            params = AdaptyOnboardingScreenParametersRequest(
                onboardingScreenOrder = screenOrder,
                onboardingName = name,
                onboardingScreenName = screenName
            )
        ),
        onResult = { result ->
            onError(result.getErrorOrNull())
        }
    )

    override suspend fun getPaywallForDefaultAudience(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywallFetchPolicy
    ): AdaptyResult<AdaptyPaywall> =
        adaptyPlugin.awaitExecute<AdaptyGetPaywallForDefaultAudienceRequest, AdaptyPaywallRequestResponse>(
            method = AdaptyPluginMethod.GET_PAYWALL_FOR_DEFAULT_AUDIENCE,
            request = AdaptyGetPaywallForDefaultAudienceRequest(
                placementId = placementId,
                locale = locale,
                fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicyRequest()
            )
        ).asAdaptyResult { it.asAdaptyPaywall() }


    override suspend fun isActivated(): Boolean {
        val response = adaptyPlugin.awaitExecute<Unit, Boolean>(
            method = AdaptyPluginMethod.IS_ACTIVATED,
            request = Unit
        )

        val isActivated =
            (response.asAdaptyResult { it } as? AdaptyResult.Success<Boolean>)?.value ?: false

        return isActivated
    }

    override suspend fun createWebPaywallUrl(
        paywall: AdaptyPaywall?,
        product: AdaptyPaywallProduct?
    ): AdaptyResult<String> {

        val request = when {
            paywall != null -> AdaptyWebPaywallRequest.fromPaywall(paywall.asAdaptyPaywallRequest())
            product != null -> AdaptyWebPaywallRequest.fromPaywallProduct(product.asAdaptyPaywallProductRequest())
            else -> return AdaptyResult.Error(
                AdaptyError(
                    code = AdaptyErrorCode.WRONG_PARAMETER,
                    message = "Either Paywall or product must be provided"
                )
            )
        }

        val result = adaptyPlugin.awaitExecute<AdaptyWebPaywallRequest, String>(
            method = AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL,
            request = request
        ).asAdaptyResult { it }

        return result
    }

    override suspend fun openWebPaywall(
        paywall: AdaptyPaywall?,
        product: AdaptyPaywallProduct?,
        onError: (AdaptyError?) -> Unit
    ) {
        val request = when {
            paywall != null -> AdaptyWebPaywallRequest.fromPaywall(paywall.asAdaptyPaywallRequest())
            product != null -> AdaptyWebPaywallRequest.fromPaywallProduct(product.asAdaptyPaywallProductRequest())
            else -> {
                val error = AdaptyError(
                    code = AdaptyErrorCode.WRONG_PARAMETER,
                    message = "Either Paywall or product must be provided"
                )
                onError(error)
                return
            }
        }

        val result = adaptyPlugin.awaitExecute<AdaptyWebPaywallRequest, Boolean>(
            method = AdaptyPluginMethod.OPEN_WEB_PAYWALL,
            request = request
        ).asAdaptyResult { it }

        result.onError(onError)
    }

    override suspend fun presentCodeRedemptionSheet(onError: (AdaptyError?) -> Unit) {
        if (isAndroidPlatform) return
        val result = adaptyPlugin.awaitExecute<Unit, Boolean>(
            method = AdaptyPluginMethod.PRESENT_CODE_REDEMPTION_SHEET,
            request = Unit
        ).asAdaptyResult { it }

        result.onError(onError)
    }

    override suspend fun updateRefundPreference(preference: AdaptyIosRefundPreference): AdaptyResult<Boolean> {
        if (isAndroidPlatform) return AdaptyResult.Error(
            AdaptyError(
                code = AdaptyErrorCode.DEVELOPER_ERROR,
                message = "This method is only available for iOS"
            )
        )
        return adaptyPlugin
            .awaitExecute<AdaptyIosUpdateRefundPreferenceRequest, Boolean>(
                method = AdaptyPluginMethod.UPDATE_REFUND_PREFERENCE,
                request = AdaptyIosUpdateRefundPreferenceRequest(
                    refundPreference = preference.asAdaptyIosRefundPreferenceRequest()
                )
            )
            .asAdaptyResult { it }
    }

    override suspend fun updateCollectingRefundDataConsent(consent: Boolean): AdaptyResult<Boolean> {
        if (isAndroidPlatform) return AdaptyResult.Error(
            AdaptyError(
                code = AdaptyErrorCode.DEVELOPER_ERROR,
                message = "This method is only available for iOS"
            )
        )
        return adaptyPlugin
            .awaitExecute<AdaptyIosUpdateCollectingRefundDataRequest, Boolean>(
                method = AdaptyPluginMethod.UPDATE_COLLECTING_REFUND_DATA_CONSENT,
                request = AdaptyIosUpdateCollectingRefundDataRequest(consent = consent)
            )
            .asAdaptyResult { it }
    }
}