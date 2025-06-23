import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyAndroidSubscriptionUpdateParametersRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallProductsRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowOnboardingRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyMakePurchaseRequest
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallProductRequest
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyReportTransactionRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetIntegrationIdentifierRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateAttributionRequest
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
        AdaptyPluginMethod.SET_FALLBACK_PAYWALLS -> getSetFallbackPaywallsRequestJsonString(param as String)
        AdaptyPluginMethod.LOG_SHOW_PAYWALL -> getAdaptyLogShowPaywallRequest(param as AdaptyLogShowPaywallRequest)
        AdaptyPluginMethod.LOG_SHOW_ONBOARDING -> getAdaptyLogShowOnboardingRequest(param as AdaptyLogShowOnboardingRequest)
        AdaptyPluginMethod.GET_PAYWALL_FOR_DEFAULT_AUDIENCE -> getAdaptyGetPaywallForDefaultAudienceRequest(
            param as AdaptyGetPaywallForDefaultAudienceRequest
        )

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
                put("ip_address_collection_disabled", configuration.ipAddressCollectionDisabled)
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
            put("is_offer_personalized", adaptyMakePurchaseRequest.isOfferPersonalized)
            put(
                "product",
                jsonInstance.encodeToJsonElement(
                    AdaptyPaywallProductRequest.serializer(),
                    adaptyMakePurchaseRequest.paywallProduct
                )
            )
            adaptyMakePurchaseRequest.subscriptionUpdateParams?.let { subscriptionUpdateParams ->
                put(
                    "subscription_update_params",
                    jsonInstance.encodeToJsonElement(
                        AdaptyAndroidSubscriptionUpdateParametersRequest.serializer(),
                        subscriptionUpdateParams
                    )
                )
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

    private fun getAdaptyLogShowOnboardingRequest(request: AdaptyLogShowOnboardingRequest): String {
        val jsonObject = buildJsonObject {
            put("params", buildJsonObject {
                put("onboarding_screen_order", request.params.onboardingScreenOrder)
                put("onboarding_name", request.params.onboardingName)
                put("onboarding_screen_name", request.params.onboardingScreenName)
            })
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

}
