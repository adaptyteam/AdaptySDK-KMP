package com.adapty.exampleapp

import com.adapty.kmp.models.AdaptyIosRefundPreference
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import kotlin.time.Duration.Companion.seconds

data class AppUiState(
    val adaptyProfile: AdaptyProfile? = null,
    val examplePaywall: AdaptyPaywall? = null,
    val examplePaywallProducts: List<AdaptyPaywallProduct> = emptyList(),
    val customPaywall: AdaptyPaywall? = null,
    val customPaywallProducts: List<AdaptyPaywallProduct> = emptyList(),
    val customPaywallId: String = "",
    val customPaywallLocale: String = "",
    val selectedPolicy: DemoPaywallFetchPolicy = DemoPaywallFetchPolicy.ReloadRevalidatingCacheData,
    val userEnteredCustomerUserId: String = "",
    val savedPaywallIds: Set<String> = setOf(),
    val savedPaywalls: Map<String, AdaptyPaywall> = mapOf(),
    val isLoading: Boolean = false,
    val error: Throwable? = null
) {
    enum class DemoPaywallFetchPolicy {
        ReloadRevalidatingCacheData,
        ReturnCacheDataElseLoad,
        ReturnCacheDataIfNotExpiredElseLoadMaxAge10Sec,
        ReturnCacheDataIfNotExpiredElseLoadMaxAge30Sec,
        ReturnCacheDataIfNotExpiredElseLoadMaxAge120Sec;

        fun title(): String = when (this) {
            ReloadRevalidatingCacheData -> "Reload Revalidating Cache Data"
            ReturnCacheDataElseLoad -> "Return Cache Data Else Load"
            ReturnCacheDataIfNotExpiredElseLoadMaxAge10Sec -> "Cache Else Load (Max Age 10sec)"
            ReturnCacheDataIfNotExpiredElseLoadMaxAge30Sec -> "Cache Else Load (Max Age 30sec)"
            ReturnCacheDataIfNotExpiredElseLoadMaxAge120Sec -> "Cache Else Load (Max Age 120sec)"
        }

        fun toAdaptyPaywallFetchPolicy(): AdaptyPaywallFetchPolicy = when (this) {
            ReloadRevalidatingCacheData -> AdaptyPaywallFetchPolicy.ReloadRevalidatingCacheData
            ReturnCacheDataElseLoad -> AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad
            ReturnCacheDataIfNotExpiredElseLoadMaxAge10Sec ->
                AdaptyPaywallFetchPolicy.ReturnCacheDataIfNotExpiredElseLoad(10.seconds.inWholeMilliseconds)

            ReturnCacheDataIfNotExpiredElseLoadMaxAge30Sec ->
                AdaptyPaywallFetchPolicy.ReturnCacheDataIfNotExpiredElseLoad(30.seconds.inWholeMilliseconds)

            ReturnCacheDataIfNotExpiredElseLoadMaxAge120Sec ->
                AdaptyPaywallFetchPolicy.ReturnCacheDataIfNotExpiredElseLoad(120.seconds.inWholeMilliseconds)
        }
    }

}

sealed interface AppUiEvent {
    data object OnClickLogout : AppUiEvent
    data object OnClickIdentifyCustomerUserId : AppUiEvent
    data object OnClickReloadProfile : AppUiEvent
    data object OnClickUpdateProfile : AppUiEvent
    data object OnClickSetIntegrationIdentifier : AppUiEvent
    data object OnClickUpdateAttribution : AppUiEvent
    data class OnCustomerUserIdInputChanged(val value: String) : AppUiEvent
    data class OnNewFetchPolicySelected(val policy: AppUiState.DemoPaywallFetchPolicy) : AppUiEvent
    data object OnClickRefreshExamplePaywall : AppUiEvent
    data object OnClickLoadCustomPaywall : AppUiEvent
    data object OnClickRestorePurchases : AppUiEvent
    data object OnClickResetCustomPaywall : AppUiEvent
    data class OnClickReportTransaction(val transactionId: String, val variationId: String) :
        AppUiEvent

    data class OnClickLogShowPaywall(val paywall: AdaptyPaywall) : AppUiEvent
    data class OnClickProduct(val product: AdaptyPaywallProduct) : AppUiEvent
    data class OnClickCustomPaywallIdInputChanged(val value: String) : AppUiEvent
    data class OnClickCustomPaywallLocaleInputChanged(val value: String) : AppUiEvent
    data class OnClickUpdateRefundPreference(val refundPreference: AdaptyIosRefundPreference) :
        AppUiEvent

    data class OnClickUpdateConsent(val consent: Boolean) : AppUiEvent
    data class OnNewPaywallIdAdded(val paywallId: String) : AppUiEvent
    data class CreateAndPresentPaywallView(val paywall: AdaptyPaywall, val loadProducts: Boolean) :
        AppUiEvent

}