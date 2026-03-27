import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyGetOnboardingForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetOnboardingRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallProductsRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateCollectingRefundDataRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosUpdateRefundPreferenceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyMakePurchaseRequest
import com.adapty.kmp.internal.plugin.request.AdaptyOnboardingRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallProductRequest
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyPurchaseParametersRequest
import com.adapty.kmp.internal.plugin.request.AdaptyReportTransactionRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetIntegrationIdentifierRequest
import com.adapty.kmp.internal.plugin.request.AdaptyIosRefundPreferenceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateOnboardingViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUICreatePaywallViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDismissViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIIOSPresentationStyleRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIPresentViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIShowDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateAttributionRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPresentationRequest
import com.adapty.kmp.internal.utils.getEmptyJsonObjectString
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.internal.utils.toJsonObject
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyProfileParameters
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object AdaptyPluginRequestTemplate {

    internal fun getJsonString(method: AdaptyPluginMethod, param: Any): String = when (method) {
        AdaptyPluginMethod.ACTIVATE -> getActivateRequestJsonString(param as AdaptyConfig)
        AdaptyPluginMethod.IDENTIFY -> getIdentifyRequestJsonString(param as String)
        AdaptyPluginMethod.UPDATE_PROFILE -> getUpdateProfileRequestJsonString(param as AdaptyProfileParameters)
        AdaptyPluginMethod.GET_PROFILE -> getEmptyJsonObjectString()
        AdaptyPluginMethod.RESTORE_PURCHASES -> getEmptyJsonObjectString()
        AdaptyPluginMethod.LOGOUT -> getEmptyJsonObjectString()
        AdaptyPluginMethod.SET_INTEGRATION_IDENTIFIER -> getIntegrationIdentifierRequestJsonString(
            param as AdaptySetIntegrationIdentifierRequest
        )

        AdaptyPluginMethod.GET_PAYWALL -> getPaywallRequestJsonString(param as AdaptyGetPaywallRequest)
        AdaptyPluginMethod.GET_PAYWALL_PRODUCTS -> getPaywallProductsRequestJsonString(param as AdaptyGetPaywallProductsRequest)
        AdaptyPluginMethod.MAKE_PURCHASE -> getMakePurchaseRequestJsonString(param as AdaptyMakePurchaseRequest)
        AdaptyPluginMethod.UPDATE_ATTRIBUTION -> getUpdateAttributionRequestJsonString(param as AdaptyUpdateAttributionRequest)
        AdaptyPluginMethod.REPORT_TRANSACTION -> getReportTransactionRequestJsonString(param as AdaptyReportTransactionRequest)
        AdaptyPluginMethod.SET_FALLBACK -> getSetFallbackPaywallsRequestJsonString(param as String)
        AdaptyPluginMethod.LOG_SHOW_PAYWALL -> getAdaptyLogShowPaywallRequest(param as AdaptyLogShowPaywallRequest)
        AdaptyPluginMethod.GET_PAYWALL_FOR_DEFAULT_AUDIENCE -> getAdaptyGetPaywallForDefaultAudienceRequest(
            param as AdaptyGetPaywallForDefaultAudienceRequest
        )

        AdaptyPluginMethod.GET_CURRENT_INSTALLATION_STATUS -> getEmptyJsonObjectString()
        AdaptyPluginMethod.IS_ACTIVATED -> getEmptyJsonObjectString()
        AdaptyPluginMethod.PRESENT_CODE_REDEMPTION_SHEET -> getEmptyJsonObjectString()
        AdaptyPluginMethod.GET_ONBOARDING -> getOnboardingRequestJsonString(param as AdaptyGetOnboardingRequest)
        AdaptyPluginMethod.GET_ONBOARDING_FOR_DEFAULT_AUDIENCE -> getOnboardingForDefaultAudienceRequestJsonString(
            param as AdaptyGetOnboardingForDefaultAudienceRequest
        )

        AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL -> getWebPaywallRequestJsonString(param as AdaptyWebPaywallRequest)
        AdaptyPluginMethod.OPEN_WEB_PAYWALL -> getWebPaywallRequestJsonString(param as AdaptyWebPaywallRequest)
        AdaptyPluginMethod.UPDATE_REFUND_PREFERENCE -> getUpdateRefundPreferenceRequestJsonString(
            param as AdaptyIosUpdateRefundPreferenceRequest
        )

        AdaptyPluginMethod.UPDATE_COLLECTING_REFUND_DATA_CONSENT -> getUpdateCollectingRefundDataConsentRequestJsonString(
            param as AdaptyIosUpdateCollectingRefundDataRequest
        )

        AdaptyPluginMethod.SET_LOG_LEVEL -> getEmptyJsonObjectString()

        // UI methods
        AdaptyPluginMethod.CREATE_PAYWALL_VIEW -> getCreatePaywallViewRequestJsonString(
            param as AdaptyUICreatePaywallViewRequest
        )

        AdaptyPluginMethod.PRESENT_PAYWALL_VIEW -> getPresentViewRequestJsonString(param as AdaptyUIPresentViewRequest)
        AdaptyPluginMethod.DISMISS_PAYWALL_VIEW -> getDismissViewRequestJsonString(param as AdaptyUIDismissViewRequest)
        AdaptyPluginMethod.SHOW_DIALOG -> getShowDialogRequestJsonString(param as AdaptyUIShowDialogRequest)
        AdaptyPluginMethod.CREATE_ONBOARDING_VIEW -> getCreateOnboardingViewRequestJsonString(
            param as AdaptyUICreateOnboardingViewRequest
        )

        AdaptyPluginMethod.PRESENT_ONBOARDING_VIEW -> getPresentViewRequestJsonString(param as AdaptyUIPresentViewRequest)
        AdaptyPluginMethod.DISMISS_ONBOARDING_VIEW -> getDismissViewRequestJsonString(param as AdaptyUIDismissViewRequest)

        else -> throw Exception(
            "Unknown method type. Please, provide request json template in " +
                    "AdaptyPluginRequestTemplate.kt for ${method.methodName} method. " +
                    "Suggested method name is ${method.methodName}RequestJsonString()"
        )
    }


    private fun getActivateRequestJsonString(configuration: AdaptyConfig): String {
        return buildJsonObject {
            putJsonObject("configuration") {
                put("api_key", configuration.apiKey)
                put("customer_user_id", configuration.customerUserId)
                put("observer_mode", configuration.observerMode)
                put("apple_idfa_collection_disabled", configuration.appleIdfaCollectionDisabled)
                put("google_adid_collection_disabled", configuration.googleAdvertisingIdCollection)
                put("google_enable_pending_prepaid_plans", configuration.googleEnablePendingPrepaidPlans)
                put("google_local_access_level_allowed", configuration.googleLocalAccessLevelAllowed)
                put("ip_address_collection_disabled", configuration.ipAddressCollectionDisabled)
                put("clear_data_on_backup", configuration.appleClearDataOnBackup)
                put("server_cluster", configuration.serverCluster)
                put("log_level", "debug")
                put("cross_platform_sdk_name", AdaptyConfig.SDK_NAME)
                put("cross_platform_sdk_version", AdaptyConfig.SDK_VERSION)
                put("activate_ui", configuration.activateUI)
                putJsonObject("media_cache") {
                    put(
                        "memory_storage_total_cost_limit",
                        configuration.mediaCache.memoryStorageTotalCostLimit
                    )
                    put(
                        "memory_storage_count_limit",
                        configuration.mediaCache.memoryStorageCountLimit
                    )
                    put("disk_storage_size_limit", configuration.mediaCache.diskStorageSizeLimit)
                }

            }
        }.toString()
    }

    private fun getIdentifyRequestJsonString(customerUserId: String): String {
        return buildJsonObject {
            put("customer_user_id", customerUserId)
        }.toString()
    }


    private fun getUpdateProfileRequestJsonString(adaptyProfileParameters: AdaptyProfileParameters): String {
        return buildJsonObject {
            putJsonObject("params") {
                put("first_name", adaptyProfileParameters.firstName ?: "")
                put("last_name", adaptyProfileParameters.lastName ?: "")
                put("gender", adaptyProfileParameters.gender ?: "o")
                put("birthday", adaptyProfileParameters.birthday ?: "1970-01-01")
                put("email", adaptyProfileParameters.email ?: "")
                put("phone_number", adaptyProfileParameters.phoneNumber ?: "")
                put("custom_attributes", adaptyProfileParameters.customAttributes.toJsonObject())
            }
        }.toString()
    }

    private fun getIntegrationIdentifierRequestJsonString(adaptySetIntegrationIdentifierRequest: AdaptySetIntegrationIdentifierRequest): String {
        val jsonObject = buildJsonObject {
            putJsonObject("key_values") {
                adaptySetIntegrationIdentifierRequest.keyValues.forEach { (key, value) ->
                    put(key, value)
                }
            }
        }
        return jsonObject.toString()
    }

    private fun getPaywallRequestJsonString(adaptyGetPaywallRequest: AdaptyGetPaywallRequest): String {
        val jsonObject = buildJsonObject {
            put("placement_id", adaptyGetPaywallRequest.placementId)
            put("locale", adaptyGetPaywallRequest.locale)
            put("load_timeout", adaptyGetPaywallRequest.loadTimeoutInSeconds)
            adaptyGetPaywallRequest.fetchPolicy?.let { fetchPolicy ->
                put("fetch_policy", buildFetchPolicyJsonObject(fetchPolicy))
            }
        }
        return jsonObject.toString()
    }

    private fun buildFetchPolicyJsonObject(fetchPolicy: com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest) =
        buildJsonObject {
            when (fetchPolicy) {
                is com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest.ReloadRevalidatingCacheData -> {
                    put("type", "reload_revalidating_cache_data")
                }

                is com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad -> {
                    put("type", "return_cache_data_else_load")
                }

                is com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest.ReturnCacheDataIfNotExpiredElseLoad -> {
                    put("type", "return_cache_data_if_not_expired_else_load")
                    put("max_age", fetchPolicy.maxAgeInSeconds)
                }
            }

        }

    private fun getPaywallProductsRequestJsonString(request: AdaptyGetPaywallProductsRequest): String {
        val jsonObject = buildJsonObject {
            put(
                "paywall",
                jsonInstance.encodeToJsonElement(
                    AdaptyPaywallRequestResponse.serializer(),
                    request.paywall
                )
            )
        }
        return jsonObject.toString()
    }

    private fun getMakePurchaseRequestJsonString(adaptyMakePurchaseRequest: AdaptyMakePurchaseRequest): String {

        val jsonObject = buildJsonObject {
            put(
                "product",
                jsonInstance.encodeToJsonElement(
                    AdaptyPaywallProductRequest.serializer(),
                    adaptyMakePurchaseRequest.paywallProduct
                )
            )

            adaptyMakePurchaseRequest.parameters?.let { parameters ->
                put("parameters",jsonInstance.encodeToJsonElement(
                    AdaptyPurchaseParametersRequest.serializer(),
                    parameters
                ))
            }

        }
        return jsonObject.toString()
    }

    private fun getUpdateAttributionRequestJsonString(adaptyUpdateAttributionRequest: AdaptyUpdateAttributionRequest): String {
        val jsonObject = buildJsonObject {
            put("attribution", adaptyUpdateAttributionRequest.attribution)
            put("source", adaptyUpdateAttributionRequest.source)
        }
        return jsonObject.toString()
    }

    private fun getReportTransactionRequestJsonString(adaptyReportTransactionRequest: AdaptyReportTransactionRequest): String {
        val jsonObject = buildJsonObject {
            put("transaction_id", adaptyReportTransactionRequest.transactionId)
            put("variation_id", adaptyReportTransactionRequest.variationId)
        }
        return jsonObject.toString()
    }

    private fun getSetFallbackPaywallsRequestJsonString(assetId: String): String {
        return buildJsonObject {
            put("asset_id", assetId)
        }.toString()
    }

    private fun getAdaptyLogShowPaywallRequest(request: AdaptyLogShowPaywallRequest): String {
        val jsonObject = buildJsonObject {
            put(
                "paywall",
                jsonInstance.encodeToJsonElement(
                    AdaptyPaywallRequestResponse.serializer(),
                    request.paywall
                )
            )
        }
        return jsonObject.toString()
    }

    private fun getAdaptyGetPaywallForDefaultAudienceRequest(request: AdaptyGetPaywallForDefaultAudienceRequest): String {
        val jsonObject = buildJsonObject {
            put("placement_id", request.placementId)
            put("locale", request.locale)
            request.fetchPolicy?.let {
                put("fetch_policy", buildFetchPolicyJsonObject(it))
            }
        }
        return jsonObject.toString()
    }

    private fun getOnboardingRequestJsonString(request: AdaptyGetOnboardingRequest): String {
        return buildJsonObject {
            put("placement_id", request.placementId)
            put("locale", request.locale)
            put("load_timeout", request.loadTimeoutInSeconds)
            request.fetchPolicy?.let {
                put("fetch_policy", buildFetchPolicyJsonObject(it))
            }
        }.toString()
    }

    private fun getOnboardingForDefaultAudienceRequestJsonString(request: AdaptyGetOnboardingForDefaultAudienceRequest): String {
        return buildJsonObject {
            put("placement_id", request.placementId)
            put("locale", request.locale)
            request.fetchPolicy?.let {
                put("fetch_policy", buildFetchPolicyJsonObject(it))
            }
        }.toString()
    }

    private fun getWebPaywallRequestJsonString(request: AdaptyWebPaywallRequest): String {
        return buildJsonObject {
            request.paywallProduct?.let {
                put(
                    "product",
                    jsonInstance.encodeToJsonElement(
                        AdaptyPaywallProductRequest.serializer(),
                        it
                    )
                )
            }
            request.paywall?.let {
                put(
                    "paywall",
                    jsonInstance.encodeToJsonElement(
                        AdaptyPaywallRequestResponse.serializer(),
                        it
                    )
                )
            }
            request.webPresentationRequest?.let {
                put("open_in", it.toSerialName())
            }
        }.toString()
    }

    private fun getUpdateRefundPreferenceRequestJsonString(request: AdaptyIosUpdateRefundPreferenceRequest): String {
        return buildJsonObject {
            put("refund_preference", request.refundPreference.toSerialName())
        }.toString()
    }

    private fun getUpdateCollectingRefundDataConsentRequestJsonString(request: AdaptyIosUpdateCollectingRefundDataRequest): String {
        return buildJsonObject {
            put("consent", request.consent)
        }.toString()
    }

    private fun getCreatePaywallViewRequestJsonString(request: AdaptyUICreatePaywallViewRequest): String {
        return buildJsonObject {
            put(
                "paywall",
                jsonInstance.encodeToJsonElement(
                    AdaptyPaywallRequestResponse.serializer(),
                    request.paywall
                )
            )
            request.loadTimeOutInSeconds?.let { put("load_timeout", it) }
            put("preload_products", request.preloadProducts)
            request.customTags?.let {
                putJsonObject("custom_tags") {
                    it.forEach { (key, value) -> put(key, value) }
                }
            }
            request.customTimers?.let {
                putJsonObject("custom_timers") {
                    it.forEach { (key, value) -> put(key, value) }
                }
            }
        }.toString()
    }

    private fun getPresentViewRequestJsonString(request: AdaptyUIPresentViewRequest): String {
        return buildJsonObject {
            put("id", request.id)
            put("ios_presentation_style", request.iosPresentationStyle.toSerialName())
        }.toString()
    }

    private fun getDismissViewRequestJsonString(request: AdaptyUIDismissViewRequest): String {
        return buildJsonObject {
            put("id", request.id)
            put("destroy", request.destroy)
        }.toString()
    }

    private fun getShowDialogRequestJsonString(request: AdaptyUIShowDialogRequest): String {
        return buildJsonObject {
            put("id", request.id)
            putJsonObject("configuration") {
                request.configuration.title?.let { put("title", it) }
                request.configuration.content?.let { put("content", it) }
                put("default_action_title", request.configuration.defaultActionTitle)
                request.configuration.secondaryActionTitle?.let { put("secondary_action_title", it) }
            }
        }.toString()
    }

    private fun getCreateOnboardingViewRequestJsonString(request: AdaptyUICreateOnboardingViewRequest): String {
        return buildJsonObject {
            put(
                "onboarding",
                jsonInstance.encodeToJsonElement(
                    AdaptyOnboardingRequestResponse.serializer(),
                    request.onboarding
                )
            )
            request.externalUrlsPresentation?.let {
                put("external_urls_presentation", it.toSerialName())
            }
        }.toString()
    }

    private fun AdaptyUIIOSPresentationStyleRequest.toSerialName(): String = when (this) {
        AdaptyUIIOSPresentationStyleRequest.FULLSCREEN -> "full_screen"
        AdaptyUIIOSPresentationStyleRequest.PAGESHEET -> "page_sheet"
    }

    private fun AdaptyWebPresentationRequest.toSerialName(): String = when (this) {
        AdaptyWebPresentationRequest.EXTERNAL_BROWSER -> "browser_out_app"
        AdaptyWebPresentationRequest.IN_APP_BROWSER -> "browser_in_app"
    }

    private fun AdaptyIosRefundPreferenceRequest.toSerialName(): String = when (this) {
        AdaptyIosRefundPreferenceRequest.NO_PREFERENCE -> "no_preference"
        AdaptyIosRefundPreferenceRequest.GRANT -> "grant"
        AdaptyIosRefundPreferenceRequest.DECLINE -> "decline"
    }
}
