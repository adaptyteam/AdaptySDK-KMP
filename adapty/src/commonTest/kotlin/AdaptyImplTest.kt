import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyGetOnboardingForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetOnboardingRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallProductsRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyMakePurchaseRequest
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest
import com.adapty.kmp.internal.plugin.request.AdaptyReportTransactionRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetIntegrationIdentifierRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateAttributionRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPresentationRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallProductRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPurchaseParametersRequest
import com.adapty.kmp.internal.plugin.request.toAdaptyCustomAttributesRequest
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateParameters
import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateReplacementMode
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyInstallationStatusNotDetermined
import com.adapty.kmp.models.AdaptyIosRefundPreference
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyWebPresentation
import com.adapty.kmp.models.fold
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds


class AdaptyImplTest {

    private lateinit var fakeAdaptyPlugin: FakeAdaptyPlugin
    private lateinit var adaptyImpl: AdaptyImpl


    @BeforeTest
    fun setUp() {
        fakeAdaptyPlugin = FakeAdaptyPlugin()
        adaptyImpl = AdaptyImpl(
            adaptyPlugin = fakeAdaptyPlugin,
            appMainScope = TestScope()
        )
    }

    @Test
    fun `activate method - verify request json and handle success and error`() = runTest {
        val config = AdaptyConfig.Builder(AdaptyFakeTestData.API_KEY)
            .withObserverMode(false)
            .withCustomerUserId(AdaptyFakeTestData.CUSTOMER_USER_ID)
            .withIpAddressCollectionDisabled(false)
            .withGoogleAdvertisingIdCollectionDisabled(false)
            .withAppleIdfaCollectionDisabled(false)
            .withServerCluster(AdaptyConfig.ServerCluster.EU)
            .withLogLevel(AdaptyLogLevel.DEBUG)
            .withActivateUI(false)
            .build()



        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.activate(config) },
            method = AdaptyPluginMethod.ACTIVATE,
            param = config,
            expectedSuccessData = Unit
        )
    }


    @Test
    fun `identify method - verify request and response`() = runTest {
        val customerUserId = AdaptyFakeTestData.CUSTOMER_USER_ID

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.identify(customerUserId = customerUserId) },
            method = AdaptyPluginMethod.IDENTIFY,
            param = customerUserId,
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `updateProfile method - verify request and response`() = runTest {
        val builder = AdaptyProfileParameters.Builder()
            .withEmail("email@email.com")
            .withPhoneNumber("+18888888888")
            .withFirstName("John")
            .withLastName("Appleseed")
            .withGender(AdaptyProfile.Gender.MALE)
            .withCustomAttribute("custom_attribute_key_string", "custom_attribute_value_string")
            .withCustomAttribute("custom_attribute_key_double", 123.0)
            .withBirthday(AdaptyProfile.Date(1970, 1, 3))

        val adaptyProfileParameters = builder.build()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.updateProfile(params = adaptyProfileParameters) },
            method = AdaptyPluginMethod.UPDATE_PROFILE,
            param = adaptyProfileParameters,
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `getProfile method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.getProfile() },
            method = AdaptyPluginMethod.GET_PROFILE,
            expectedSuccessData = AdaptyFakeTestData.getProfile()
        )
    }

    @Test
    fun `restorePurchases method - verify request and response`() = runTest {

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.restorePurchases()
            },
            method = AdaptyPluginMethod.RESTORE_PURCHASES,
            expectedSuccessData = AdaptyFakeTestData.getProfile()
        )
    }

    @Test
    fun `logout method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.logout()
            },
            method = AdaptyPluginMethod.LOGOUT,
            param = Unit,
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `setIntegrationIdentifier method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.setIntegrationIdentifier(
                    key = AdaptyFakeTestData.INTEGRATION_KEY,
                    value = AdaptyFakeTestData.INTEGRATION_VALUE
                )
            },
            method = AdaptyPluginMethod.SET_INTEGRATION_IDENTIFIER,
            param = AdaptySetIntegrationIdentifierRequest(
                keyValues = mapOf(
                    AdaptyFakeTestData.INTEGRATION_KEY to AdaptyFakeTestData.INTEGRATION_VALUE
                )
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `getPaywall method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.getPaywall(
                    placementId = AdaptyFakeTestData.PLACEMENT_ID,
                    locale = AdaptyFakeTestData.LOCALE,
                    loadTimeout = 30.seconds,
                    fetchPolicy = AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad
                )
            },
            method = AdaptyPluginMethod.GET_PAYWALL,
            param = AdaptyGetPaywallRequest(
                placementId = AdaptyFakeTestData.PLACEMENT_ID,
                locale = AdaptyFakeTestData.LOCALE,
                loadTimeoutInSeconds = 30.0,
                fetchPolicy = AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad
            ),
            expectedSuccessData = AdaptyFakeTestData.getPaywall()
        )
    }

    @Test
    fun `getPaywallForDefaultAudience method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.getPaywallForDefaultAudience(
                    placementId = AdaptyFakeTestData.PLACEMENT_ID,
                    locale = AdaptyFakeTestData.LOCALE,
                    fetchPolicy = AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad
                )
            },
            method = AdaptyPluginMethod.GET_PAYWALL_FOR_DEFAULT_AUDIENCE,
            param = AdaptyGetPaywallForDefaultAudienceRequest(
                placementId = AdaptyFakeTestData.PLACEMENT_ID,
                locale = AdaptyFakeTestData.LOCALE,
                fetchPolicy = AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad
            ),
            expectedSuccessData = AdaptyFakeTestData.getPaywall()
        )
    }

    @Test
    fun `getPaywallProducts method - verify request and response`() = runTest {
        val paywall = AdaptyFakeTestData.getPaywall()
        val paywallProductList = AdaptyFakeTestData.getPaywallProductList()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.getPaywallProducts(paywall = paywall)
            },
            method = AdaptyPluginMethod.GET_PAYWALL_PRODUCTS,
            param = AdaptyGetPaywallProductsRequest(paywall = paywall.asAdaptyPaywallRequest()),
            expectedSuccessData = paywallProductList,
        )
    }

    @Test
    fun `makePurchase method - verify request and response`() = runTest {
        val paywallProduct = AdaptyFakeTestData.getPaywallProductList()[0]
        val isOfferPersonalized = true
        val androidSubscriptionUpdateParameters = AdaptyAndroidSubscriptionUpdateParameters(
            oldSubVendorProductId = "old_vendor_product_id",
            replacementMode = AdaptyAndroidSubscriptionUpdateReplacementMode.CHARGE_FULL_PRICE,
        )
        val parameters = AdaptyPurchaseParameters.Builder()
            .setSubscriptionUpdateParams(androidSubscriptionUpdateParameters)
            .setIsOfferPersonalized(isOfferPersonalized)
            .build()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.makePurchase(
                    product = paywallProduct,
                    parameters = parameters
                )
            },
            method = AdaptyPluginMethod.MAKE_PURCHASE,
            param = AdaptyMakePurchaseRequest(
                paywallProduct = paywallProduct.asAdaptyPaywallProductRequest(),
                parameters = parameters.asAdaptyPurchaseParametersRequest()
            ),
            expectedSuccessData = AdaptyFakeTestData.getSuccessPurchaseResult(),
        )
    }

    @Test
    fun `updateAttribution method - verify request and response`() = runTest {

        val attribution = mapOf(
            "status" to "non_organic|organic|unknown",
            "channel" to "Google Ads",
            "campaign" to "Christmas Sale",
            "ad_group" to "ad group",
            "ad_set" to "ad set",
            "creative" to "creative id"
        )

        val source = "custom"

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.updateAttribution(attribution = attribution, source = source)
            },
            method = AdaptyPluginMethod.UPDATE_ATTRIBUTION,
            param = AdaptyUpdateAttributionRequest(
                attribution = jsonInstance.encodeToString(attribution.toAdaptyCustomAttributesRequest()),
                source = source
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `reportTransaction method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.reportTransaction(
                    transactionId = AdaptyFakeTestData.TRANSACTION_ID,
                    variationId = AdaptyFakeTestData.VARIATION_ID
                )
            },
            method = AdaptyPluginMethod.REPORT_TRANSACTION,
            param = AdaptyReportTransactionRequest(
                transactionId = AdaptyFakeTestData.TRANSACTION_ID,
                variationId = AdaptyFakeTestData.VARIATION_ID,
            ),
            expectedSuccessData = Unit,
        )
    }

    @Test
    fun `setFallBack method - verify request and response`() = runTest {
        val assetId = AdaptyFakeTestData.ASSET_ID
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.setFallback(assetId = assetId)
            },
            method = AdaptyPluginMethod.SET_FALLBACK,
            param = assetId,
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `logShowPaywall method - verify request and response`() = runTest {
        val paywall = AdaptyFakeTestData.getPaywall()
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.logShowPaywall(paywall = paywall)
            },
            method = AdaptyPluginMethod.LOG_SHOW_PAYWALL,
            param = AdaptyLogShowPaywallRequest(paywall = paywall.asAdaptyPaywallRequest()),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `getCurrentInstallationStatus method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.getCurrentInstallationStatus() },
            method = AdaptyPluginMethod.GET_CURRENT_INSTALLATION_STATUS,
            expectedSuccessData = AdaptyInstallationStatusNotDetermined,
            onSuccess = { actual ->
                assertEquals(AdaptyInstallationStatusNotDetermined, actual)
            }
        )
    }

    @Test
    fun `isActivated method - verify request and response`() = runTest {
        // Simulate success with true
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.IS_ACTIVATED, true
        )
        val resultTrue = adaptyImpl.isActivated()
        assertEquals(
            AdaptyPluginMethod.IS_ACTIVATED.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )
        assertTrue(resultTrue)

        // Simulate success with false
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.IS_ACTIVATED, false
        )
        val resultFalse = adaptyImpl.isActivated()
        assertFalse(resultFalse)
    }

    @Test
    fun `getOnboarding method - verify request and response`() = runTest {
        val onboarding = AdaptyFakeTestData.getOnboarding()
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.getOnboarding(
                    placementId = AdaptyFakeTestData.PLACEMENT_ID,
                    locale = AdaptyFakeTestData.LOCALE,
                    loadTimeout = 30.seconds,
                    fetchPolicy = AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad
                )
            },
            method = AdaptyPluginMethod.GET_ONBOARDING,
            param = AdaptyGetOnboardingRequest(
                placementId = AdaptyFakeTestData.PLACEMENT_ID,
                locale = AdaptyFakeTestData.LOCALE,
                loadTimeoutInSeconds = 30.0,
                fetchPolicy = AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad
            ),
            expectedSuccessData = onboarding
        )
    }

    @Test
    fun `getOnboardingForDefaultAudience method - verify request and response`() = runTest {
        val onboarding = AdaptyFakeTestData.getOnboarding()
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.getOnboardingForDefaultAudience(
                    placementId = AdaptyFakeTestData.PLACEMENT_ID,
                    locale = AdaptyFakeTestData.LOCALE,
                    fetchPolicy = AdaptyPaywallFetchPolicy.ReturnCacheDataElseLoad
                )
            },
            method = AdaptyPluginMethod.GET_ONBOARDING_FOR_DEFAULT_AUDIENCE,
            param = AdaptyGetOnboardingForDefaultAudienceRequest(
                placementId = AdaptyFakeTestData.PLACEMENT_ID,
                locale = AdaptyFakeTestData.LOCALE,
                fetchPolicy = AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad
            ),
            expectedSuccessData = onboarding
        )
    }

    @Test
    fun `createWebPaywallUrl method - verify request and response`() = runTest {
        val paywall = AdaptyFakeTestData.getPaywall()
        val expectedUrl = "https://pay.adapty.io/test"
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.createWebPaywallUrl(paywall = paywall)
            },
            method = AdaptyPluginMethod.CREATE_WEB_PAYWALL_URL,
            param = AdaptyWebPaywallRequest.fromPaywall(paywall.asAdaptyPaywallRequest()),
            expectedSuccessData = expectedUrl,
        )
    }

    @Test
    fun `openWebPaywall method - verify request and response`() = runTest {
        val paywall = AdaptyFakeTestData.getPaywall()
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.openWebPaywall(
                    paywall = paywall,
                    openIn = AdaptyWebPresentation.IN_APP_BROWSER
                )
            },
            method = AdaptyPluginMethod.OPEN_WEB_PAYWALL,
            param = AdaptyWebPaywallRequest.fromPaywall(
                paywall = paywall.asAdaptyPaywallRequest(),
                webPresentationRequest = AdaptyWebPresentationRequest.IN_APP_BROWSER
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `presentCodeRedemptionSheet method - returns error on Android`() = runTest {
        // presentCodeRedemptionSheet is iOS-only; on Android it returns an error immediately
        val result = adaptyImpl.presentCodeRedemptionSheet()
        result.fold(
            onSuccess = { fail("Expected error on Android but got success") },
            onError = { error ->
                assertEquals(AdaptyErrorCode.DEVELOPER_ERROR, error.code)
            }
        )
    }

    @Test
    fun `updateRefundPreference method - returns error on Android`() = runTest {
        // updateRefundPreference is iOS-only; on Android it returns an error immediately
        val result = adaptyImpl.updateRefundPreference(
            preference = AdaptyIosRefundPreference.NO_PREFERENCE
        )
        result.fold(
            onSuccess = { fail("Expected error on Android but got success") },
            onError = { error ->
                assertEquals(AdaptyErrorCode.DEVELOPER_ERROR, error.code)
            }
        )
    }

    @Test
    fun `updateCollectingRefundDataConsent method - returns error on Android`() = runTest {
        // updateCollectingRefundDataConsent is iOS-only; on Android it returns an error immediately
        val result = adaptyImpl.updateCollectingRefundDataConsent(consent = true)
        result.fold(
            onSuccess = { fail("Expected error on Android but got success") },
            onError = { error ->
                assertEquals(AdaptyErrorCode.DEVELOPER_ERROR, error.code)
            }
        )
    }

    @Test
    fun `setLogLevel method - verify request is sent`() = runTest {
        adaptyImpl.setLogLevel(AdaptyLogLevel.DEBUG)

        assertEquals(
            AdaptyPluginMethod.SET_LOG_LEVEL.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )
    }
}
