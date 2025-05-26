import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallProductsRequest
import com.adapty.kmp.internal.plugin.request.AdaptyGetPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowOnboardingRequest
import com.adapty.kmp.internal.plugin.request.AdaptyLogShowPaywallRequest
import com.adapty.kmp.internal.plugin.request.AdaptyMakePurchaseRequest
import com.adapty.kmp.internal.plugin.request.AdaptyOnboardingScreenParametersRequest
import com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest
import com.adapty.kmp.internal.plugin.request.AdaptyReportTransactionRequest
import com.adapty.kmp.internal.plugin.request.AdaptySetIntegrationIdentifierRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUpdateAttributionRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallProductRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallRequest
import com.adapty.kmp.internal.plugin.request.asAdaptySubscriptionUpdateParametersRequest
import com.adapty.kmp.internal.plugin.request.toAdaptyCustomAttributesRequest
import com.adapty.kmp.internal.utils.createJsonInstance
import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateParameters
import com.adapty.kmp.models.AdaptyAndroidSubscriptionUpdateReplacementMode
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyPaywallFetchPolicy
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.fold
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds


typealias ApiCallWithErrorCallBack = (onError: (AdaptyError?) -> Unit) -> Unit

class AdaptyImplTest {

    private lateinit var fakeAdaptyPlugin: FakeAdaptyPlugin
    private lateinit var adaptyImpl: AdaptyImpl

    private val testError = AdaptyError(
        code = AdaptyErrorCode.UNKNOWN,
        message = "Test Error Message",
        detail = "Test Error Message Detail"
    )


    @BeforeTest
    fun setUp() {
        fakeAdaptyPlugin = FakeAdaptyPlugin()
        adaptyImpl = AdaptyImpl(fakeAdaptyPlugin)
    }

    @Test
    fun `activate method - verify request json and handle success and error`() {
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

        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.activate(configuration = config, onError = onError)
        }

        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.ACTIVATE,
            param = config,
            onSuccess = { error -> assertNull(error) },
            onError = { error -> assertEquals(testError, error) }
        )
    }


    @Test
    fun `identify method - verify request and response`() {
        val customerUserId = AdaptyFakeTestData.CUSTOMER_USER_ID
        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.identify(customerUserId = customerUserId, onError = onError)
        }

        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.IDENTIFY,
            param = customerUserId
        )
    }

    @Test
    fun `updateProfile method - verify request and response`() {
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

        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.updateProfile(params = adaptyProfileParameters, onError = onError)
        }

        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.UPDATE_PROFILE,
            param = adaptyProfileParameters
        )
    }

    @Test
    fun `getProfile method - verify request and response`() = runTest {
        verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.getProfile() },
            method = AdaptyPluginMethod.GET_PROFILE,
            expectedSuccessData = AdaptyFakeTestData.getProfile()
        )
    }

    @Test
    fun `restorePurchases method - verify request and response`() = runTest {

        verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.restorePurchases()
            },
            method = AdaptyPluginMethod.RESTORE_PURCHASES,
            expectedSuccessData = AdaptyFakeTestData.getProfile()
        )
    }

    @Test
    fun `logout method - verify request and response`() {
        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.logout(onError = onError)
        }
        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.LOGOUT,
            param = Unit
        )
    }

    @Test
    fun `setIntegrationIdentifier method - verify request and response`() {
        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.setIntegrationIdentifier(
                key = AdaptyFakeTestData.INTEGRATION_KEY,
                value = AdaptyFakeTestData.INTEGRATION_VALUE,
                onError = onError
            )
        }
        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.SET_INTEGRATION_IDENTIFIER,
            param = AdaptySetIntegrationIdentifierRequest(
                keyValues = mapOf(
                    AdaptyFakeTestData.INTEGRATION_KEY to AdaptyFakeTestData.INTEGRATION_VALUE
                )
            )
        )
    }

    @Test
    fun `getPaywall method - verify request and response`() = runTest {
        verifyApiCallResultBehavior(
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
                loadTimeoutInSeconds = 30,
                fetchPolicy = AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad
            ),
            expectedSuccessData = AdaptyFakeTestData.getPaywall()
        )
    }

    @Test
    fun `getPaywallForDefaultAudience method - verify request and response`() = runTest {
        verifyApiCallResultBehavior(
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
                fetchPolicy = com.adapty.kmp.internal.plugin.request.AdaptyPaywallFetchPolicyRequest.ReturnCacheDataElseLoad
            ),
            expectedSuccessData = AdaptyFakeTestData.getPaywall()
        )
    }

    @Test
    fun `getPaywallProducts method - verify request and response`() = runTest {
        val paywall = AdaptyFakeTestData.getPaywall()
        val paywallProductList = AdaptyFakeTestData.getPaywallProductList()

        verifyApiCallResultBehavior(
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
        val subscriptionUpdateParameters = AdaptyAndroidSubscriptionUpdateParameters(
            oldSubVendorProductId = "old_vendor_product_id",
            replacementMode = AdaptyAndroidSubscriptionUpdateReplacementMode.CHARGE_FULL_PRICE,
        )

        verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.makePurchase(
                    product = paywallProduct,
                    isOfferPersonalized = isOfferPersonalized,
                    subscriptionUpdateParams = subscriptionUpdateParameters
                )
            },
            method = AdaptyPluginMethod.MAKE_PURCHASE,
            param = AdaptyMakePurchaseRequest(
                paywallProduct = paywallProduct.asAdaptyPaywallProductRequest(),
                isOfferPersonalized = isOfferPersonalized,
                subscriptionUpdateParams = subscriptionUpdateParameters.asAdaptySubscriptionUpdateParametersRequest()
            ),
            expectedSuccessData = AdaptyFakeTestData.getSuccessPurchaseResult(),
        )
    }

    @Test
    fun `updateAttribution method - verify request and response`() {

        val attribution = mapOf(
            "status" to "non_organic|organic|unknown",
            "channel" to "Google Ads",
            "campaign" to "Christmas Sale",
            "ad_group" to "ad group",
            "ad_set" to "ad set",
            "creative" to "creative id"
        )

        val source = "custom"

        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.updateAttribution(
                attribution = attribution,
                source = source,
                onError = onError
            )
        }
        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.UPDATE_ATTRIBUTION,
            param = AdaptyUpdateAttributionRequest(
                attribution = createJsonInstance().encodeToString(attribution.toAdaptyCustomAttributesRequest()),
                source = source
            )
        )
    }

    @Test
    fun `reportTransaction method - verify request and response`() = runTest {
        verifyApiCallResultBehavior(
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
            expectedSuccessData = AdaptyFakeTestData.getProfile(),
        )
    }

    @Test
    fun `setFallBackPaywalls method - verify request and response`() {
        val assetId = AdaptyFakeTestData.ASSET_ID
        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.setFallbackPaywalls(assetId = assetId, onError = onError)
        }

        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.SET_FALLBACK_PAYWALLS,
            param = assetId
        )
    }

    @Test
    fun `logShowPaywall method - verify request and response`() {
        val paywall = AdaptyFakeTestData.getPaywall()
        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.logShowPaywall(paywall = paywall, onError = onError)
        }

        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.LOG_SHOW_PAYWALL,
            param = AdaptyLogShowPaywallRequest(paywall = paywall.asAdaptyPaywallRequest())
        )
    }

    @Test
    fun `logShowOnboarding method - verify request and response`() {
        val apiCall: ApiCallWithErrorCallBack = { onError ->
            adaptyImpl.logShowOnboarding(
                name = AdaptyFakeTestData.ONBOARDING_NAME,
                screenName = AdaptyFakeTestData.ONBOARDING_SCREEN_NAME,
                screenOrder = AdaptyFakeTestData.ONBOARDING_SCREEN_ORDER,
                onError = onError
            )
        }

        verifyApiCallErrorCallbackBehavior(
            apiCall = apiCall,
            method = AdaptyPluginMethod.LOG_SHOW_ONBOARDING,
            param = AdaptyLogShowOnboardingRequest(
                params = AdaptyOnboardingScreenParametersRequest(
                    onboardingName = AdaptyFakeTestData.ONBOARDING_NAME,
                    onboardingScreenName = AdaptyFakeTestData.ONBOARDING_SCREEN_NAME,
                    onboardingScreenOrder = AdaptyFakeTestData.ONBOARDING_SCREEN_ORDER
                )
            )
        )
    }


    private fun verifyApiCallErrorCallbackBehavior(
        apiCall: (onError: (AdaptyError?) -> Unit) -> Unit,
        method: AdaptyPluginMethod,
        param: Any,
        onSuccess: (AdaptyError?) -> Unit = { error -> assertNull(error) },
        onError: (AdaptyError?) -> Unit = { error -> assertEquals(testError, error) },
        simulateSuccessResponse: () -> Unit = { fakeAdaptyPlugin.simulateSuccessResponse() },
        simulateErrorResponse: () -> Unit = { fakeAdaptyPlugin.simulateErrorResponse(testError) },
    ) {
        // Simulate a successful response
        simulateSuccessResponse()

        var actualError: AdaptyError? = null

        // Trigger the API call
        apiCall.invoke { error -> actualError = error }

        // Verify the JSON request was sent correctly
        verifyRequestJson(method, param)

        // Verify success case
        onSuccess(actualError)

        // Now simulate an error response
        simulateErrorResponse()

        // Trigger the API call again
        apiCall.invoke { error -> actualError = error }

        // Verify error case
        onError(actualError)
    }

    private suspend fun <T : Any> verifyApiCallResultBehavior(
        apiCall: suspend () -> AdaptyResult<T>,
        method: AdaptyPluginMethod,
        param: Any = Unit,
        expectedSuccessData: T,
        onSuccess: (T) -> Unit = { actual -> assertEquals(expectedSuccessData, actual) },
        onError: (AdaptyError) -> Unit = { error -> assertEquals(testError, error) },
        simulateSuccessResponse: () -> Unit = {
            fakeAdaptyPlugin.simulateSuccessResponseForMethod(method, expectedSuccessData)
        },
        simulateErrorResponse: () -> Unit = {
            fakeAdaptyPlugin.simulateErrorResponse(testError)
        },
    ) {
        // Simulate a successful response
        simulateSuccessResponse()

        // Trigger the API call
        var result = apiCall()

        // Verify the JSON request was sent correctly
        verifyRequestJson(method, param)

        // Verify success case
        result.fold(
            onSuccess = onSuccess,
            onError = { error ->
                fail("Expected success but got error: $error")
            }
        )

        // Now simulate an error response
        simulateErrorResponse()

        // Trigger the API call again
        result = apiCall()

        //Verify error case
        result.fold(
            onSuccess = { successData ->
                fail("Expected error but got success: $successData")
            },
            onError = onError
        )
    }


    private fun verifyRequestJson(method: AdaptyPluginMethod, param: Any) {
        val expectedMethodName = method.methodName
        val expectedRequestJson =
            AdaptyPluginRequestTemplate.getJsonString(method = method, param = param)

        val actualRequestJson = fakeAdaptyPlugin.capturedRequestJsonString
        val actualMethodName = fakeAdaptyPlugin.capturedRequestMethodName

        assertEquals(expectedMethodName, actualMethodName)
        assertJsonStringEquals(expectedRequestJson, actualRequestJson)
    }


    // Helper function to assert that two JSON strings are equivalent
    private fun assertJsonStringEquals(
        expectedJsonString: String?,
        actualJsonString: String?,
        message: String? = null
    ) {
        val actualJson = createJsonInstance().parseToJsonElement(actualJsonString ?: "").jsonObject
        val expectedJson =
            createJsonInstance().parseToJsonElement(expectedJsonString ?: "").jsonObject

        assertEquals(expectedJson, actualJson, message)
    }
}
