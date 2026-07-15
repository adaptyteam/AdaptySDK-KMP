package com.adapty.kmp.internal

import com.adapty.kmp.AdaptyContract
import com.adapty.kmp.OnInstallationDetailsListener
import com.adapty.kmp.OnProfileUpdatedListener
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.asAdaptyResult
import com.adapty.kmp.internal.plugin.awaitExecute
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.constants.Constants.DEFAULT_LOCALE
import com.adapty.kmp.internal.plugin.execute
import com.adapty.kmp.internal.plugin.request.AdaptyConfigurationRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetOnboardingForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetOnboardingRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetFlowForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallProductsRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetFlowRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIdentifyRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateCollectingRefundDataRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateRefundPreferenceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowFlowRequest
import com.adapty.kmp.internal.plugin.request.AdaptyFlowRequestResponse
import com.adapty.kmp.internal.plugin.request.asAdaptyFlow
import com.adapty.kmp.internal.plugin.request.asAdaptyFlowPaywallRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyFlowRequest
import com.adapty.kmp.internal.plugin.request.AdaptyMakePurchaseRequest
import com.adapty.kmp.internal.plugin.request.AdaptyOnboardingRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyReportTransactionRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetFallbackPaywallsRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetIntegrationIdentifierRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetLogLevelRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateAttributionRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateProfileRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPaywallRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyConfigurationRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyCustomerIdentityRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyIosRefundPreferenceRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyLogLevelRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyOnboarding
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallFetchPolicyRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallProductRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPurchaseParametersRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyUpdateProfileRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyWebPresentationRequest
import com.adapty.kmp.internal.plugin.request.toAdaptyCustomAttributesRequest
import com.adapty.kmp.internal.plugin.response.AdaptyGetCurrentInstallationStatusResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallProductResponse
import com.adapty.kmp.internal.plugin.response.AdaptyProfileResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPurchaseResultResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyInstallationStatus
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallProduct
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.plugin.response.asAdaptyPurchaseResult
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.isAndroidPlatform
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyCustomerIdentity
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
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
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyWebPresentation
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration

@Suppress("OVERRIDE_DEPRECATION")
internal class AdaptyImpl(
    private val adaptyPlugin: AdaptyPlugin,
    private val appMainScope: CoroutineScope = MainScope()
) : AdaptyContract {

    private var eventsListenerJob: Job? = null
    private var profileUpdateEventObserver: OnProfileUpdatedListener? = null
    private var installationDetailsEventObserver: OnInstallationDetailsListener? = null


    init {
        listerForEvents()
    }


    override suspend fun activate(configuration: AdaptyConfig): AdaptyResult<Unit> {
        adaptyPlugin.initialize()
        return adaptyPlugin.awaitExecute<AdaptyConfigurationRequest, Boolean>(
            method = AdaptyPluginMethod.ACTIVATE,
            request = configuration.asAdaptyConfigurationRequest()
        ).asAdaptyResult {}
    }

    override suspend fun identify(
        customerUserId: String,
        iosAppAccountToken: String?,
        androidObfuscatedAccountId: String?
    ): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptyIdentifyRequest, Boolean>(
            method = AdaptyPluginMethod.IDENTIFY,
            request = AdaptyIdentifyRequest(
                customerUserId = customerUserId,
                parameters = AdaptyCustomerIdentity.createIfNotEmpty(
                    iosAppAccountToken = iosAppAccountToken,
                    androidObfuscatedAccountId = androidObfuscatedAccountId
                )?.asAdaptyCustomerIdentityRequest()
            )
        ).asAdaptyResult { }


    override suspend fun updateProfile(params: AdaptyProfileParameters): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptyUpdateProfileRequest, Boolean>(
            method = AdaptyPluginMethod.UPDATE_PROFILE,
            request = params.asAdaptyUpdateProfileRequest()
        ).asAdaptyResult { }

    override suspend fun getProfile(): AdaptyResult<AdaptyProfile> =
        adaptyPlugin.awaitExecute<Unit, AdaptyProfileResponse>(
            method = AdaptyPluginMethod.GET_PROFILE,
            request = Unit,
        ).asAdaptyResult { it.asAdaptyProfile() }


    override suspend fun getCurrentInstallationStatus(): AdaptyResult<AdaptyInstallationStatus> =
        adaptyPlugin.awaitExecute<Unit, AdaptyGetCurrentInstallationStatusResponse>(
            method = AdaptyPluginMethod.GET_CURRENT_INSTALLATION_STATUS,
            request = Unit,
        ).asAdaptyResult { it.asAdaptyInstallationStatus() }

    override suspend fun getFlow(
        placementId: String,
        fetchPolicy: AdaptyPaywallFetchPolicy,
        loadTimeout: Duration
    ): AdaptyResult<AdaptyFlow> =
        adaptyPlugin.awaitExecute<AdaptyGetFlowRequest, AdaptyFlowRequestResponse>(
            method = AdaptyPluginMethod.GET_FLOW,
            request = AdaptyGetFlowRequest(
                placementId = placementId,
                fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicyRequest(),
                loadTimeoutInSeconds = loadTimeout.inWholeMilliseconds.toDouble() / 1000.0
            )
        ).asAdaptyResult { it.asAdaptyFlow() }

    override suspend fun getFlowForDefaultAudience(
        placementId: String,
        fetchPolicy: AdaptyPaywallFetchPolicy
    ): AdaptyResult<AdaptyFlow> =
        adaptyPlugin.awaitExecute<AdaptyGetFlowForDefaultAudienceRequest, AdaptyFlowRequestResponse>(
            method = AdaptyPluginMethod.GET_FLOW_FOR_DEFAULT_AUDIENCE,
            request = AdaptyGetFlowForDefaultAudienceRequest(
                placementId = placementId,
                fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicyRequest()
            )
        ).asAdaptyResult { it.asAdaptyFlow() }

    override suspend fun getPaywallProducts(flow: AdaptyFlow): AdaptyResult<List<AdaptyPaywallProduct>> =
        adaptyPlugin.awaitExecute<AdaptyGetPaywallProductsRequest, List<AdaptyPaywallProductResponse>>(
            method = AdaptyPluginMethod.GET_PAYWALL_PRODUCTS,
            request = AdaptyGetPaywallProductsRequest(flow = flow.asAdaptyFlowRequest())
        ).asAdaptyResult { list -> list.map { it.asAdaptyPaywallProduct() } }

    override suspend fun logShowFlow(flow: AdaptyFlow): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptyLogShowFlowRequest, Boolean>(
            method = AdaptyPluginMethod.LOG_SHOW_FLOW,
            request = AdaptyLogShowFlowRequest(flow = flow.asAdaptyFlowRequest())
        ).asAdaptyResult { }


    override suspend fun makePurchase(
        product: AdaptyPaywallProduct,
        parameters: AdaptyPurchaseParameters?,
    ): AdaptyResult<AdaptyPurchaseResult> =
        adaptyPlugin.awaitExecute<AdaptyMakePurchaseRequest, AdaptyPurchaseResultResponse>(
            method = AdaptyPluginMethod.MAKE_PURCHASE,
            request = AdaptyMakePurchaseRequest(
                paywallProduct = product.asAdaptyPaywallProductRequest(),
                parameters = parameters?.asAdaptyPurchaseParametersRequest(),
            )
        ).asAdaptyResult { it.asAdaptyPurchaseResult() }

    override suspend fun restorePurchases(): AdaptyResult<AdaptyProfile> =
        adaptyPlugin.awaitExecute<Unit, AdaptyProfileResponse>(
            method = AdaptyPluginMethod.RESTORE_PURCHASES,
            request = Unit
        ).asAdaptyResult { it.asAdaptyProfile() }

    override suspend fun updateAttribution(
        attribution: Map<String, Any>,
        source: String
    ): AdaptyResult<Unit> = adaptyPlugin.awaitExecute<AdaptyUpdateAttributionRequest, Boolean>(
        method = AdaptyPluginMethod.UPDATE_ATTRIBUTION,
        request = AdaptyUpdateAttributionRequest(
            attribution = jsonInstance.encodeToString(attribution.toAdaptyCustomAttributesRequest()),
            source = source
        )
    ).asAdaptyResult { }

    override suspend fun setIntegrationIdentifier(key: String, value: String): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptySetIntegrationIdentifierRequest, Boolean>(
            method = AdaptyPluginMethod.SET_INTEGRATION_IDENTIFIER,
            request = AdaptySetIntegrationIdentifierRequest(
                keyValues = mapOf(key to value)
            )
        ).asAdaptyResult { }

    override suspend fun reportTransaction(
        transactionId: String,
        variationId: String?
    ): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptyReportTransactionRequest, Boolean>(
            method = AdaptyPluginMethod.REPORT_TRANSACTION,
            request = AdaptyReportTransactionRequest(
                transactionId = transactionId,
                variationId = variationId
            )
        ).asAdaptyResult { }

    override suspend fun logout(): AdaptyResult<Unit> = adaptyPlugin.awaitExecute<Unit, Boolean>(
        method = AdaptyPluginMethod.LOGOUT,
        request = Unit
    ).asAdaptyResult { }

    override fun setOnProfileUpdatedListener(onProfileUpdatedListener: OnProfileUpdatedListener?) {
        this.profileUpdateEventObserver = onProfileUpdatedListener
    }

    override fun setOnInstallationDetailsListener(onInstallationDetailsListener: OnInstallationDetailsListener?) {
        this.installationDetailsEventObserver = onInstallationDetailsListener
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

    override suspend fun setFallback(assetId: String): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptySetFallbackPaywallsRequest, Boolean>(
            method = AdaptyPluginMethod.SET_FALLBACK,
            request = AdaptySetFallbackPaywallsRequest(assetId = assetId)
        ).asAdaptyResult { }


    override suspend fun isActivated(): Boolean {
        val response = adaptyPlugin.awaitExecute<Unit, Boolean>(
            method = AdaptyPluginMethod.IS_ACTIVATED,
            request = Unit
        )

        val isActivated =
            (response.asAdaptyResult { it } as? AdaptyResult.Success<Boolean>)?.value ?: false

        return isActivated
    }

    override suspend fun createWebPaywallUrl(flowPaywall: AdaptyFlowPaywall): AdaptyResult<String> =
        adaptyPlugin.awaitExecute<AdaptyWebPaywallRequest, String>(
            method = AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL,
            request = AdaptyWebPaywallRequest.fromPaywall(flowPaywall.asAdaptyFlowPaywallRequest())
        ).asAdaptyResult { it }

    override suspend fun createWebPaywallUrl(product: AdaptyPaywallProduct): AdaptyResult<String> =
        adaptyPlugin.awaitExecute<AdaptyWebPaywallRequest, String>(
            method = AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL,
            request = AdaptyWebPaywallRequest.fromPaywallProduct(product.asAdaptyPaywallProductRequest())
        ).asAdaptyResult { it }

    override suspend fun openWebPaywall(
        flowPaywall: AdaptyFlowPaywall,
        openIn: AdaptyWebPresentation
    ): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptyWebPaywallRequest, Boolean>(
            method = AdaptyPluginMethod.OPEN_WEB_PAYWALL,
            request = AdaptyWebPaywallRequest.fromPaywall(
                paywall = flowPaywall.asAdaptyFlowPaywallRequest(),
                webPresentationRequest = openIn.asAdaptyWebPresentationRequest()
            )
        ).asAdaptyResult { }

    override suspend fun openWebPaywall(
        product: AdaptyPaywallProduct,
        openIn: AdaptyWebPresentation
    ): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptyWebPaywallRequest, Boolean>(
            method = AdaptyPluginMethod.OPEN_WEB_PAYWALL,
            request = AdaptyWebPaywallRequest.fromPaywallProduct(
                product = product.asAdaptyPaywallProductRequest(),
                webPresentationRequest = openIn.asAdaptyWebPresentationRequest()
            )
        ).asAdaptyResult { }

    override suspend fun presentCodeRedemptionSheet(): AdaptyResult<Unit> {
        if (isAndroidPlatform) return AdaptyResult.Error(
            AdaptyError(
                code = AdaptyErrorCode.DEVELOPER_ERROR,
                message = "This method is only available for iOS"
            )
        )
        return adaptyPlugin.awaitExecute<Unit, Boolean>(
            method = AdaptyPluginMethod.PRESENT_CODE_REDEMPTION_SHEET,
            request = Unit
        ).asAdaptyResult { }
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

    override suspend fun getOnboarding(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywallFetchPolicy,
        loadTimeout: Duration
    ): AdaptyResult<AdaptyOnboarding> =
        adaptyPlugin.awaitExecute<AdaptyGetOnboardingRequest, AdaptyOnboardingRequestResponse>(
            method = AdaptyPluginMethod.GET_ONBOARDING,
            request = AdaptyGetOnboardingRequest(
                placementId = placementId,
                locale = locale.takeIf { !it.isNullOrBlank() } ?: DEFAULT_LOCALE,
                fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicyRequest(),
                loadTimeoutInSeconds = loadTimeout.inWholeMilliseconds.toDouble() / 1000.0
            )
        ).asAdaptyResult { it.asAdaptyOnboarding() }

    override suspend fun getOnboardingForDefaultAudience(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywallFetchPolicy
    ): AdaptyResult<AdaptyOnboarding> =
        adaptyPlugin.awaitExecute<AdaptyGetOnboardingForDefaultAudienceRequest, AdaptyOnboardingRequestResponse>(
            method = AdaptyPluginMethod.GET_ONBOARDING_FOR_DEFAULT_AUDIENCE,
            request = AdaptyGetOnboardingForDefaultAudienceRequest(
                placementId = placementId,
                locale = locale.takeIf { !it.isNullOrBlank() } ?: DEFAULT_LOCALE,
                fetchPolicy = fetchPolicy.asAdaptyPaywallFetchPolicyRequest()
            )
        ).asAdaptyResult { it.asAdaptyOnboarding() }

    private fun listerForEvents() {
        eventsListenerJob?.cancel()
        eventsListenerJob = appMainScope.launch {
            supervisorScope {
                launch { listenForProfileUpdateEvent() }
                launch { listenForInstallationEvent() }
            }
        }
    }

    @OptIn(AdaptyKMPInternal::class)
    private suspend fun listenForProfileUpdateEvent() {
        AdaptyPluginEventHandler.profileFlow
            .catch {
                logger.log("AdaptyImpl, onProfileUpdatedListener, error: $it")
            }
            .collect { profile ->
                profileUpdateEventObserver?.onProfileReceived(profile)
            }
    }

    @OptIn(AdaptyKMPInternal::class)
    private suspend fun listenForInstallationEvent() {
        AdaptyPluginEventHandler.installationEventResultFlow
            .catch {
                logger.log("AdaptyImpl, onInstallationDetailsListener, error: $it")
            }
            .collect { installationEventResult ->
                installationEventResult
                    .onSuccess { details ->
                        installationDetailsEventObserver?.onInstallationDetailsSuccess(
                            details
                        )
                    }
                    .onError { error ->
                        installationDetailsEventObserver?.onInstallationDetailsFailure(
                            error
                        )
                    }
            }
    }
}