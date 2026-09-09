@file:Suppress("DEPRECATION") // references the deprecated onboarding API

import com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
import com.adapty.kmp.AdaptyUIFlowsEventsObserver
import com.adapty.kmp.AdaptyUIObserverModeResolver
import com.adapty.kmp.AdaptyUISystemRequestsHandler
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.response.AdaptyOnInstallationDetailsFailEventResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnInstallationDetailsSuccessEventResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnStateUpdatedActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyDidReceivePromotedPurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyProfileUpdatedResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyError
import com.adapty.kmp.internal.plugin.response.asAdaptyInstallationDetails
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.plugin.response.asAdaptyPromotedProduct
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.models.AdaptyPromotedProduct
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEvent
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventOnboardingStarted
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventScreenCompleted
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventScreenPresented
import com.adapty.kmp.models.AdaptyOnboardingsDatePickerParams
import com.adapty.kmp.models.AdaptyOnboardingsInputParams
import com.adapty.kmp.models.AdaptyOnboardingsMultiSelectParams
import com.adapty.kmp.models.AdaptyOnboardingsSelectParams
import com.adapty.kmp.models.AdaptyOnboardingsStateUpdatedParams
import com.adapty.kmp.models.AdaptyOnboardingsTextInput
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyWebPresentation
import com.adapty.kmp.models.AdaptyUIOnboardingMeta
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIFlowView
import com.adapty.kmp.models.AdaptyUIPermission
import com.adapty.kmp.models.AdaptyUIPermissionResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(AdaptyKMPInternal::class)
class AdaptyEventsTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAdaptyPlugin: FakeAdaptyPlugin
    private lateinit var adaptyUIImpl: AdaptyUIImpl
    private lateinit var appMainScope: CoroutineScope

    // Paywall captured state
    private lateinit var capturedFlowEvents: MutableList<String>
    private var capturedFlowView: AdaptyUIFlowView? = null
    private var capturedAction: AdaptyUIAction? = null
    private var capturedProduct: AdaptyPaywallProduct? = null
    private var capturedProfile: AdaptyProfile? = null
    private var capturedError: AdaptyError? = null
    private var capturedPurchaseResult: AdaptyPurchaseResult? = null
    private var capturedProductId: String? = null
    private var capturedPermission: AdaptyUIPermission? = null
    private var capturedPermissionView: AdaptyUIFlowView? = null
    private var capturedAppReviewView: AdaptyUIFlowView? = null
    private var capturedStartPurchase: (() -> Unit)? = null
    private var capturedFinishPurchase: (() -> Unit)? = null
    private var capturedStartRestore: (() -> Unit)? = null
    private var capturedFinishRestore: (() -> Unit)? = null
    private var capturedAnalyticName: String? = null
    private var capturedAnalyticParams: String? = null

    // Onboarding captured state
    private lateinit var capturedOnboardingEvents: MutableList<String>
    private var capturedOnboardingView: AdaptyUIOnboardingView? = null
    private var capturedMeta: AdaptyUIOnboardingMeta? = null
    private var capturedOnboardingError: AdaptyError? = null
    private var capturedActionId: String? = null
    private var capturedEvent: AdaptyOnboardingsAnalyticsEvent? = null
    private var capturedStateParams: AdaptyOnboardingsStateUpdatedParams? = null
    private var capturedElementId: String? = null

    private val flowObserver = object : AdaptyUIFlowsEventsObserver {

        override val mainUiScope: CoroutineScope get() = TestScope()

        override fun flowViewDidPerformAction(
            view: AdaptyUIFlowView,
            action: AdaptyUIAction
        ) {
            capturedFlowView = view
            capturedAction = action
            capturedFlowEvents.add("didPerformAction")
        }

        override fun flowViewDidAppear(view: AdaptyUIFlowView) {
            capturedFlowView = view
            capturedFlowEvents.add("didAppear")
        }

        override fun flowViewDidDisappear(view: AdaptyUIFlowView) {
            capturedFlowView = view
            capturedFlowEvents.add("didDisappear")
        }

        override fun flowViewDidSelectProduct(view: AdaptyUIFlowView, productId: String) {
            capturedFlowView = view
            capturedProductId = productId
            capturedFlowEvents.add("didSelectProduct")
        }

        override fun flowViewDidStartPurchase(
            view: AdaptyUIFlowView,
            product: AdaptyPaywallProduct
        ) {
            capturedFlowView = view
            capturedProduct = product
            capturedFlowEvents.add("didStartPurchase")
        }

        override fun flowViewDidFinishPurchase(
            view: AdaptyUIFlowView,
            product: AdaptyPaywallProduct,
            purchaseResult: AdaptyPurchaseResult
        ) {
            capturedFlowView = view
            capturedProduct = product
            capturedPurchaseResult = purchaseResult
            capturedFlowEvents.add("didFinishPurchase")
        }

        override fun flowViewDidFailPurchase(
            view: AdaptyUIFlowView,
            product: AdaptyPaywallProduct,
            error: AdaptyError
        ) {
            capturedFlowView = view
            capturedProduct = product
            capturedError = error
            capturedFlowEvents.add("didFailPurchase")
        }

        override fun flowViewDidStartRestore(view: AdaptyUIFlowView) {
            capturedFlowView = view
            capturedFlowEvents.add("didStartRestore")
        }

        override fun flowViewDidFinishRestore(
            view: AdaptyUIFlowView,
            profile: AdaptyProfile
        ) {
            capturedFlowView = view
            capturedProfile = profile
            capturedFlowEvents.add("didFinishRestore")
        }

        override fun flowViewDidFailRestore(view: AdaptyUIFlowView, error: AdaptyError) {
            capturedFlowView = view
            capturedError = error
            capturedFlowEvents.add("didFailRestore")
        }

        override fun flowViewDidReceiveError(view: AdaptyUIFlowView, error: AdaptyError) {
            capturedFlowView = view
            capturedError = error
            capturedFlowEvents.add("didFailRendering")
        }

        override fun flowViewDidFailLoadingProducts(
            view: AdaptyUIFlowView,
            error: AdaptyError
        ) {
            capturedFlowView = view
            capturedError = error
            capturedFlowEvents.add("didFailLoadingProducts")
        }

        override fun flowViewDidFinishWebPaymentNavigation(
            view: AdaptyUIFlowView,
            product: AdaptyPaywallProduct?,
            error: AdaptyError?
        ) {
            capturedFlowView = view
            capturedProduct = product
            capturedError = error
            capturedFlowEvents.add("didFinishWebPaymentNavigation")
        }

        override fun flowViewDidReceiveAnalyticEvent(
            view: AdaptyUIFlowView,
            name: String,
            paramsJsonString: String
        ) {
            capturedFlowView = view
            capturedAnalyticName = name
            capturedAnalyticParams = paramsJsonString
            capturedFlowEvents.add("didReceiveAnalyticEvent")
        }
    }

    private val onboardingObserver = object : AdaptyUIOnboardingsEventsObserver {

        override val mainUiScope: CoroutineScope get() = TestScope()

        override fun onboardingViewDidFinishLoading(
            view: AdaptyUIOnboardingView,
            meta: AdaptyUIOnboardingMeta
        ) {
            capturedOnboardingView = view
            capturedMeta = meta
            capturedOnboardingEvents.add("didFinishLoading")
        }

        override fun onboardingViewDidFailWithError(
            view: AdaptyUIOnboardingView,
            error: AdaptyError
        ) {
            capturedOnboardingView = view
            capturedOnboardingError = error
            capturedOnboardingEvents.add("didFailWithError")
        }

        override fun onboardingViewOnCloseAction(
            view: AdaptyUIOnboardingView,
            meta: AdaptyUIOnboardingMeta,
            actionId: String
        ) {
            capturedOnboardingView = view
            capturedMeta = meta
            capturedActionId = actionId
            capturedOnboardingEvents.add("onCloseAction")
        }

        override fun onboardingViewOnCustomAction(
            view: AdaptyUIOnboardingView,
            meta: AdaptyUIOnboardingMeta,
            actionId: String
        ) {
            capturedOnboardingView = view
            capturedMeta = meta
            capturedActionId = actionId
            capturedOnboardingEvents.add("onCustomAction")
        }

        override fun onboardingViewOnPaywallAction(
            view: AdaptyUIOnboardingView,
            meta: AdaptyUIOnboardingMeta,
            actionId: String
        ) {
            capturedOnboardingView = view
            capturedMeta = meta
            capturedActionId = actionId
            capturedOnboardingEvents.add("onPaywallAction")
        }

        override fun onboardingViewOnStateUpdatedAction(
            view: AdaptyUIOnboardingView,
            meta: AdaptyUIOnboardingMeta,
            elementId: String,
            params: AdaptyOnboardingsStateUpdatedParams
        ) {
            capturedOnboardingView = view
            capturedMeta = meta
            capturedElementId = elementId
            capturedStateParams = params
            capturedOnboardingEvents.add("onStateUpdatedAction")
        }

        override fun onboardingViewOnAnalyticsEvent(
            view: AdaptyUIOnboardingView,
            meta: AdaptyUIOnboardingMeta,
            event: AdaptyOnboardingsAnalyticsEvent
        ) {
            capturedOnboardingView = view
            capturedMeta = meta
            capturedEvent = event
            capturedOnboardingEvents.add("onAnalyticsEvent")
        }
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAdaptyPlugin = FakeAdaptyPlugin()

        capturedFlowEvents = mutableListOf()
        capturedFlowView = null
        capturedAction = null
        capturedProduct = null
        capturedProfile = null
        capturedError = null
        capturedPurchaseResult = null
        capturedProductId = null

        capturedOnboardingEvents = mutableListOf()
        capturedOnboardingView = null
        capturedMeta = null
        capturedOnboardingError = null
        capturedActionId = null
        capturedEvent = null
        capturedStateParams = null
        capturedElementId = null
        capturedPermission = null
        capturedPermissionView = null
        capturedAppReviewView = null
        capturedStartPurchase = null
        capturedFinishPurchase = null
        capturedStartRestore = null
        capturedFinishRestore = null
        capturedAnalyticName = null
        capturedAnalyticParams = null

        appMainScope = CoroutineScope(testDispatcher + SupervisorJob())
        adaptyUIImpl = AdaptyUIImpl(
            adaptyPlugin = fakeAdaptyPlugin,
            appMainScope = appMainScope,
            defaultDispatcher = testDispatcher,
            mainDispatcher = testDispatcher
        )
        adaptyUIImpl.setFlowsEventsObserver(flowObserver)
        adaptyUIImpl.setOnboardingsEventsObserver(onboardingObserver)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        appMainScope.cancel()
        Dispatchers.resetMain()
    }

    private val observerModeResolver = object : AdaptyUIObserverModeResolver {
        override fun observerModeDidInitiatePurchase(
            view: AdaptyUIFlowView,
            product: AdaptyPaywallProduct,
            onStartPurchase: () -> Unit,
            onFinishPurchase: () -> Unit
        ) {
            capturedFlowView = view
            capturedProduct = product
            capturedStartPurchase = onStartPurchase
            capturedFinishPurchase = onFinishPurchase
            capturedFlowEvents.add("observerDidInitiatePurchase")
        }

        override fun observerModeDidInitiateRestore(
            view: AdaptyUIFlowView,
            onStartRestore: () -> Unit,
            onFinishRestore: () -> Unit
        ) {
            capturedFlowView = view
            capturedStartRestore = onStartRestore
            capturedFinishRestore = onFinishRestore
            capturedFlowEvents.add("observerDidInitiateRestore")
        }
    }

    private fun fakeSystemRequestsHandler(result: AdaptyUIPermissionResult) =
        object : AdaptyUISystemRequestsHandler {
            override suspend fun handlePermission(
                view: AdaptyUIFlowView,
                permission: AdaptyUIPermission,
                customArgs: Map<String, String>?
            ): AdaptyUIPermissionResult {
                capturedPermissionView = view
                capturedPermission = permission
                return result
            }

            override suspend fun handleAppReviewRequest(view: AdaptyUIFlowView) {
                capturedAppReviewView = view
            }
        }

    private suspend fun sendEventAndWait(event: AdaptyPluginEvent, json: String) {
        AdaptyPluginEventHandler.onNewEvent(event.eventName, json)
        delay(200)
    }

    private fun assertFlowViewId(expectedViewId: String = AdaptyFakeTestData.EVENT_VIEW_ID) {
        assertNotNull(capturedFlowView)
        assertEquals(expectedViewId, capturedFlowView!!.id)
    }

    private fun assertOnboardingViewId(expectedViewId: String = AdaptyFakeTestData.EVENT_VIEW_ID) {
        assertNotNull(capturedOnboardingView)
        assertEquals(expectedViewId, capturedOnboardingView!!.id)
    }

    private fun assertMeta() {
        assertNotNull(capturedMeta)
        assertEquals(AdaptyFakeTestData.ONBOARDING_ID, capturedMeta!!.onboardingId)
        assertEquals(AdaptyFakeTestData.SCREEN_CLIENT_ID, capturedMeta!!.screenClientId)
        assertEquals(AdaptyFakeTestData.SCREEN_INDEX, capturedMeta!!.screenIndex)
        assertEquals(AdaptyFakeTestData.TOTAL_SCREENS, capturedMeta!!.screensTotal)
    }

    // =========================================================================
    // PAYWALL VIEW EVENT TESTS
    // =========================================================================

    @Test
    fun `flow view did perform action - close`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "close")
            )
        )

        assertTrue(capturedFlowEvents.contains("didPerformAction"))
        assertFlowViewId()
        assertIs<AdaptyUIAction.CloseAction>(capturedAction)
    }

    @Test
    fun `flow view did perform action - open url`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "open_url", "action_value" to "https://example.com", "action_open_in" to "browser_out_app")
            )
        )

        assertTrue(capturedFlowEvents.contains("didPerformAction"))
        assertFlowViewId()
        val action = capturedAction
        assertIs<AdaptyUIAction.OpenUrlAction>(action)
        assertEquals("https://example.com", action.url)
        assertEquals(AdaptyWebPresentation.EXTERNAL_BROWSER, action.openIn)
    }

    @Test
    fun `flow view did perform action - open url in app browser`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "open_url", "action_value" to "https://example.com", "action_open_in" to "browser_in_app")
            )
        )

        assertTrue(capturedFlowEvents.contains("didPerformAction"))
        assertFlowViewId()
        val action = capturedAction
        assertIs<AdaptyUIAction.OpenUrlAction>(action)
        assertEquals("https://example.com", action.url)
        assertEquals(AdaptyWebPresentation.IN_APP_BROWSER, action.openIn)
    }

    @Test
    fun `flow view did perform action - open url without open_in defaults to external browser`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "open_url", "action_value" to "https://example.com")
            )
        )

        assertTrue(capturedFlowEvents.contains("didPerformAction"))
        assertFlowViewId()
        val action = capturedAction
        assertIs<AdaptyUIAction.OpenUrlAction>(action)
        assertEquals("https://example.com", action.url)
        assertEquals(AdaptyWebPresentation.EXTERNAL_BROWSER, action.openIn)
    }

    @Test
    fun `flow view did perform action - custom`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "custom", "action_value" to "my_custom_action")
            )
        )

        assertTrue(capturedFlowEvents.contains("didPerformAction"))
        assertFlowViewId()
        val action = capturedAction
        assertIs<AdaptyUIAction.CustomAction>(action)
        assertEquals("my_custom_action", action.action)
    }

    @Test
    fun `flow view did appear`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_APPEAR,
            AdaptyPluginResponseTemplate.getEventJsonString(AdaptyPluginEvent.FLOW_VIEW_DID_APPEAR)
        )

        assertTrue(capturedFlowEvents.contains("didAppear"))
        assertFlowViewId()
    }

    @Test
    fun `flow view did disappear`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_DISAPPEAR,
            AdaptyPluginResponseTemplate.getEventJsonString(AdaptyPluginEvent.FLOW_VIEW_DID_DISAPPEAR)
        )

        assertTrue(capturedFlowEvents.contains("didDisappear"))
        assertFlowViewId()
    }

    @Test
    fun `flow view did select product`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_SELECT_PRODUCT,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_SELECT_PRODUCT
            )
        )

        assertTrue(capturedFlowEvents.contains("didSelectProduct"))
        assertFlowViewId()
        assertEquals(AdaptyFakeTestData.PRODUCT_ID, capturedProductId)
    }

    @Test
    fun `flow view did start purchase`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_START_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_START_PURCHASE
            )
        )

        assertTrue(capturedFlowEvents.contains("didStartPurchase"))
        assertFlowViewId()
        assertNotNull(capturedProduct)
        assertEquals(AdaptyFakeTestData.PRODUCT_ID, capturedProduct!!.vendorProductId)
    }

    @Test
    fun `flow view did finish purchase - success`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_PURCHASE,
                mapOf("purchase_type" to "success")
            )
        )

        assertTrue(capturedFlowEvents.contains("didFinishPurchase"))
        assertFlowViewId()
        assertNotNull(capturedProduct)
        assertIs<AdaptyPurchaseResult.Success>(capturedPurchaseResult)
    }

    @Test
    fun `flow view did finish purchase - cancelled`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_PURCHASE,
                mapOf("purchase_type" to "user_cancelled")
            )
        )

        assertTrue(capturedFlowEvents.contains("didFinishPurchase"))
        assertFlowViewId()
        assertIs<AdaptyPurchaseResult.UserCanceled>(capturedPurchaseResult)
    }

    @Test
    fun `flow view did fail purchase`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_PURCHASE
            )
        )

        assertTrue(capturedFlowEvents.contains("didFailPurchase"))
        assertFlowViewId()
        assertNotNull(capturedProduct)
        assertNotNull(capturedError)
        assertEquals("Test error message", capturedError!!.message)
    }

    @Test
    fun `flow view did start restore`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_START_RESTORE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_START_RESTORE
            )
        )

        assertTrue(capturedFlowEvents.contains("didStartRestore"))
        assertFlowViewId()
    }

    @Test
    fun `flow view did finish restore`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_RESTORE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_RESTORE
            )
        )

        assertTrue(capturedFlowEvents.contains("didFinishRestore"))
        assertFlowViewId()
        assertNotNull(capturedProfile)
        assertEquals("1", capturedProfile!!.profileId)
    }

    @Test
    fun `flow view did fail restore`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_RESTORE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_RESTORE
            )
        )

        assertTrue(capturedFlowEvents.contains("didFailRestore"))
        assertFlowViewId()
        assertNotNull(capturedError)
        assertEquals("Test error message", capturedError!!.message)
    }

    @Test
    fun `flow view did fail rendering`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_RECEIVE_ERROR,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_RECEIVE_ERROR
            )
        )

        assertTrue(capturedFlowEvents.contains("didFailRendering"))
        assertFlowViewId()
        assertNotNull(capturedError)
    }

    @Test
    fun `flow view did fail loading products`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_LOADING_PRODUCTS,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_LOADING_PRODUCTS
            )
        )

        assertTrue(capturedFlowEvents.contains("didFailLoadingProducts"))
        assertFlowViewId()
        assertNotNull(capturedError)
    }

    @Test
    fun `flow view did finish web payment navigation - with product and error`() =
        runTest(testDispatcher) {
            sendEventAndWait(
                AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                    mapOf("include_product" to true, "include_error" to true)
                )
            )

            assertTrue(capturedFlowEvents.contains("didFinishWebPaymentNavigation"))
            assertFlowViewId()
            assertNotNull(capturedProduct)
            assertNotNull(capturedError)
        }

    @Test
    fun `flow view did finish web payment navigation - no product no error`() =
        runTest(testDispatcher) {
            sendEventAndWait(
                AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                    mapOf("include_product" to false, "include_error" to false)
                )
            )

            assertTrue(capturedFlowEvents.contains("didFinishWebPaymentNavigation"))
            assertFlowViewId()
            assertNull(capturedProduct)
            assertNull(capturedError)
        }

    @Test
    fun `system requests handler receives permission and answers the plugin`() =
        runTest(testDispatcher) {
            fakeAdaptyPlugin.simulateSuccessResponse()
            adaptyUIImpl.setSystemRequestsHandler(
                fakeSystemRequestsHandler(AdaptyUIPermissionResult.granted("authorized"))
            )

            sendEventAndWait(
                AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
                    mapOf("permission" to "camera", "event_id" to "perm_event_42")
                )
            )

            assertEquals(AdaptyUIPermission.CAMERA, capturedPermission)
            assertEquals(AdaptyFakeTestData.EVENT_VIEW_ID, capturedPermissionView?.id)
            assertEquals(
                AdaptyPluginMethod.FLOW_VIEW_DID_ANSWER_PERMISSION.methodName,
                fakeAdaptyPlugin.capturedRequestMethodName
            )
            val request =
                jsonInstance.parseToJsonElement(fakeAdaptyPlugin.capturedRequestJsonString!!).jsonObject
            assertEquals("perm_event_42", request["event_id"]!!.jsonPrimitive.content)
            assertEquals("granted", request["status"]!!.jsonPrimitive.content)
            assertEquals("authorized", request["detail"]!!.jsonPrimitive.content)
        }

    @Test
    fun `system requests handler denial is sent to the plugin`() = runTest(testDispatcher) {
        fakeAdaptyPlugin.simulateSuccessResponse()
        adaptyUIImpl.setSystemRequestsHandler(
            fakeSystemRequestsHandler(AdaptyUIPermissionResult.denied("user declined"))
        )

        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
                mapOf("permission" to "push")
            )
        )

        val request =
            jsonInstance.parseToJsonElement(fakeAdaptyPlugin.capturedRequestJsonString!!).jsonObject
        assertEquals("denied", request["status"]!!.jsonPrimitive.content)
        assertEquals("user declined", request["detail"]!!.jsonPrimitive.content)
    }

    @Test
    fun `unknown permission id reaches the handler verbatim`() = runTest(testDispatcher) {
        fakeAdaptyPlugin.simulateSuccessResponse()
        adaptyUIImpl.setSystemRequestsHandler(
            fakeSystemRequestsHandler(AdaptyUIPermissionResult.granted())
        )

        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
                mapOf("permission" to "sms")
            )
        )

        assertEquals(AdaptyUIPermission("sms"), capturedPermission)
        assertEquals("sms", capturedPermission?.value)
    }

    @Test
    fun `permission with no registered handler sends no answer`() = runTest(testDispatcher) {
        fakeAdaptyPlugin.simulateSuccessResponse()

        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
                mapOf("permission" to "push")
            )
        )

        // Native keeps the request pending and resolves it as denied at teardown; the SDK must not
        // fabricate an answer.
        assertNull(fakeAdaptyPlugin.capturedRequestMethodName)
    }

    @Test
    fun `suspended permission handler does not stall other events`() = runTest(testDispatcher) {
        fakeAdaptyPlugin.simulateSuccessResponse()
        val permissionGate = CompletableDeferred<AdaptyUIPermissionResult>()
        adaptyUIImpl.setSystemRequestsHandler(object : AdaptyUISystemRequestsHandler {
            override suspend fun handlePermission(
                view: AdaptyUIFlowView,
                permission: AdaptyUIPermission,
                customArgs: Map<String, String>?
            ): AdaptyUIPermissionResult = permissionGate.await()
        })

        // Leaves the handler awaiting, as an OS prompt would.
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION,
                mapOf("permission" to "push")
            )
        )

        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_APPEAR,
            AdaptyPluginResponseTemplate.getEventJsonString(AdaptyPluginEvent.FLOW_VIEW_DID_APPEAR)
        )

        // One loop dispatches every view's events; it must not be blocked by a pending request.
        assertTrue(
            capturedFlowEvents.contains("didAppear"),
            "events must not queue behind a pending permission request"
        )

        permissionGate.complete(AdaptyUIPermissionResult.granted())
        delay(200)
        assertEquals(
            AdaptyPluginMethod.FLOW_VIEW_DID_ANSWER_PERMISSION.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )
    }

    @Test
    fun `system requests handler receives app review request`() = runTest(testDispatcher) {
        adaptyUIImpl.setSystemRequestsHandler(
            fakeSystemRequestsHandler(AdaptyUIPermissionResult.denied())
        )

        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_REQUEST_APP_REVIEW,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_REQUEST_APP_REVIEW
            )
        )

        assertEquals(AdaptyFakeTestData.EVENT_VIEW_ID, capturedAppReviewView?.id)
    }

    @Test
    fun `flow view observer did initiate purchase`() = runTest(testDispatcher) {
        fakeAdaptyPlugin.simulateSuccessResponse()
        adaptyUIImpl.setObserverModeResolver(observerModeResolver)
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_PURCHASE
            )
        )

        assertTrue(capturedFlowEvents.contains("observerDidInitiatePurchase"))
        assertFlowViewId()
        assertNotNull(capturedProduct)
        assertNotNull(capturedStartPurchase)

        capturedStartPurchase!!()
        delay(200)
        assertEquals(
            AdaptyPluginMethod.OBSERVER_PURCHASE_DID_START.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )

        capturedFinishPurchase!!()
        delay(200)
        assertEquals(
            AdaptyPluginMethod.OBSERVER_PURCHASE_DID_FINISH.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )
    }

    @Test
    fun `flow view observer did initiate restore`() = runTest(testDispatcher) {
        fakeAdaptyPlugin.simulateSuccessResponse()
        adaptyUIImpl.setObserverModeResolver(observerModeResolver)
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_RESTORE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_RESTORE
            )
        )

        assertTrue(capturedFlowEvents.contains("observerDidInitiateRestore"))
        assertFlowViewId()
        assertNotNull(capturedStartRestore)

        capturedStartRestore!!()
        delay(200)
        assertEquals(
            AdaptyPluginMethod.OBSERVER_RESTORE_DID_START.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )

        capturedFinishRestore!!()
        delay(200)
        assertEquals(
            AdaptyPluginMethod.OBSERVER_RESTORE_DID_FINISH.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )
    }

    @Test
    fun `observer-mode purchase with no registered resolver does nothing`() =
        runTest(testDispatcher) {
            fakeAdaptyPlugin.simulateSuccessResponse()

            sendEventAndWait(
                AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_PURCHASE,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_PURCHASE
                )
            )

            assertTrue(capturedFlowEvents.isEmpty())
            assertNull(fakeAdaptyPlugin.capturedRequestMethodName)
        }

    @Test
    fun `flow view did receive analytic event`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.FLOW_VIEW_DID_RECEIVE_ANALYTIC_EVENT,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.FLOW_VIEW_DID_RECEIVE_ANALYTIC_EVENT,
                mapOf("name" to "flow_shown")
            )
        )

        assertTrue(capturedFlowEvents.contains("didReceiveAnalyticEvent"))
        assertFlowViewId()
        assertEquals("flow_shown", capturedAnalyticName)
        assertNotNull(capturedAnalyticParams)
    }

    @Test
    fun `each native view of the same flow gets its own id`() {
        val flow = AdaptyFakeTestData.getFlow()

        val first = flow.createNativePlatformViewId()
        val second = flow.createNativePlatformViewId()

        // Two embeds of one flow must not share an id: the id keys the per-view observer map, so a
        // shared id would make the second view evict the first one's observer.
        assertNotEquals(first, second)
        assertTrue(first.startsWith("compose_native_flow_${flow.instanceIdentity}"))
        assertTrue(second.startsWith("compose_native_flow_${flow.instanceIdentity}"))
    }

    @Test
    fun `per-view observers for the same flow both receive their own events`() =
        runTest(testDispatcher) {
            val flow = AdaptyFakeTestData.getFlow()
            val firstViewId = flow.createNativePlatformViewId()
            val secondViewId = flow.createNativePlatformViewId()
            val firstEvents = mutableListOf<String>()
            val secondEvents = mutableListOf<String>()

            fun observer(sink: MutableList<String>) = object : AdaptyUIFlowsEventsObserver {
                override val mainUiScope: CoroutineScope get() = TestScope()
                override fun flowViewDidAppear(view: AdaptyUIFlowView) {
                    sink.add(view.id)
                }
            }
            adaptyUIImpl.registerFlowEventsListener(observer(firstEvents), firstViewId)
            adaptyUIImpl.registerFlowEventsListener(observer(secondEvents), secondViewId)

            sendEventAndWait(
                AdaptyPluginEvent.FLOW_VIEW_DID_APPEAR,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.FLOW_VIEW_DID_APPEAR,
                    mapOf("view_id" to secondViewId)
                )
            )

            // The event targets the second view only; registering it must not have evicted the first.
            assertEquals(listOf(secondViewId), secondEvents)
            assertTrue(firstEvents.isEmpty(), "first view's observer must not receive another view's event")

            adaptyUIImpl.unregisterFlowEventsListener(firstViewId)
            adaptyUIImpl.unregisterFlowEventsListener(secondViewId)
        }

    // =========================================================================
    // ONBOARDING VIEW EVENT TESTS
    // =========================================================================

    @Test
    fun `onboarding did finish loading`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_DID_FINISH_LOADING,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_DID_FINISH_LOADING
            )
        )

        assertTrue(capturedOnboardingEvents.contains("didFinishLoading"))
        assertOnboardingViewId()
        assertMeta()
    }

    @Test
    fun `onboarding did fail with error`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_DID_FAIL_WITH_ERROR,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_DID_FAIL_WITH_ERROR
            )
        )

        assertTrue(capturedOnboardingEvents.contains("didFailWithError"))
        assertOnboardingViewId()
        assertNotNull(capturedOnboardingError)
        assertEquals("Test error message", capturedOnboardingError!!.message)
    }

    @Test
    fun `onboarding on analytics action - screen presented`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION,
                mapOf("event_name" to "screen_presented")
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onAnalyticsEvent"))
        assertOnboardingViewId()
        assertMeta()
        assertIs<AdaptyOnboardingsAnalyticsEventScreenPresented>(capturedEvent)
    }

    @Test
    fun `onboarding on analytics action - onboarding started`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION,
                mapOf("event_name" to "onboarding_started")
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onAnalyticsEvent"))
        assertIs<AdaptyOnboardingsAnalyticsEventOnboardingStarted>(capturedEvent)
    }

    @Test
    fun `onboarding on analytics action - screen completed with elementId and reply`() =
        runTest(testDispatcher) {
            sendEventAndWait(
                AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION,
                    mapOf(
                        "event_name" to "screen_completed",
                        "element_id" to "element_001",
                        "reply" to "user_reply"
                    )
                )
            )

            assertTrue(capturedOnboardingEvents.contains("onAnalyticsEvent"))
            assertOnboardingViewId()
            assertMeta()
            val event = capturedEvent
            assertIs<AdaptyOnboardingsAnalyticsEventScreenCompleted>(event)
            assertEquals("element_001", event.elementId)
            assertEquals("user_reply", event.reply)
        }

    @Test
    fun `onboarding on close action`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_CLOSE_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_CLOSE_ACTION
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onCloseAction"))
        assertOnboardingViewId()
        assertMeta()
        assertEquals(AdaptyFakeTestData.ACTION_ID, capturedActionId)
    }

    @Test
    fun `onboarding on custom action`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_CUSTOM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_CUSTOM_ACTION
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onCustomAction"))
        assertOnboardingViewId()
        assertMeta()
        assertEquals(AdaptyFakeTestData.ACTION_ID, capturedActionId)
    }

    @Test
    fun `onboarding on paywall action`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_PAYWALL_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_PAYWALL_ACTION
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onPaywallAction"))
        assertOnboardingViewId()
        assertMeta()
        assertEquals(AdaptyFakeTestData.ACTION_ID, capturedActionId)
    }

    @Test
    fun `onboarding on state updated action - select`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
                mapOf("element_type" to "select", "element_id" to "select_001")
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onStateUpdatedAction"))
        assertOnboardingViewId()
        assertMeta()
        assertEquals("select_001", capturedElementId)
        val params = capturedStateParams
        assertIs<AdaptyOnboardingsSelectParams>(params)
        assertEquals("option_1", params.id)
        assertEquals("val_1", params.value)
        assertEquals("Option 1", params.label)
    }

    @Test
    fun `onboarding on state updated action - multi select`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
                mapOf("element_type" to "multi_select", "element_id" to "multi_select_001")
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onStateUpdatedAction"))
        assertOnboardingViewId()
        assertEquals("multi_select_001", capturedElementId)
        val params = capturedStateParams
        assertIs<AdaptyOnboardingsMultiSelectParams>(params)
        assertEquals(2, params.params.size)
        assertEquals("option_1", params.params[0].id)
        assertEquals("option_2", params.params[1].id)
    }

    @Test
    fun `onboarding on state updated action - input text`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
                mapOf("element_type" to "input", "element_id" to "input_001")
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onStateUpdatedAction"))
        assertOnboardingViewId()
        assertEquals("input_001", capturedElementId)
        val params = capturedStateParams
        assertIs<AdaptyOnboardingsInputParams>(params)
        val input = params.input
        assertIs<AdaptyOnboardingsTextInput>(input)
        assertEquals("Hello World", input.value)
    }

    @Test
    fun `onboarding on state updated action - date picker`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
                mapOf("element_type" to "date_picker", "element_id" to "date_001")
            )
        )

        assertTrue(capturedOnboardingEvents.contains("onStateUpdatedAction"))
        assertOnboardingViewId()
        assertEquals("date_001", capturedElementId)
        val params = capturedStateParams
        assertIs<AdaptyOnboardingsDatePickerParams>(params)
        assertEquals(15, params.day)
        assertEquals(6, params.month)
        assertEquals(1990, params.year)
    }

    @Test
    fun `diagnostic - verify state updated action JSON parsing`() {
        val json = AdaptyPluginResponseTemplate.getEventJsonString(
            AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION,
            mapOf("element_type" to "select", "element_id" to "select_001")
        )
        val result =
            jsonInstance.decodeFromString<AdaptyOnboardingViewEventOnStateUpdatedActionResponse>(
                json
            )
        assertEquals("select_001", result.action.elementId)
    }

    // =========================================================================
    // PROFILE & INSTALLATION EVENT TESTS
    // =========================================================================

    @Test
    fun `did load latest profile event - profile is parsed correctly`() {
        val response = AdaptyPluginResponseTemplate.getEventJsonString(
            AdaptyPluginEvent.DID_LOAD_LATEST_PROFILE
        ).decodeJsonString<AdaptyProfileUpdatedResponse>()
        assertNotNull(response, "Failed to parse profile event JSON")

        val profile = response.profile.asAdaptyProfile()
        assertEquals("1", profile.profileId)
        assertEquals("1", profile.customerUserId)
        assertEquals(false, profile.isTestUser)

        val accessLevel = profile.accessLevels["1"]
        assertNotNull(accessLevel)
        assertEquals(true, accessLevel.isActive)
        assertEquals("1", accessLevel.vendorProductId)
        assertEquals("google", accessLevel.store)

        assertEquals("test_value", profile.customAttributes["test_attribute"])
    }

    @Test
    fun `promoted purchase arriving before registration is replayed on registration`() = runTest {
        val adaptyImpl = AdaptyImpl(
            adaptyPlugin = fakeAdaptyPlugin,
            appMainScope = appMainScope
        )
        val received = mutableListOf<AdaptyPromotedProduct>()

        // Event arrives while no listener is registered: it must be held, not dropped.
        sendEventAndWait(
            AdaptyPluginEvent.DID_RECEIVE_PROMOTED_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.DID_RECEIVE_PROMOTED_PURCHASE
            )
        )
        // AdaptyPluginEventHandler decodes on Dispatchers.Default, i.e. real background work that
        // virtual time cannot drain. Wait for it for real, then let the collector (on the test
        // dispatcher) run, so the event is genuinely processed while no listener exists.
        testScheduler.advanceUntilIdle()
        withContext(Dispatchers.Default) { kotlinx.coroutines.delay(500) }
        testScheduler.advanceUntilIdle()
        assertTrue(received.isEmpty(), "Nothing should be delivered before registration")

        adaptyImpl.setOnPromotedPurchaseListener { product -> received.add(product) }
        testScheduler.advanceUntilIdle()

        assertEquals(1, received.size, "Held promoted purchase should be replayed on registration")
        assertEquals(AdaptyFakeTestData.PRODUCT_ID, received.single().vendorProductId)

        // Replayed once only: registering again must not re-deliver it.
        adaptyImpl.setOnPromotedPurchaseListener { product -> received.add(product) }
        testScheduler.advanceUntilIdle()
        assertEquals(1, received.size, "Held purchase should be delivered exactly once")
    }

    @Test
    fun `did receive promoted purchase event - product is parsed correctly`() {
        val response = AdaptyPluginResponseTemplate.getEventJsonString(
            AdaptyPluginEvent.DID_RECEIVE_PROMOTED_PURCHASE
        ).decodeJsonString<AdaptyDidReceivePromotedPurchaseResponse>()
        assertNotNull(response, "Failed to parse promoted purchase event JSON")

        val product = response.product.asAdaptyPromotedProduct()
        assertEquals(AdaptyFakeTestData.PRODUCT_ID, product.vendorProductId)
        assertEquals("Premium Monthly", product.localizedTitle)
        assertEquals(true, product.isFamilyShareable)
        assertEquals(9.99, product.price.amount)
        assertEquals("intro_offer", product.subscription?.offer?.offerIdentifier?.id)
    }

    @Test
    fun `on installation details success event - details are parsed correctly`() {
        val response = AdaptyPluginResponseTemplate.getEventJsonString(
            AdaptyPluginEvent.ON_INSTALLATION_DETAILS_SUCCESS
        ).decodeJsonString<AdaptyOnInstallationDetailsSuccessEventResponse>()
        assertNotNull(response, "Failed to parse installation details success event JSON")

        val details = response.details.asAdaptyInstallationDetails()
        assertEquals("install_abc123", details.installId)
        assertEquals(5, details.appLaunchCount)
    }

    @Test
    fun `on installation details fail event - error is parsed correctly`() {
        val response = AdaptyPluginResponseTemplate.getEventJsonString(
            AdaptyPluginEvent.ON_INSTALLATION_DETAILS_FAIL
        ).decodeJsonString<AdaptyOnInstallationDetailsFailEventResponse>()
        assertNotNull(response, "Failed to parse installation details fail event JSON")

        val error = response.error.asAdaptyError()
        assertEquals("Test error message", error.message)
    }
}
