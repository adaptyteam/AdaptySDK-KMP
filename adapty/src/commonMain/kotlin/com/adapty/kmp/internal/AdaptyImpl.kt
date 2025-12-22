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
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallProductsRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIdentifyRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateCollectingRefundDataRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateRefundPreferenceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyMakePurchaseRequest
import com.adapty.kmp.internal.plugin.request.AdaptyOnboardingRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallRequestResponse
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
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywall
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallFetchPolicyRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallProductRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallRequest
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
                locale = locale.takeIf { !it.isNullOrBlank() } ?: DEFAULT_LOCALE,
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
    ): AdaptyResult<AdaptyProfile> =
        adaptyPlugin.awaitExecute<AdaptyReportTransactionRequest, AdaptyProfileResponse>(
            method = AdaptyPluginMethod.REPORT_TRANSACTION,
            request = AdaptyReportTransactionRequest(
                transactionId = transactionId,
                variationId = variationId
            )
        ).asAdaptyResult { it.asAdaptyProfile() }

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


    override suspend fun logShowPaywall(paywall: AdaptyPaywall): AdaptyResult<Unit> =
        adaptyPlugin.awaitExecute<AdaptyLogShowPaywallRequest, Boolean>(
            method = AdaptyPluginMethod.LOG_SHOW_PAYWALL,
            request = AdaptyLogShowPaywallRequest(paywall = paywall.asAdaptyPaywallRequest())
        ).asAdaptyResult { }

    override suspend fun getPaywallForDefaultAudience(
        placementId: String,
        locale: String?,
        fetchPolicy: AdaptyPaywallFetchPolicy
    ): AdaptyResult<AdaptyPaywall> =
        adaptyPlugin.awaitExecute<AdaptyGetPaywallForDefaultAudienceRequest, AdaptyPaywallRequestResponse>(
            method = AdaptyPluginMethod.GET_PAYWALL_FOR_DEFAULT_AUDIENCE,
            request = AdaptyGetPaywallForDefaultAudienceRequest(
                placementId = placementId,
                locale = locale.takeIf { !it.isNullOrBlank() } ?: DEFAULT_LOCALE,
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
        openIn: AdaptyWebPresentation
    ): AdaptyResult<Unit> {
        val request = when {
            paywall != null -> AdaptyWebPaywallRequest.fromPaywall(
                paywall = paywall.asAdaptyPaywallRequest(),
                webPresentationRequest = openIn.asAdaptyWebPresentationRequest()
            )

            product != null -> AdaptyWebPaywallRequest.fromPaywallProduct(
                product = product.asAdaptyPaywallProductRequest(),
                webPresentationRequest = openIn.asAdaptyWebPresentationRequest()
            )

            else -> {
                val error = AdaptyError(
                    code = AdaptyErrorCode.WRONG_PARAMETER,
                    message = "Either Paywall or product must be provided"
                )
                return AdaptyResult.Error(error)
            }
        }

        return adaptyPlugin.awaitExecute<AdaptyWebPaywallRequest, Boolean>(
            method = AdaptyPluginMethod.OPEN_WEB_PAYWALL,
            request = request
        ).asAdaptyResult { }
    }

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
                loadTimeoutInSeconds = loadTimeout.inWholeSeconds
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