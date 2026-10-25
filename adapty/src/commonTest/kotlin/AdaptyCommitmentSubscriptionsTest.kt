import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyFlowPaywallRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyFlowRequestResponse
import com.adapty.kmp.internal.plugin.request.AdaptyMakePromotedPurchaseRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPromotedProductRequest
import com.adapty.kmp.internal.plugin.response.AdaptyDidReceivePromotedPurchaseResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyPromotedProduct
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.isAndroidPlatform
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyPeriodUnit
import com.adapty.kmp.models.AdaptyPrice
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptySubscriptionBillingPlan
import com.adapty.kmp.models.AdaptySubscriptionCommitmentInfo
import com.adapty.kmp.models.AdaptySubscriptionPeriod
import com.adapty.kmp.models.AdaptySubscriptionPricingTerms
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Commitment subscriptions / billing plans (cross_platform 4.3.1, native iOS SDK 4.3.x).
 *
 * Covers: product `subscription.pricing_terms` decoding, `billing_plan_id` sent back in
 * `make_purchase` / web paywall requests, product reference `pricing_terms` round trip,
 * promoted purchase `purchase_intent_id`, profile `billing_plan_id` / `commitment`, error 1060.
 */
class AdaptyCommitmentSubscriptionsTest {

    private lateinit var fakeAdaptyPlugin: FakeAdaptyPlugin
    private lateinit var adaptyImpl: AdaptyImpl

    @BeforeTest
    fun setUp() {
        fakeAdaptyPlugin = FakeAdaptyPlugin()
        adaptyImpl = AdaptyImpl(adaptyPlugin = fakeAdaptyPlugin, appMainScope = TestScope())
    }

    // =========================================================================
    // Product pricing terms
    // =========================================================================

    @Test
    fun `getPaywallProducts - decodes iOS pricing terms with commitment info`() = runTest {
        val product = fetchProducts(buildProductJson(withPricingTerms = true, withOffer = true)).single()

        val expected = AdaptySubscriptionPricingTerms(
            billingPlan = AdaptySubscriptionBillingPlan.MONTHLY,
            billingPrice = AdaptyPrice(
                amount = MONTHLY_AMOUNT,
                currencyCode = "USD",
                currencySymbol = "$",
                localizedString = "$6.99"
            ),
            billingPeriod = AdaptySubscriptionPeriod(unit = AdaptyPeriodUnit.MONTH, numberOfUnits = 1),
            commitmentInfo = AdaptySubscriptionCommitmentInfo(
                price = AdaptyPrice(
                    amount = COMMITMENT_AMOUNT,
                    currencyCode = "USD",
                    currencySymbol = "$",
                    localizedString = "$83.88"
                ),
                period = AdaptySubscriptionPeriod(unit = AdaptyPeriodUnit.YEAR, numberOfUnits = 1),
            ),
        )
        assertEquals(expected, product.subscription?.pricingTerms)
        assertEquals("monthly", product.subscription?.pricingTerms?.billingPlan?.value)
    }

    @Test
    fun `getPaywallProducts - unknown billing plan survives decoding`() = runTest {
        val product = fetchProducts(
            buildProductJson(withPricingTerms = true, withOffer = false, billingPlanId = "future_plan")
        ).single()

        val billingPlan = product.subscription?.pricingTerms?.billingPlan
        assertEquals(AdaptySubscriptionBillingPlan("future_plan"), billingPlan)
        assertFalse(billingPlan == AdaptySubscriptionBillingPlan.MONTHLY)
        assertFalse(billingPlan == AdaptySubscriptionBillingPlan.UP_FRONT)
    }

    @Test
    fun `getPaywallProducts - pricing terms are null when native omits them (Android)`() = runTest {
        val product = fetchProducts(buildProductJson(withPricingTerms = false, withOffer = true)).single()

        assertNotNull(product.subscription)
        assertNull(product.subscription?.pricingTerms)
    }

    // =========================================================================
    // billing_plan_id written into product requests
    // =========================================================================

    @Test
    fun `makePurchase - sends billing plan id alongside the offer`() = runTest {
        val product = fetchProducts(buildProductJson(withPricingTerms = true, withOffer = true)).single()

        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.MAKE_PURCHASE,
            AdaptyFakeTestData.getSuccessPurchaseResult()
        )
        adaptyImpl.makePurchase(product = product, parameters = null)

        assertEquals(AdaptyPluginMethod.MAKE_PURCHASE.methodName, fakeAdaptyPlugin.capturedRequestMethodName)
        val expectedSubscription = buildJsonObject {
            putJsonObject("pricing_terms") { put("billing_plan_id", "monthly") }
            putJsonObject("offer") {
                putJsonObject("offer_identifier") {
                    put("id", OFFER_ID)
                    put("type", "promotional")
                }
            }
        }
        assertEquals(expectedSubscription, capturedProductSubscription())
    }

    @Test
    fun `makePurchase - sends only the billing plan when the product has no offer`() = runTest {
        val product = fetchProducts(buildProductJson(withPricingTerms = true, withOffer = false)).single()

        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.MAKE_PURCHASE,
            AdaptyFakeTestData.getSuccessPurchaseResult()
        )
        adaptyImpl.makePurchase(product = product, parameters = null)

        val expectedSubscription = buildJsonObject {
            putJsonObject("pricing_terms") { put("billing_plan_id", "monthly") }
        }
        assertEquals(expectedSubscription, capturedProductSubscription())
    }

    @Test
    fun `makePurchase - omits subscription when there is neither billing plan nor offer`() = runTest {
        val product = fetchProducts(buildProductJson(withPricingTerms = false, withOffer = false)).single()

        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.MAKE_PURCHASE,
            AdaptyFakeTestData.getSuccessPurchaseResult()
        )
        adaptyImpl.makePurchase(product = product, parameters = null)

        assertFalse("subscription" in capturedProductJson())
    }

    @Test
    fun `makePurchase - Android product without pricing terms sends no pricing_terms key`() = runTest {
        val product = fetchProducts(buildProductJson(withPricingTerms = false, withOffer = true)).single()

        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.MAKE_PURCHASE,
            AdaptyFakeTestData.getSuccessPurchaseResult()
        )
        adaptyImpl.makePurchase(product = product, parameters = null)

        val subscription = capturedProductSubscription()
        assertFalse("pricing_terms" in subscription)
        assertTrue("offer" in subscription)
    }

    @Test
    fun `openWebPaywall and createWebPaywallUrl - send billing plan id in the product`() = runTest {
        val product = fetchProducts(buildProductJson(withPricingTerms = true, withOffer = false)).single()
        val expectedSubscription = buildJsonObject {
            putJsonObject("pricing_terms") { put("billing_plan_id", "monthly") }
        }

        fakeAdaptyPlugin.simulateSuccessResponse()
        adaptyImpl.openWebPaywall(product = product, openIn = AdaptyWebPresentation.IN_APP_BROWSER)
        assertEquals(AdaptyPluginMethod.OPEN_WEB_PAYWALL.methodName, fakeAdaptyPlugin.capturedRequestMethodName)
        assertEquals(expectedSubscription, capturedProductSubscription())

        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL,
            "https://example.com/web-paywall"
        )
        adaptyImpl.createWebPaywallUrl(product = product)
        assertEquals(AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL.methodName, fakeAdaptyPlugin.capturedRequestMethodName)
        assertEquals(expectedSubscription, capturedProductSubscription())
    }

    // =========================================================================
    // Product reference pricing_terms round trip
    // =========================================================================

    @Test
    fun `product reference pricing terms survive native - Kotlin - native round trip`() = runTest {
        val references = buildJsonArray {
            add(buildProductReferenceJson())
            // Android-shaped reference: no pricing_terms, base plan + offer instead
            add(buildJsonObject {
                put("vendor_product_id", "android.product")
                put("adapty_product_id", "adapty_android_product")
                put("access_level_id", "premium")
                put("product_type", "subscription")
                put("base_plan_id", "base-plan")
                put("offer_id", "android-offer")
            })
        }
        val flow = fetchFlowWithProductReferences(references)

        // get_paywall_products
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.GET_PAYWALL_PRODUCTS,
            emptyList<AdaptyPaywallProduct>()
        )
        adaptyImpl.getPaywallProducts(flow)
        assertEquals(references, capturedFlowProductReferences("flow"))

        // log_show_flow
        fakeAdaptyPlugin.simulateSuccessResponse()
        adaptyImpl.logShowFlow(flow)
        assertEquals(references, capturedFlowProductReferences("flow"))

        // create_web_paywall_url (flow paywall)
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL,
            "https://example.com/web-paywall"
        )
        adaptyImpl.createWebPaywallUrl(flowPaywall = flow.paywalls.single())
        val paywall = capturedRequestJson().getValue("paywall").jsonObject
        assertEquals(references, paywall.getValue("products"))
    }

    @Test
    fun `product reference - legacy flat iOS offer ids are no longer sent`() = runTest {
        val flow = fetchFlowWithProductReferences(buildJsonArray { add(buildProductReferenceJson()) })

        fakeAdaptyPlugin.simulateSuccessResponse()
        adaptyImpl.logShowFlow(flow)

        val reference = capturedFlowProductReferences("flow").single().jsonObject
        assertFalse("promotional_offer_id" in reference)
        assertFalse("win_back_offer_id" in reference)
    }

    // =========================================================================
    // Promoted purchase: purchase_intent_id
    // =========================================================================

    @Test
    fun `promoted purchase - purchase intent id and billing plan go from event to request`() = runTest {
        val event = buildJsonObject {
            put("product", buildJsonObject {
                put("purchase_intent_id", PURCHASE_INTENT_ID)
                put("vendor_product_id", AdaptyFakeTestData.PRODUCT_ID)
                put("localized_description", "Full access to premium features.")
                put("localized_title", "Premium Yearly")
                put("is_family_shareable", false)
                put("price", buildPriceJson(COMMITMENT_AMOUNT, "$83.88"))
                put("subscription", buildSubscriptionJson(withPricingTerms = true, withOffer = true))
            })
        }.toString()

        val response = event.decodeJsonString<AdaptyDidReceivePromotedPurchaseResponse>()
        assertNotNull(response, "Failed to parse promoted purchase event JSON")
        val product = response.product.asAdaptyPromotedProduct()

        assertEquals(PURCHASE_INTENT_ID, product.purchaseIntentId)
        assertEquals(AdaptySubscriptionBillingPlan.MONTHLY, product.subscription?.pricingTerms?.billingPlan)

        val expectedRequest = buildJsonObject {
            putJsonObject("product") {
                put("purchase_intent_id", PURCHASE_INTENT_ID)
                put("vendor_product_id", AdaptyFakeTestData.PRODUCT_ID)
                putJsonObject("subscription") {
                    putJsonObject("pricing_terms") { put("billing_plan_id", "monthly") }
                    putJsonObject("offer") {
                        putJsonObject("offer_identifier") {
                            put("id", OFFER_ID)
                            put("type", "promotional")
                        }
                    }
                }
            }
        }
        val actualRequest = jsonInstance.encodeToJsonElement(
            AdaptyMakePromotedPurchaseRequest.serializer(),
            AdaptyMakePromotedPurchaseRequest(product = product.asAdaptyPromotedProductRequest())
        )
        assertEquals(expectedRequest, actualRequest)

        // make_promoted_purchase is iOS only: end-to-end through AdaptyImpl where it is allowed.
        if (!isAndroidPlatform) {
            fakeAdaptyPlugin.simulateSuccessResponseForMethod(
                AdaptyPluginMethod.MAKE_PROMOTED_PURCHASE,
                AdaptyFakeTestData.getSuccessPurchaseResult()
            )
            adaptyImpl.makePromotedPurchase(product)
            assertEquals(expectedRequest, capturedRequestJson())
        }
    }

    @Test
    fun `promoted purchase - no purchase intent id is sent when the event has none`() {
        val product = AdaptyFakeTestData.getPromotedProduct()
        assertNull(product.purchaseIntentId)

        val request = jsonInstance.encodeToJsonElement(
            AdaptyMakePromotedPurchaseRequest.serializer(),
            AdaptyMakePromotedPurchaseRequest(product = product.asAdaptyPromotedProductRequest())
        ).jsonObject.getValue("product").jsonObject
        assertFalse("purchase_intent_id" in request)
    }

    // =========================================================================
    // Profile: billing_plan_id + commitment
    // =========================================================================

    @Test
    fun `getProfile - decodes billing plan and commitment on access levels and subscriptions`() = runTest {
        val profile = fetchProfile(withCommitment = true)

        val expectedCommitment = AdaptyProfile.SubscriptionCommitmentInfo(
            billingPeriodNumber = 3,
            totalBillingPeriods = 12,
            expiresAt = COMMITMENT_EXPIRES_AT,
        )

        val accessLevel = profile.accessLevels.getValue("premium")
        assertEquals(AdaptySubscriptionBillingPlan.MONTHLY, accessLevel.billingPlan)
        assertEquals(expectedCommitment, accessLevel.commitmentInfo)

        val subscription = profile.subscriptions.getValue(AdaptyFakeTestData.PRODUCT_ID)
        assertEquals(AdaptySubscriptionBillingPlan.MONTHLY, subscription.billingPlan)
        assertEquals(expectedCommitment, subscription.commitmentInfo)
    }

    @Test
    fun `getProfile - billing plan and commitment are null when native omits them`() = runTest {
        val profile = fetchProfile(withCommitment = false)

        val accessLevel = profile.accessLevels.getValue("premium")
        assertNull(accessLevel.billingPlan)
        assertNull(accessLevel.commitmentInfo)

        val subscription = profile.subscriptions.getValue(AdaptyFakeTestData.PRODUCT_ID)
        assertNull(subscription.billingPlan)
        assertNull(subscription.commitmentInfo)
    }

    // =========================================================================
    // Error 1060
    // =========================================================================

    @Test
    fun `error 1060 maps to BILLING_PLAN_UNAVAILABLE`() = runTest {
        val plugin = RawResponseAdaptyPlugin(
            buildJsonObject {
                putJsonObject("error") {
                    put("adapty_code", 1060)
                    put("message", "Billing plan unavailable")
                }
            }.toString()
        )
        val result = AdaptyImpl(adaptyPlugin = plugin, appMainScope = TestScope())
            .makePurchase(product = AdaptyFakeTestData.getPaywallProductList().first(), parameters = null)

        val error = assertIs<AdaptyResult.Error>(result).error
        assertEquals(AdaptyErrorCode.BILLING_PLAN_UNAVAILABLE, error.code)
        assertEquals(1060, AdaptyErrorCode.BILLING_PLAN_UNAVAILABLE.value)
    }

    @Test
    fun `error 1060 - FakeAdaptyPlugin error response round trip`() = runTest {
        val error = AdaptyError(code = AdaptyErrorCode.BILLING_PLAN_UNAVAILABLE, message = "unavailable")
        fakeAdaptyPlugin.simulateErrorResponse(error)

        val result = adaptyImpl.makePurchase(
            product = AdaptyFakeTestData.getPaywallProductList().first(),
            parameters = null
        )
        assertEquals(error, assertIs<AdaptyResult.Error>(result).error)
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Returns a preconfigured raw JSON response, for decoding native payloads end to end. */
    private class RawResponseAdaptyPlugin(private val responseJson: String) : AdaptyPlugin {
        override fun initialize() {}
        override fun executePlatformSpecific(method: String, data: String, onResult: (String?) -> Unit) {
            onResult(responseJson)
        }
    }

    private fun <T> AdaptyResult<T>.valueOrFail(): T = when (this) {
        is AdaptyResult.Success -> value
        is AdaptyResult.Error -> fail("Expected success but got error: $error")
    }

    private suspend fun fetchProducts(vararg products: JsonObject): List<AdaptyPaywallProduct> {
        val response = buildJsonObject { put("success", JsonArray(products.toList())) }.toString()
        return AdaptyImpl(adaptyPlugin = RawResponseAdaptyPlugin(response), appMainScope = TestScope())
            .getPaywallProducts(AdaptyFakeTestData.getFlow())
            .valueOrFail()
    }

    private suspend fun fetchFlowWithProductReferences(references: JsonArray): AdaptyFlow {
        val flowDto = AdaptyFakeTestData.getFlowRequestResponse()
        val paywallDto = flowDto.variations.single().copy(products = emptyList())
        val paywallJson = jsonInstance.encodeToJsonElement(
            AdaptyFlowPaywallRequestResponse.serializer(),
            paywallDto
        ).jsonObject
        val flowJson = jsonInstance.encodeToJsonElement(
            AdaptyFlowRequestResponse.serializer(),
            flowDto.copy(variations = emptyList())
        ).jsonObject
        val nativeFlowJson = JsonObject(
            flowJson + ("variations" to JsonArray(listOf(JsonObject(paywallJson + ("products" to references)))))
        )
        val response = buildJsonObject { put("success", nativeFlowJson) }.toString()
        return AdaptyImpl(adaptyPlugin = RawResponseAdaptyPlugin(response), appMainScope = TestScope())
            .getFlow(placementId = AdaptyFakeTestData.PLACEMENT_ID)
            .valueOrFail()
    }

    private suspend fun fetchProfile(withCommitment: Boolean): AdaptyProfile {
        val commitmentFields: JsonObject = buildJsonObject {
            if (withCommitment) {
                put("billing_plan_id", "monthly")
                putJsonObject("commitment") {
                    put("billing_period_number", 3)
                    put("total_billing_periods", 12)
                    put("expires_at", COMMITMENT_EXPIRES_AT)
                }
            }
        }
        val accessLevel = JsonObject(buildJsonObject {
            put("id", "premium")
            put("is_active", true)
            put("vendor_product_id", AdaptyFakeTestData.PRODUCT_ID)
            put("store", "app_store")
            put("activated_at", "2026-10-01T10:00:00.000Z")
            put("renewed_at", "2026-10-01T10:00:00.000Z")
            put("expires_at", "2026-11-01T10:00:00.000Z")
            put("is_lifetime", false)
            put("will_renew", true)
            put("is_in_grace_period", false)
            put("is_refund", false)
        } + commitmentFields)
        val subscription = JsonObject(buildJsonObject {
            put("store", "app_store")
            put("vendor_product_id", AdaptyFakeTestData.PRODUCT_ID)
            put("vendor_transaction_id", "tx_2")
            put("vendor_original_transaction_id", "tx_1")
            put("is_active", true)
            put("is_lifetime", false)
            put("activated_at", "2026-10-01T10:00:00.000Z")
            put("renewed_at", "2026-10-01T10:00:00.000Z")
            put("expires_at", "2026-11-01T10:00:00.000Z")
            put("is_in_grace_period", false)
            put("is_refund", false)
            put("is_sandbox", true)
            put("will_renew", true)
        } + commitmentFields)
        val response = buildJsonObject {
            putJsonObject("success") {
                put("profile_id", "profile_1")
                put("customer_user_id", AdaptyFakeTestData.CUSTOMER_USER_ID)
                put("segment_hash", "segment")
                put("is_test_user", false)
                put("timestamp", AdaptyFakeTestData.CURRENT_TIMESTAMP_MILLIS)
                putJsonObject("custom_attributes") {}
                putJsonObject("paid_access_levels") { put("premium", accessLevel) }
                putJsonObject("subscriptions") { put(AdaptyFakeTestData.PRODUCT_ID, subscription) }
                putJsonObject("non_subscriptions") {}
            }
        }.toString()
        return AdaptyImpl(adaptyPlugin = RawResponseAdaptyPlugin(response), appMainScope = TestScope())
            .getProfile()
            .valueOrFail()
    }

    private fun capturedRequestJson(): JsonObject =
        jsonInstance.parseToJsonElement(fakeAdaptyPlugin.capturedRequestJsonString ?: "").jsonObject

    private fun capturedProductJson(): JsonObject = capturedRequestJson().getValue("product").jsonObject

    private fun capturedProductSubscription(): JsonObject =
        capturedProductJson().getValue("subscription").jsonObject

    private fun capturedFlowProductReferences(flowKey: String): JsonArray =
        capturedRequestJson().getValue(flowKey).jsonObject
            .getValue("variations").jsonArray.single().jsonObject
            .getValue("products").jsonArray

    private fun buildProductReferenceJson(): JsonObject = buildJsonObject {
        put("flow_product_id", "flow_product_1")
        put("vendor_product_id", AdaptyFakeTestData.PRODUCT_ID)
        put("adapty_product_id", AdaptyFakeTestData.ADAPTY_PRODUCT_ID)
        put("access_level_id", "premium")
        put("product_type", "subscription")
        put("pricing_terms", buildJsonArray {
            add(buildJsonObject {
                put("billing_plan_id", "monthly")
                put("promotional_offer_id", "promo_monthly")
            })
            add(buildJsonObject {
                put("billing_plan_id", "up_front")
                put("promotional_offer_id", "promo_up_front")
                put("win_back_offer_id", "win_back_up_front")
            })
            add(buildJsonObject { put("billing_plan_id", "future_plan") })
        })
    }

    private fun buildPriceJson(amount: Double, localized: String): JsonObject = buildJsonObject {
        put("amount", amount)
        put("currency_code", "USD")
        put("currency_symbol", "$")
        put("localized_string", localized)
    }

    private fun buildPeriodJson(unit: String, numberOfUnits: Int): JsonObject = buildJsonObject {
        put("unit", unit)
        put("number_of_units", numberOfUnits)
    }

    private fun buildSubscriptionJson(
        withPricingTerms: Boolean,
        withOffer: Boolean,
        billingPlanId: String = "monthly",
    ): JsonObject = buildJsonObject {
        put("group_identifier", "group_premium")
        put("period", buildPeriodJson("year", 1))
        put("localized_period", "1 year")
        if (withPricingTerms) {
            putJsonObject("pricing_terms") {
                put("billing_plan_id", billingPlanId)
                put("billing_price", buildPriceJson(MONTHLY_AMOUNT, "$6.99"))
                put("billing_period", buildPeriodJson("month", 1))
                putJsonObject("commitment_info") {
                    put("price", buildPriceJson(COMMITMENT_AMOUNT, "$83.88"))
                    put("period", buildPeriodJson("year", 1))
                }
            }
        }
        if (withOffer) {
            putJsonObject("offer") {
                putJsonObject("offer_identifier") {
                    put("id", OFFER_ID)
                    put("type", "promotional")
                }
                put("phases", JsonArray(emptyList()))
            }
        }
    }

    private fun buildProductJson(
        withPricingTerms: Boolean,
        withOffer: Boolean,
        billingPlanId: String = "monthly",
    ): JsonObject = buildJsonObject {
        put("vendor_product_id", AdaptyFakeTestData.PRODUCT_ID)
        put("adapty_product_id", AdaptyFakeTestData.ADAPTY_PRODUCT_ID)
        put("paywall_product_index", 0)
        put("paywall_variation_id", AdaptyFakeTestData.VARIATION_ID)
        put("paywall_ab_test_name", "main_test")
        put("paywall_name", "Main Paywall")
        put("access_level_id", "premium")
        put("product_type", "subscription")
        put("localized_description", "Full access to premium features.")
        put("localized_title", "Premium Yearly")
        put("is_family_shareable", false)
        put("price", buildPriceJson(COMMITMENT_AMOUNT, "$83.88"))
        put("subscription", buildSubscriptionJson(withPricingTerms, withOffer, billingPlanId))
    }

    private companion object {
        const val OFFER_ID = "promo_offer_1"
        const val PURCHASE_INTENT_ID = "purchase_intent_1"
        const val MONTHLY_AMOUNT = 6.99
        const val COMMITMENT_AMOUNT = 83.88
        const val COMMITMENT_EXPIRES_AT = "2027-10-01T10:00:00.000Z"
    }
}
