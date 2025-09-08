import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.utils.toJsonObject
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyPaywallProductSubscription
import com.adapty.kmp.models.AdaptyPeriodUnit
import com.adapty.kmp.models.AdaptyPrice
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyRenewalType
import com.adapty.kmp.models.AdaptySubscriptionOfferPaymentMode
import com.adapty.kmp.models.AdaptySubscriptionOfferType
import com.adapty.kmp.models.AdaptySubscriptionPeriod
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object AdaptyPluginResponseTemplate {

    internal fun getJsonString(method: AdaptyPluginMethod, successData: Any): String =
        when (method) {
            AdaptyPluginMethod.ACTIVATE -> genericSuccessResponse()
            AdaptyPluginMethod.IDENTIFY -> genericSuccessResponse()
            AdaptyPluginMethod.UPDATE_PROFILE -> genericSuccessResponse()
            AdaptyPluginMethod.UPDATE_ATTRIBUTION -> genericSuccessResponse()
            AdaptyPluginMethod.GET_PROFILE -> getSuccessProfileResponse(successData as AdaptyProfile)
            AdaptyPluginMethod.RESTORE_PURCHASES -> getSuccessProfileResponse(successData as AdaptyProfile)
            AdaptyPluginMethod.LOGOUT -> genericSuccessResponse()
            AdaptyPluginMethod.SET_INTEGRATION_IDENTIFIER -> genericSuccessResponse()
            AdaptyPluginMethod.GET_PAYWALL -> getSuccessPaywallResponse(successData as AdaptyPaywall)
            AdaptyPluginMethod.GET_PAYWALL_PRODUCTS -> {
                @Suppress("UNCHECKED_CAST")
                getSuccessPaywallProductsResponse(successData as List<AdaptyPaywallProduct>)
            }

            AdaptyPluginMethod.MAKE_PURCHASE -> getSuccessPurchaseResultResponse(successData as AdaptyPurchaseResult)
            AdaptyPluginMethod.REPORT_TRANSACTION -> getSuccessReportTransactionResponse(successData as AdaptyProfile)
            AdaptyPluginMethod.SET_FALLBACK_PAYWALLS -> genericSuccessResponse()
            AdaptyPluginMethod.LOG_SHOW_PAYWALL -> genericSuccessResponse()
            AdaptyPluginMethod.GET_PAYWALL_FOR_DEFAULT_AUDIENCE -> getSuccessPaywallResponse(
                successData as AdaptyPaywall
            )


            else -> throw Exception(
                "Unknown method type. Please, provide response json template in " +
                        "AdaptyPluginResponseTemplate.kt for ${method.methodName} method. " +
                        "Suggested method name is ${method.methodName}ResponseJsonString()"
            )
        }


    fun genericSuccessResponse(): String {
        val jsonResponse = buildJsonObject {
            put("success", true)
        }
        return jsonResponse.toString()
    }

    fun errorResponse(error: AdaptyError): String {
        val jsonResponse = buildJsonObject {
            put("error", buildJsonObject {
                put("adapty_code", error.code.value)
                put("message", error.message)
                put("detail", error.detail)
            })
        }
        return jsonResponse.toString()
    }

    private fun getSuccessProfileResponse(adaptyProfile: AdaptyProfile): String {
        return buildSuccessJsonString(buildProfileJson(adaptyProfile = adaptyProfile))
    }

    private fun buildProfileJson(adaptyProfile: AdaptyProfile) = buildJsonObject {

        val customAttributesJson = adaptyProfile.customAttributes.toJsonObject()

        val accessLevelsJson = buildJsonObject {
            adaptyProfile.accessLevels.forEach { (key, accessLevel) ->
                put(key, buildJsonObject {
                    put("id", accessLevel.id)
                    put("is_active", accessLevel.isActive)
                    put("vendor_product_id", accessLevel.vendorProductId)
                    put("store", accessLevel.store)
                    put("activated_at", accessLevel.activatedAt)
                    put("renewed_at", accessLevel.renewedAt)
                    put("expires_at", accessLevel.expiresAt)
                    put("is_lifetime", accessLevel.isLifetime)
                    put("active_introductory_offer_type", accessLevel.activeIntroductoryOfferType)
                    put("active_promotional_offer_type", accessLevel.activePromotionalOfferType)
                    put("active_promotional_offer_id", accessLevel.activePromotionalOfferId)
                    accessLevel.offerId?.let { put("offer_id", it) } // Include only if not null
                    put("will_renew", accessLevel.willRenew)
                    put("is_in_grace_period", accessLevel.isInGracePeriod)
                    put("unsubscribed_at", accessLevel.unsubscribedAt)
                    put("billing_issue_detected_at", accessLevel.billingIssueDetectedAt)
                    put("starts_at", accessLevel.startsAt)
                    put("cancellation_reason", accessLevel.cancellationReason)
                    put("is_refund", accessLevel.isRefund)
                })
            }
        }

        val subscriptionsJson = buildJsonObject {
            adaptyProfile.subscriptions.forEach { (key, subscription) ->
                put(key, buildJsonObject {
                    put("store", subscription.store)
                    put("vendor_product_id", subscription.vendorProductId)
                    put("vendor_transaction_id", subscription.vendorTransactionId)
                    put("vendor_original_transaction_id", subscription.vendorOriginalTransactionId)
                    put("is_active", subscription.isActive)
                    put("is_lifetime", subscription.isLifetime)
                    put("activated_at", subscription.activatedAt)
                    put("renewed_at", subscription.renewedAt)
                    put("expires_at", subscription.expiresAt)
                    put("starts_at", subscription.startsAt)
                    put("unsubscribed_at", subscription.unsubscribedAt)
                    put("billing_issue_detected_at", subscription.billingIssueDetectedAt)
                    put("is_in_grace_period", subscription.isInGracePeriod)
                    put("is_refund", subscription.isRefund)
                    put("is_sandbox", subscription.isSandbox)
                    put("will_renew", subscription.willRenew)
                    put("active_introductory_offer_type", subscription.activeIntroductoryOfferType)
                    put("active_promotional_offer_type", subscription.activePromotionalOfferType)
                    put("active_promotional_offer_id", subscription.activePromotionalOfferId)
                    put("offer_id", subscription.offerId)
                    put("cancellation_reason", subscription.cancellationReason)
                })
            }
        }

        val nonSubscriptionsJson = buildJsonObject {
            adaptyProfile.nonSubscriptions.forEach { (key, nonSubscriptionList) ->
                put(key, buildJsonArray {
                    nonSubscriptionList.forEach { nonSubscription ->
                        add(buildJsonObject {
                            put("purchase_id", nonSubscription.purchaseId)
                            put("store", nonSubscription.store)
                            put("vendor_product_id", nonSubscription.vendorProductId)
                            put("vendor_transaction_id", nonSubscription.vendorTransactionId)
                            put("purchased_at", nonSubscription.purchasedAt)
                            put("is_sandbox", nonSubscription.isSandbox)
                            put("is_refund", nonSubscription.isRefund)
                            put("is_consumable", nonSubscription.isConsumable)
                        })
                    }
                })
            }
        }

        put("profile_id", adaptyProfile.profileId)
        put("customer_user_id", adaptyProfile.customerUserId)
        put("segment_hash", adaptyProfile.segmentId)
        put("custom_attributes", customAttributesJson)
        put("paid_access_levels", accessLevelsJson)
        put("subscriptions", subscriptionsJson)
        put("non_subscriptions", nonSubscriptionsJson)
        put("timestamp", JsonPrimitive(AdaptyFakeTestData.CURRENT_TIMESTAMP_MILLIS))
        put("is_test_user", adaptyProfile.isTestUser)
    }

    private fun getSuccessPaywallResponse(adaptyPaywall: AdaptyPaywall): String {

        val productsJsonArray = buildJsonArray {
            adaptyPaywall.products.forEach { product ->
                add(buildJsonObject {
                    put("vendor_product_id", product.vendorId)
                    put("adapty_product_id", product.adaptyProductId)
                    put("promotional_offer_id", product.promotionalOfferId)
                    put("win_back_offer_id", product.winBackOfferId)
                    put("base_plan_id", product.basePlanId)
                    put("offer_id", product.offerId)
                })
            }
        }

        val paywallResponseJson = buildJsonObject {
            put("developer_id", adaptyPaywall.placementId)
            put("paywall_id", adaptyPaywall.instanceIdentity)
            put("paywall_name", adaptyPaywall.name)
            put("audience_name", adaptyPaywall.audienceName)
            put("ab_test_name", adaptyPaywall.abTestName)
            put("variation_id", adaptyPaywall.variationId)
            put("revision", adaptyPaywall.revision)
            put("response_created_at", adaptyPaywall.version)
            adaptyPaywall.remoteConfig?.let { remoteConfig ->
                put("remote_config", buildJsonObject {
                    put("lang", remoteConfig.locale)
                    put("data", remoteConfig.dataJsonString)
                })
            }
            adaptyPaywall.viewConfiguration?.let { viewConfiguration ->
                put("paywall_builder", buildJsonObject {
                    put("paywall_builder_id", viewConfiguration.paywallBuilderId)
                    put("lang", viewConfiguration.locale)
                })
            }

            put("products", productsJsonArray)
            put("payload_data", "testPayloadData")

        }
        return buildSuccessJsonString(paywallResponseJson)
    }


    private fun getSuccessPaywallProductsResponse(adaptyPaywallProducts: List<AdaptyPaywallProduct>): String {
        val paywallProductListJson = buildJsonArray {
            adaptyPaywallProducts.forEach { product ->
                add(buildJsonObject {
                    put("vendor_product_id", product.vendorProductId)
                    put("adapty_product_id", product.adaptyProductId)
                    put("paywall_product_index", product.paywallProductIndex)
                    put("paywall_variation_id", product.paywallVariationId)
                    put("paywall_ab_test_name", product.paywallABTestName)
                    put("paywall_name", product.paywallName)
                    put("audience_name", product.audienceName)
                    put("localized_description", product.localizedDescription)
                    put("localized_title", product.localizedTitle)
                    put("is_family_shareable", product.isFamilyShareable)
                    put("region_code", product.regionCode)

                    // Price
                    put("price", buildPriceJson(product.price))

                    // Subscription
                    product.subscription?.let { sub ->
                        put("subscription", buildSubscriptionJson(sub))
                    }

                    put("payload_data", product.payloadData)
                })
            }
        }

        return buildSuccessJsonString(paywallProductListJson)
    }

    private fun buildPriceJson(price: AdaptyPrice) = buildJsonObject {
        put("currency_code", price.currencyCode)
        put("currency_symbol", price.currencySymbol)
        put("amount", price.amount)
        put("localized_string", price.localizedString)
    }

    private fun buildSubscriptionPeriodJson(subscriptionPeriod: AdaptySubscriptionPeriod) =
        buildJsonObject {
            put(
                "unit", when (subscriptionPeriod.unit) {
                    AdaptyPeriodUnit.MONTH -> "month"
                    AdaptyPeriodUnit.YEAR -> "year"
                    AdaptyPeriodUnit.WEEK -> "week"
                    AdaptyPeriodUnit.DAY -> "day"
                    AdaptyPeriodUnit.UNKNOWN -> "unknown"
                }
            )
            put(
                "number_of_units",
                subscriptionPeriod.numberOfUnits
            )
        }

    private fun buildSubscriptionJson(sub: AdaptyPaywallProductSubscription) = buildJsonObject {
        put("group_identifier", sub.groupIdentifier)
        put(
            "renewal_type", when (sub.renewalType) {
                AdaptyRenewalType.PREPAID -> "prepaid"
                AdaptyRenewalType.AUTORENEWABLE -> "autorenewable"
            }
        )
        put("base_plan_id", sub.basePlanId)
        put("period", buildSubscriptionPeriodJson(sub.period))
        put("localized_period", sub.localizedPeriod)

        sub.offer?.let { offer ->
            put("offer", buildJsonObject {
                put("offer_identifier", buildJsonObject {
                    put("id", offer.offerIdentifier.id)
                    put(
                        "type", when (offer.offerIdentifier.type) {
                            AdaptySubscriptionOfferType.INTRODUCTORY -> "introductory"
                            AdaptySubscriptionOfferType.PROMOTIONAL -> "promotional"
                            AdaptySubscriptionOfferType.WINBACK -> "win_back"
                        }
                    )
                })
                put(
                    "offer_tags",
                    JsonArray(offer.offerTags?.map { JsonPrimitive(it) }
                        ?: emptyList()))

                put("phases", buildJsonArray {
                    offer.phases.forEach { phase ->
                        add(buildJsonObject {
                            put("price", buildPriceJson(phase.price))
                            put("number_of_periods", phase.numberOfPeriods)
                            put(
                                "payment_mode", when (phase.paymentMode) {
                                    AdaptySubscriptionOfferPaymentMode.PAY_AS_YOU_GO -> "pay_as_you_go"
                                    AdaptySubscriptionOfferPaymentMode.PAY_UP_FRONT -> "pay_up_front"
                                    AdaptySubscriptionOfferPaymentMode.FREE_TRIAL -> "free_trial"
                                    AdaptySubscriptionOfferPaymentMode.UNKNOWN -> "unknown"
                                }
                            )
                            put(
                                "subscription_period",
                                buildSubscriptionPeriodJson(phase.subscriptionPeriod)
                            )
                            put("localized_subscription_period", phase.localizedSubscriptionPeriod)
                            put("localized_number_of_periods", phase.localizedNumberOfPeriods)
                        })
                    }
                })
            })
        }
    }

    private fun getSuccessPurchaseResultResponse(adaptyPurchaseResult: AdaptyPurchaseResult): String {
        val resultJson = buildJsonObject {
            when (adaptyPurchaseResult) {
                is AdaptyPurchaseResult.UserCanceled -> put("type", "user_cancelled")
                is AdaptyPurchaseResult.Pending -> put("type", "pending")
                is AdaptyPurchaseResult.Success -> {
                    put("type", "success")
                    put("profile", buildProfileJson(adaptyPurchaseResult.profile))
                }

            }
        }
        return buildSuccessJsonString(resultJson)
    }

    private fun getSuccessReportTransactionResponse(adaptyProfile: AdaptyProfile): String {
        return buildSuccessJsonString(buildProfileJson(adaptyProfile = adaptyProfile))
    }


    private fun buildSuccessJsonString(jsonElement: JsonElement): String {
        val successDataJson = buildJsonObject {
            put("success", jsonElement)
        }
        return successDataJson.toString()
    }
}