import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyPaywallProductReference
import com.adapty.kmp.models.AdaptyPaywallProductSubscription
import com.adapty.kmp.models.AdaptyPeriodUnit
import com.adapty.kmp.models.AdaptyPlacement
import com.adapty.kmp.models.AdaptyPrice
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyRemoteConfig
import com.adapty.kmp.models.AdaptyRenewalType
import com.adapty.kmp.models.AdaptySubscriptionOffer
import com.adapty.kmp.models.AdaptySubscriptionOfferIdentifier
import com.adapty.kmp.models.AdaptySubscriptionOfferPaymentMode
import com.adapty.kmp.models.AdaptySubscriptionOfferPhase
import com.adapty.kmp.models.AdaptySubscriptionOfferType
import com.adapty.kmp.models.AdaptySubscriptionPeriod
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal object AdaptyFakeTestData {

    const val API_KEY = "PUBLIC_SDK_KEY"
    const val CUSTOMER_USER_ID = "testUserId"
    const val ASSET_ID = "testAssetId"
    const val CURRENT_TIMESTAMP_MILLIS = 1745859166 * 1000L
    const val TEST_DATE = "2025-01-01T12:00:00"
    const val INTEGRATION_KEY = "testIntegrationKey"
    const val INTEGRATION_VALUE = "testIntegrationValue"
    const val TRANSACTION_ID = "test_transaction_id_#1"
    const val VARIATION_ID = "test_variation_id_#2"
    const val PLACEMENT_ID = "test_placement_id_#3"
    const val LOCALE = "en"

    const val ONBOARDING_SCREEN_NAME = "onboarding_screen_name_#1"
    const val ONBOARDING_NAME = "onboarding_name_#4"
    const val ONBOARDING_SCREEN_ORDER = 3


    fun getProfile(): AdaptyProfile {

        val accessLevels = mapOf(
            "1" to AdaptyProfile.AccessLevel(
                id = "1",
                isActive = true,
                vendorProductId = "1",
                offerId = null,
                store = "google",
                activatedAt = TEST_DATE,
                startsAt = TEST_DATE,
                renewedAt = TEST_DATE,
                expiresAt = TEST_DATE,
                isLifetime = false,
                cancellationReason = "Test Cancellation reason",
                isRefund = false,
                activeIntroductoryOfferType = "pay_up_front",
                activePromotionalOfferType = "free_trial",
                activePromotionalOfferId = "2",
                willRenew = true,
                isInGracePeriod = false,
                unsubscribedAt = TEST_DATE,
                billingIssueDetectedAt = TEST_DATE
            )
        )

        val customAttributes = mapOf(
            "test_attribute" to "test_value",
            "test_attribute_2" to 12.0
        )

        return AdaptyProfile(
            profileId = "1",
            segmentId = "1",
            customerUserId = "1",
            accessLevels = accessLevels,
            subscriptions = mapOf(),
            nonSubscriptions = mapOf(),
            customAttributes = customAttributes,
            isTestUser = false
        )
    }

    fun getPaywall(): AdaptyPaywall {
        return AdaptyPaywall(
            placement = AdaptyPlacement(
                id = "123",
                audienceName = "testAudience",
                abTestName = "test",
                revision = 1,
                placementAudienceVersionId = "test"
            ),
            instanceIdentity = "456",
            name = "testPaywall",
            variationId = "1",
            products = listOf(
                AdaptyPaywallProductReference(
                    vendorId = "1",
                    adaptyProductId = "1",
                    promotionalOfferId = "promotionalOfferId - #3",
                    winBackOfferId = "winBackOfferId -#2",
                    basePlanId = "basePlanId - #4",
                    offerId = "offerId - #5",
                    productType = "productType",
                    accessLevelId = "accessLevelId"
                )
            ),
            remoteConfig = AdaptyRemoteConfig(
                dataJsonString = buildJsonObject {
                    put("stringKey", "testString")
                    put("intKey", 123)
                    put("doubleKey", 123.0)
                    put("booleanKey", true)
                    put("objectKey", buildJsonObject {
                        put("stringKey", "testString")
                    })
                }.toString(),
                locale = "en",
                dataMap = mapOf(
                    "stringKey" to "testString",
                    "intKey" to 123,
                    "doubleKey" to 123.0,
                    "booleanKey" to true,
                    "objectKey" to mapOf(
                        "stringKey" to "testString"
                    )
                )
            ),
            payloadData = "testPayloadData",
            requestLocale = "en",
            webPurchaseUrl = null
        )
    }


    fun getPaywallProductList(): List<AdaptyPaywallProduct> {
        val price = AdaptyPrice(
            currencyCode = "USD",
            currencySymbol = "$",
            amount = 9.99f,
            localizedString = "$9.99"
        )

        val subscriptionPeriod = AdaptySubscriptionPeriod(
            unit = AdaptyPeriodUnit.MONTH,
            numberOfUnits = 1
        )

        val offerPhase = AdaptySubscriptionOfferPhase(
            price = price,
            numberOfPeriods = 1,
            paymentMode = AdaptySubscriptionOfferPaymentMode.FREE_TRIAL,
            subscriptionPeriod = subscriptionPeriod,
            localizedSubscriptionPeriod = "1 month",
            localizedNumberOfPeriods = "1"
        )

        val offer = AdaptySubscriptionOffer(
            offerIdentifier = AdaptySubscriptionOfferIdentifier(
                id = "intro_offer",
                type = AdaptySubscriptionOfferType.INTRODUCTORY
            ),
            phases = listOf(offerPhase),
            offerTags = listOf("welcome")
        )

        val subscription = AdaptyPaywallProductSubscription(
            groupIdentifier = "com.example.group",
            period = subscriptionPeriod,
            localizedPeriod = "1 month",
            offer = offer,
            renewalType = AdaptyRenewalType.AUTORENEWABLE,
            basePlanId = "base_plan_001"
        )

        val product = AdaptyPaywallProduct(
            vendorProductId = "com.example.product.monthly",
            adaptyProductId = "adapty_product_001",
            paywallVariationId = "variation_1",
            paywallProductIndex = 1,
            paywallABTestName = "main_test",
            paywallName = "Main Paywall",
            audienceName = "main_audience",
            localizedDescription = "Full access to premium features.",
            localizedTitle = "Premium Monthly",
            isFamilyShareable = true,
            regionCode = "US",
            price = price,
            subscription = subscription,
            payloadData = null,
            accessLevelId = "accessLevelId",
            productType = "productType",
        )

        return listOf(product)
    }


    fun getSuccessPurchaseResult(): AdaptyPurchaseResult {
        return AdaptyPurchaseResult.Success(profile = getProfile())
    }

}