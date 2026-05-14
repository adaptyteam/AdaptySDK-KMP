import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.utils.toJsonObject
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyPaywallProductSubscription
import com.adapty.kmp.models.AdaptyPeriodUnit
import com.adapty.kmp.models.AdaptyPrice
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyRenewalType
import com.adapty.kmp.models.AdaptySubscriptionOffer
import com.adapty.kmp.models.AdaptySubscriptionOfferIdentifier
import com.adapty.kmp.models.AdaptySubscriptionOfferPaymentMode
import com.adapty.kmp.models.AdaptySubscriptionOfferPhase
import com.adapty.kmp.models.AdaptySubscriptionOfferType
import com.adapty.kmp.models.AdaptySubscriptionPeriod
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPaywallView
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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
            AdaptyPluginMethod.REPORT_TRANSACTION -> genericSuccessResponse()
            AdaptyPluginMethod.SET_FALLBACK -> genericSuccessResponse()
            AdaptyPluginMethod.LOG_SHOW_PAYWALL -> genericSuccessResponse()
            AdaptyPluginMethod.GET_PAYWALL_FOR_DEFAULT_AUDIENCE -> getSuccessPaywallResponse(
                successData as AdaptyPaywall
            )

            AdaptyPluginMethod.GET_CURRENT_INSTALLATION_STATUS -> getSuccessInstallationStatusResponse()
            AdaptyPluginMethod.IS_ACTIVATED -> getSuccessBooleanResponse(successData as Boolean)
            AdaptyPluginMethod.PRESENT_CODE_REDEMPTION_SHEET -> genericSuccessResponse()
            AdaptyPluginMethod.UPDATE_REFUND_PREFERENCE -> getSuccessBooleanResponse(successData as Boolean)
            AdaptyPluginMethod.UPDATE_COLLECTING_REFUND_DATA_CONSENT -> getSuccessBooleanResponse(successData as Boolean)
            AdaptyPluginMethod.GET_ONBOARDING -> getSuccessOnboardingResponse(successData as AdaptyOnboarding)
            AdaptyPluginMethod.GET_ONBOARDING_FOR_DEFAULT_AUDIENCE -> getSuccessOnboardingResponse(
                successData as AdaptyOnboarding
            )

            AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL -> getSuccessStringResponse(successData as String)
            AdaptyPluginMethod.OPEN_WEB_PAYWALL -> genericSuccessResponse()

            // UI methods
            AdaptyPluginMethod.CREATE_PAYWALL_VIEW -> getSuccessPaywallViewResponse(successData as AdaptyUIPaywallView)
            AdaptyPluginMethod.PRESENT_PAYWALL_VIEW -> genericSuccessResponse()
            AdaptyPluginMethod.DISMISS_PAYWALL_VIEW -> genericSuccessUnitResponse()
            AdaptyPluginMethod.SHOW_DIALOG -> getSuccessDialogActionTypeResponse(successData as AdaptyUIDialogActionType)
            AdaptyPluginMethod.CREATE_ONBOARDING_VIEW -> getSuccessOnboardingViewResponse(successData as AdaptyUIOnboardingView)
            AdaptyPluginMethod.PRESENT_ONBOARDING_VIEW -> genericSuccessResponse()
            AdaptyPluginMethod.DISMISS_ONBOARDING_VIEW -> genericSuccessUnitResponse()

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

    fun genericSuccessUnitResponse(): String {
        val jsonResponse = buildJsonObject {
            put("success", buildJsonObject {})
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
                    put("product_type", product.productType)
                    put("access_level_id", product.accessLevelId)
                })
            }
        }

        val paywallResponseJson = buildJsonObject {
            put("placement", buildJsonObject {
                put("developer_id", adaptyPaywall.placement.id)
                put("audience_name", adaptyPaywall.placement.audienceName)
                put("revision", adaptyPaywall.placement.revision)
                put("ab_test_name", adaptyPaywall.placement.abTestName)
                put("placement_audience_version_id", adaptyPaywall.placement.placementAudienceVersionId)

            })
            put("developer_id", adaptyPaywall.placement.id)
            put("paywall_id", adaptyPaywall.instanceIdentity)
            put("paywall_name", adaptyPaywall.name)
            put("variation_id", adaptyPaywall.variationId)
            put("request_locale", adaptyPaywall.requestLocale)
            put("response_created_at", adaptyPaywall.responseCreatedAt)
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
                    put("localized_description", product.localizedDescription)
                    put("localized_title", product.localizedTitle)
                    put("is_family_shareable", product.isFamilyShareable)
                    put("region_code", product.regionCode)
                    put("access_level_id", product.accessLevelId)
                    put("product_type", product.productType)

                    // Price
                    put("price", buildPriceJson(product.price))

                    // Subscription
                    product.subscription?.let { sub ->
                        put("subscription", buildSubscriptionJson(sub))
                    }

                    put("web_purchase_url", product.webPurchaseUrl)
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

    private fun getSuccessOnboardingResponse(onboarding: AdaptyOnboarding): String {
        val onboardingJson = buildJsonObject {
            put("placement", buildJsonObject {
                put("developer_id", onboarding.placement.id)
                put("audience_name", onboarding.placement.audienceName)
                put("revision", onboarding.placement.revision)
                put("ab_test_name", onboarding.placement.abTestName)
                put("placement_audience_version_id", onboarding.placement.placementAudienceVersionId)
            })
            put("onboarding_id", onboarding.id)
            put("onboarding_name", onboarding.name)
            put("variation_id", onboarding.variationId)
            put("onboarding_builder", buildJsonObject {
                put("config_url", onboarding.onboardingBuilderConfigUrl)
            })
            put("response_created_at", onboarding.responseCreatedAt)
            put("request_locale", onboarding.requestLocale)
            onboarding.remoteConfig?.let { remoteConfig ->
                put("remote_config", buildJsonObject {
                    put("lang", remoteConfig.locale)
                    put("data", remoteConfig.dataJsonString)
                })
            }
        }
        return buildSuccessJsonString(onboardingJson)
    }

    private fun getSuccessInstallationStatusResponse(): String {
        val statusJson = buildJsonObject {
            put("status", "not_determined")
        }
        return buildSuccessJsonString(statusJson)
    }

    private fun getSuccessBooleanResponse(value: Boolean): String {
        return buildSuccessJsonString(JsonPrimitive(value))
    }

    private fun getSuccessStringResponse(value: String): String {
        return buildSuccessJsonString(JsonPrimitive(value))
    }

    private fun getSuccessPaywallViewResponse(view: AdaptyUIPaywallView): String {
        val viewJson = buildJsonObject {
            put("id", view.id)
            put("placement_id", view.placementId)
            put("variation_id", view.variationId)
        }
        return buildSuccessJsonString(viewJson)
    }

    private fun getSuccessOnboardingViewResponse(view: AdaptyUIOnboardingView): String {
        val viewJson = buildJsonObject {
            put("id", view.id)
            put("placement_id", view.placementId)
            put("variation_id", view.variationId)
        }
        return buildSuccessJsonString(viewJson)
    }

    private fun getSuccessDialogActionTypeResponse(actionType: AdaptyUIDialogActionType): String {
        val actionString = when (actionType) {
            AdaptyUIDialogActionType.PRIMARY -> "primary"
            AdaptyUIDialogActionType.SECONDARY -> "secondary"
        }
        return buildSuccessJsonString(JsonPrimitive(actionString))
    }

    private fun buildSuccessJsonString(jsonElement: JsonElement): String {
        val successDataJson = buildJsonObject {
            put("success", jsonElement)
        }
        return successDataJson.toString()
    }

    // =========================================================================
    // EVENT JSON BUILDERS
    // =========================================================================

    internal fun getEventJsonString(
        event: AdaptyPluginEvent,
        params: Map<String, Any?> = emptyMap()
    ): String = when (event) {
        // Paywall view events
        AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION -> buildPaywallViewActionEvent(params)
        AdaptyPluginEvent.PAYWALL_VIEW_DID_APPEAR -> buildPaywallViewOnlyEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_DISAPPEAR -> buildPaywallViewOnlyEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_SYSTEM_BACK_ACTION -> buildEventViewJson().toString()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_SELECT_PRODUCT -> buildPaywallViewSelectProductEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_START_PURCHASE -> buildPaywallViewWithProductEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_PURCHASE -> buildPaywallViewFinishPurchaseEvent(params)
        AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_PURCHASE -> buildPaywallViewFailPurchaseEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_START_RESTORE -> buildPaywallViewOnlyEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_RESTORE -> buildPaywallViewFinishRestoreEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RESTORE -> buildPaywallViewWithErrorEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RENDERING -> buildPaywallViewWithErrorEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_LOADING_PRODUCTS -> buildPaywallViewWithErrorEvent()
        AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION ->
            buildPaywallViewWebPaymentEvent(params)

        // Onboarding view events
        AdaptyPluginEvent.ONBOARDING_DID_FINISH_LOADING -> buildOnboardingViewWithMetaEvent()
        AdaptyPluginEvent.ONBOARDING_DID_FAIL_WITH_ERROR -> buildOnboardingViewWithErrorEvent()
        AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION -> buildOnboardingAnalyticsEvent(params)
        AdaptyPluginEvent.ONBOARDING_ON_CLOSE_ACTION -> buildOnboardingActionIdEvent()
        AdaptyPluginEvent.ONBOARDING_ON_CUSTOM_ACTION -> buildOnboardingActionIdEvent()
        AdaptyPluginEvent.ONBOARDING_ON_PAYWALL_ACTION -> buildOnboardingActionIdEvent()
        AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION -> buildOnboardingStateUpdatedEvent(params)

        // Profile & installation events
        AdaptyPluginEvent.DID_LOAD_LATEST_PROFILE -> buildDidLoadLatestProfileEvent()
        AdaptyPluginEvent.ON_INSTALLATION_DETAILS_SUCCESS -> buildInstallationDetailsSuccessEvent()
        AdaptyPluginEvent.ON_INSTALLATION_DETAILS_FAIL -> buildInstallationDetailsFailEvent()
    }

    private fun buildEventViewJson(
        viewId: String = AdaptyFakeTestData.EVENT_VIEW_ID,
        placementId: String = AdaptyFakeTestData.PLACEMENT_ID,
        variationId: String = AdaptyFakeTestData.VARIATION_ID
    ): JsonObject = buildJsonObject {
        put("id", viewId)
        put("placement_id", placementId)
        put("variation_id", variationId)
    }

    private fun buildOnboardingMetaJson(
        onboardingId: String = AdaptyFakeTestData.ONBOARDING_ID,
        screenCid: String = AdaptyFakeTestData.SCREEN_CLIENT_ID,
        screenIndex: Int = AdaptyFakeTestData.SCREEN_INDEX,
        totalScreens: Int = AdaptyFakeTestData.TOTAL_SCREENS
    ): JsonObject = buildJsonObject {
        put("onboarding_id", onboardingId)
        put("screen_cid", screenCid)
        put("screen_index", screenIndex)
        put("total_screens", totalScreens)
    }

    private fun buildEventErrorJson(
        code: Int = 0,
        message: String = "Test error message",
        detail: String = "Test error detail"
    ): JsonObject = buildJsonObject {
        put("adapty_code", code)
        put("message", message)
        put("detail", detail)
    }

    private fun buildEventProductJson(): JsonObject = buildJsonObject {
        put("vendor_product_id", AdaptyFakeTestData.PRODUCT_ID)
        put("adapty_product_id", AdaptyFakeTestData.ADAPTY_PRODUCT_ID)
        put("paywall_product_index", 1)
        put("paywall_variation_id", AdaptyFakeTestData.VARIATION_ID)
        put("paywall_ab_test_name", "main_test")
        put("paywall_name", "Main Paywall")
        put("localized_description", "Full access to premium features.")
        put("localized_title", "Premium Monthly")
        put("is_family_shareable", true)
        put("region_code", "US")
        put("price", buildPriceJson(
            AdaptyPrice(
                currencyCode = "USD",
                currencySymbol = "$",
                amount = 9.99,
                localizedString = "$9.99"
            )
        ))
        put("subscription", buildSubscriptionJson(
            AdaptyPaywallProductSubscription(
                groupIdentifier = "com.example.group",
                renewalType = AdaptyRenewalType.AUTORENEWABLE,
                basePlanId = "base_plan_001",
                period = AdaptySubscriptionPeriod(unit = AdaptyPeriodUnit.MONTH, numberOfUnits = 1),
                localizedPeriod = "1 month",
                offer = AdaptySubscriptionOffer(
                    offerIdentifier = AdaptySubscriptionOfferIdentifier(
                        id = "intro_offer",
                        type = AdaptySubscriptionOfferType.INTRODUCTORY
                    ),
                    phases = listOf(
                        AdaptySubscriptionOfferPhase(
                            price = AdaptyPrice(
                                currencyCode = "USD",
                                currencySymbol = "$",
                                amount = 9.99,
                                localizedString = "$9.99"
                            ),
                            numberOfPeriods = 1,
                            paymentMode = AdaptySubscriptionOfferPaymentMode.FREE_TRIAL,
                            subscriptionPeriod = AdaptySubscriptionPeriod(
                                unit = AdaptyPeriodUnit.MONTH,
                                numberOfUnits = 1
                            ),
                            localizedSubscriptionPeriod = "1 month",
                            localizedNumberOfPeriods = "1"
                        )
                    ),
                    offerTags = listOf("welcome")
                )
            )
        ))
        put("access_level_id", "accessLevelId")
        put("product_type", "productType")
    }

    private fun buildEventProfileJson(): JsonObject = buildProfileJson(AdaptyFakeTestData.getProfile())

    private fun buildPaywallViewOnlyEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
    }.toString()

    private fun buildPaywallViewActionEvent(params: Map<String, Any?>): String {
        val actionType = params["action_type"] as? String ?: "close"
        val actionValue = params["action_value"] as? String
        val actionOpenIn = params["action_open_in"] as? String
        return buildJsonObject {
            put("view", buildEventViewJson())
            put("action", buildJsonObject {
                put("type", actionType)
                actionValue?.let { put("value", it) }
                actionOpenIn?.let { put("open_in", it) }
            })
        }.toString()
    }

    private fun buildPaywallViewSelectProductEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("product_id", AdaptyFakeTestData.PRODUCT_ID)
    }.toString()

    private fun buildPaywallViewWithProductEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("product", buildEventProductJson())
    }.toString()

    private fun buildPaywallViewFinishPurchaseEvent(params: Map<String, Any?>): String {
        val purchaseType = params["purchase_type"] as? String ?: "success"
        return buildJsonObject {
            put("view", buildEventViewJson())
            put("product", buildEventProductJson())
            put("purchased_result", buildJsonObject {
                put("type", purchaseType)
                if (purchaseType == "success") {
                    put("profile", buildEventProfileJson())
                }
            })
        }.toString()
    }

    private fun buildPaywallViewFailPurchaseEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("product", buildEventProductJson())
        put("error", buildEventErrorJson())
    }.toString()

    private fun buildPaywallViewFinishRestoreEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("profile", buildEventProfileJson())
    }.toString()

    private fun buildPaywallViewWithErrorEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("error", buildEventErrorJson())
    }.toString()

    private fun buildPaywallViewWebPaymentEvent(params: Map<String, Any?>): String {
        val includeProduct = params["include_product"] as? Boolean ?: true
        val includeError = params["include_error"] as? Boolean ?: true
        return buildJsonObject {
            put("view", buildEventViewJson())
            if (includeProduct) put("product", buildEventProductJson())
            if (includeError) put("error", buildEventErrorJson())
        }.toString()
    }

    // --- Onboarding view event builders ---

    private fun buildOnboardingViewWithMetaEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("meta", buildOnboardingMetaJson())
    }.toString()

    private fun buildOnboardingViewWithErrorEvent(): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("error", buildEventErrorJson())
    }.toString()

    private fun buildOnboardingAnalyticsEvent(params: Map<String, Any?>): String {
        val eventName = params["event_name"] as? String ?: "screen_presented"
        return buildJsonObject {
            put("view", buildEventViewJson())
            put("meta", buildOnboardingMetaJson())
            put("event", buildJsonObject {
                put("name", eventName)
                (params["element_id"] as? String)?.let { put("element_id", it) }
                (params["reply"] as? String)?.let { put("reply", it) }
            })
        }.toString()
    }

    private fun buildOnboardingActionIdEvent(
        actionId: String = AdaptyFakeTestData.ACTION_ID
    ): String = buildJsonObject {
        put("view", buildEventViewJson())
        put("meta", buildOnboardingMetaJson())
        put("action_id", actionId)
    }.toString()

    private fun buildOnboardingStateUpdatedEvent(params: Map<String, Any?>): String {
        val elementType = params["element_type"] as? String ?: "select"
        val elementId = params["element_id"] as? String ?: "select_001"
        return buildJsonObject {
            put("view", buildEventViewJson())
            put("meta", buildOnboardingMetaJson())
            put("action", buildJsonObject {
                put("element_type", elementType)
                put("element_id", elementId)
                when (elementType) {
                    "select" -> put("value", buildJsonObject {
                        put("id", "option_1")
                        put("value", "val_1")
                        put("label", "Option 1")
                    })
                    "multi_select" -> put("value", buildJsonArray {
                        add(buildJsonObject {
                            put("id", "option_1")
                            put("value", "val_1")
                            put("label", "Option 1")
                        })
                        add(buildJsonObject {
                            put("id", "option_2")
                            put("value", "val_2")
                            put("label", "Option 2")
                        })
                    })
                    "input" -> put("value", buildJsonObject {
                        put("type", "text")
                        put("value", "Hello World")
                    })
                    "date_picker" -> put("value", buildJsonObject {
                        put("day", 15)
                        put("month", 6)
                        put("year", 1990)
                    })
                }
            })
        }.toString()
    }

    // --- Profile & installation event builders ---

    private fun buildDidLoadLatestProfileEvent(): String = buildJsonObject {
        put("profile", buildEventProfileJson())
    }.toString()

    private fun buildInstallationDetailsSuccessEvent(): String = buildJsonObject {
        put("details", buildJsonObject {
            put("install_id", "install_abc123")
            put("install_time", "2025-01-01T12:00:00.000Z")
            put("app_launch_count", 5)
        })
    }.toString()

    private fun buildInstallationDetailsFailEvent(): String = buildJsonObject {
        put("error", buildEventErrorJson())
    }.toString()
}