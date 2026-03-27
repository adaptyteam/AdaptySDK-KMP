import com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
import com.adapty.kmp.AdaptyUIPaywallsEventsObserver
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.response.AdaptyOnInstallationDetailsFailEventResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnInstallationDetailsSuccessEventResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnStateUpdatedActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyProfileUpdatedResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyError
import com.adapty.kmp.internal.plugin.response.asAdaptyInstallationDetails
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.internal.utils.jsonInstance
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
import com.adapty.kmp.models.AdaptyUIOnboardingMeta
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPaywallView
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
    private lateinit var capturedPaywallEvents: MutableList<String>
    private var capturedPaywallView: AdaptyUIPaywallView? = null
    private var capturedAction: AdaptyUIAction? = null
    private var capturedProduct: AdaptyPaywallProduct? = null
    private var capturedProfile: AdaptyProfile? = null
    private var capturedError: AdaptyError? = null
    private var capturedPurchaseResult: AdaptyPurchaseResult? = null
    private var capturedProductId: String? = null

    // Onboarding captured state
    private lateinit var capturedOnboardingEvents: MutableList<String>
    private var capturedOnboardingView: AdaptyUIOnboardingView? = null
    private var capturedMeta: AdaptyUIOnboardingMeta? = null
    private var capturedOnboardingError: AdaptyError? = null
    private var capturedActionId: String? = null
    private var capturedEvent: AdaptyOnboardingsAnalyticsEvent? = null
    private var capturedStateParams: AdaptyOnboardingsStateUpdatedParams? = null
    private var capturedElementId: String? = null

    private val paywallObserver = object : AdaptyUIPaywallsEventsObserver {

        override val mainUiScope: CoroutineScope get() = TestScope()

        override fun paywallViewDidPerformAction(
            view: AdaptyUIPaywallView,
            action: AdaptyUIAction
        ) {
            capturedPaywallView = view
            capturedAction = action
            capturedPaywallEvents.add("didPerformAction")
        }

        override fun paywallViewDidAppear(view: AdaptyUIPaywallView) {
            capturedPaywallView = view
            capturedPaywallEvents.add("didAppear")
        }

        override fun paywallViewDidDisappear(view: AdaptyUIPaywallView) {
            capturedPaywallView = view
            capturedPaywallEvents.add("didDisappear")
        }

        override fun paywallViewDidSelectProduct(view: AdaptyUIPaywallView, productId: String) {
            capturedPaywallView = view
            capturedProductId = productId
            capturedPaywallEvents.add("didSelectProduct")
        }

        override fun paywallViewDidStartPurchase(
            view: AdaptyUIPaywallView,
            product: AdaptyPaywallProduct
        ) {
            capturedPaywallView = view
            capturedProduct = product
            capturedPaywallEvents.add("didStartPurchase")
        }

        override fun paywallViewDidFinishPurchase(
            view: AdaptyUIPaywallView,
            product: AdaptyPaywallProduct,
            purchaseResult: AdaptyPurchaseResult
        ) {
            capturedPaywallView = view
            capturedProduct = product
            capturedPurchaseResult = purchaseResult
            capturedPaywallEvents.add("didFinishPurchase")
        }

        override fun paywallViewDidFailPurchase(
            view: AdaptyUIPaywallView,
            product: AdaptyPaywallProduct,
            error: AdaptyError
        ) {
            capturedPaywallView = view
            capturedProduct = product
            capturedError = error
            capturedPaywallEvents.add("didFailPurchase")
        }

        override fun paywallViewDidStartRestore(view: AdaptyUIPaywallView) {
            capturedPaywallView = view
            capturedPaywallEvents.add("didStartRestore")
        }

        override fun paywallViewDidFinishRestore(
            view: AdaptyUIPaywallView,
            profile: AdaptyProfile
        ) {
            capturedPaywallView = view
            capturedProfile = profile
            capturedPaywallEvents.add("didFinishRestore")
        }

        override fun paywallViewDidFailRestore(view: AdaptyUIPaywallView, error: AdaptyError) {
            capturedPaywallView = view
            capturedError = error
            capturedPaywallEvents.add("didFailRestore")
        }

        override fun paywallViewDidFailRendering(view: AdaptyUIPaywallView, error: AdaptyError) {
            capturedPaywallView = view
            capturedError = error
            capturedPaywallEvents.add("didFailRendering")
        }

        override fun paywallViewDidFailLoadingProducts(
            view: AdaptyUIPaywallView,
            error: AdaptyError
        ) {
            capturedPaywallView = view
            capturedError = error
            capturedPaywallEvents.add("didFailLoadingProducts")
        }

        override fun paywallViewDidFinishWebPaymentNavigation(
            view: AdaptyUIPaywallView,
            product: AdaptyPaywallProduct?,
            error: AdaptyError?
        ) {
            capturedPaywallView = view
            capturedProduct = product
            capturedError = error
            capturedPaywallEvents.add("didFinishWebPaymentNavigation")
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

        capturedPaywallEvents = mutableListOf()
        capturedPaywallView = null
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

        appMainScope = CoroutineScope(testDispatcher + SupervisorJob())
        adaptyUIImpl = AdaptyUIImpl(
            adaptyPlugin = fakeAdaptyPlugin,
            appMainScope = appMainScope,
            defaultDispatcher = testDispatcher,
            mainDispatcher = testDispatcher
        )
        adaptyUIImpl.setPaywallsEventsObserver(paywallObserver)
        adaptyUIImpl.setOnboardingsEventsObserver(onboardingObserver)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        appMainScope.cancel()
        Dispatchers.resetMain()
    }

    private suspend fun sendEventAndWait(event: AdaptyPluginEvent, json: String) {
        AdaptyPluginEventHandler.onNewEvent(event.eventName, json)
        delay(200)
    }

    private fun assertPaywallViewId(expectedViewId: String = AdaptyFakeTestData.EVENT_VIEW_ID) {
        assertNotNull(capturedPaywallView)
        assertEquals(expectedViewId, capturedPaywallView!!.id)
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
    fun `paywall view did perform action - close`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "close")
            )
        )

        assertTrue(capturedPaywallEvents.contains("didPerformAction"))
        assertPaywallViewId()
        assertIs<AdaptyUIAction.CloseAction>(capturedAction)
    }

    @Test
    fun `paywall view did perform action - open url`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "open_url", "action_value" to "https://example.com")
            )
        )

        assertTrue(capturedPaywallEvents.contains("didPerformAction"))
        assertPaywallViewId()
        val action = capturedAction
        assertIs<AdaptyUIAction.OpenUrlAction>(action)
        assertEquals("https://example.com", action.url)
    }

    @Test
    fun `paywall view did perform action - custom`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION,
                mapOf("action_type" to "custom", "action_value" to "my_custom_action")
            )
        )

        assertTrue(capturedPaywallEvents.contains("didPerformAction"))
        assertPaywallViewId()
        val action = capturedAction
        assertIs<AdaptyUIAction.CustomAction>(action)
        assertEquals("my_custom_action", action.action)
    }

    @Test
    fun `paywall view did appear`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_APPEAR,
            AdaptyPluginResponseTemplate.getEventJsonString(AdaptyPluginEvent.PAYWALL_VIEW_DID_APPEAR)
        )

        assertTrue(capturedPaywallEvents.contains("didAppear"))
        assertPaywallViewId()
    }

    @Test
    fun `paywall view did disappear`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_DISAPPEAR,
            AdaptyPluginResponseTemplate.getEventJsonString(AdaptyPluginEvent.PAYWALL_VIEW_DID_DISAPPEAR)
        )

        assertTrue(capturedPaywallEvents.contains("didDisappear"))
        assertPaywallViewId()
    }

    @Test
    fun `paywall view did perform system back action`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_SYSTEM_BACK_ACTION,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_SYSTEM_BACK_ACTION
            )
        )

        assertTrue(capturedPaywallEvents.contains("didPerformAction"))
        assertPaywallViewId()
        assertIs<AdaptyUIAction.AndroidSystemBackAction>(capturedAction)
    }

    @Test
    fun `paywall view did select product`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_SELECT_PRODUCT,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_SELECT_PRODUCT
            )
        )

        assertTrue(capturedPaywallEvents.contains("didSelectProduct"))
        assertPaywallViewId()
        assertEquals(AdaptyFakeTestData.PRODUCT_ID, capturedProductId)
    }

    @Test
    fun `paywall view did start purchase`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_START_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_START_PURCHASE
            )
        )

        assertTrue(capturedPaywallEvents.contains("didStartPurchase"))
        assertPaywallViewId()
        assertNotNull(capturedProduct)
        assertEquals(AdaptyFakeTestData.PRODUCT_ID, capturedProduct!!.vendorProductId)
    }

    @Test
    fun `paywall view did finish purchase - success`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_PURCHASE,
                mapOf("purchase_type" to "success")
            )
        )

        assertTrue(capturedPaywallEvents.contains("didFinishPurchase"))
        assertPaywallViewId()
        assertNotNull(capturedProduct)
        assertIs<AdaptyPurchaseResult.Success>(capturedPurchaseResult)
    }

    @Test
    fun `paywall view did finish purchase - cancelled`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_PURCHASE,
                mapOf("purchase_type" to "user_cancelled")
            )
        )

        assertTrue(capturedPaywallEvents.contains("didFinishPurchase"))
        assertPaywallViewId()
        assertIs<AdaptyPurchaseResult.UserCanceled>(capturedPurchaseResult)
    }

    @Test
    fun `paywall view did fail purchase`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_PURCHASE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_PURCHASE
            )
        )

        assertTrue(capturedPaywallEvents.contains("didFailPurchase"))
        assertPaywallViewId()
        assertNotNull(capturedProduct)
        assertNotNull(capturedError)
        assertEquals("Test error message", capturedError!!.message)
    }

    @Test
    fun `paywall view did start restore`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_START_RESTORE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_START_RESTORE
            )
        )

        assertTrue(capturedPaywallEvents.contains("didStartRestore"))
        assertPaywallViewId()
    }

    @Test
    fun `paywall view did finish restore`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_RESTORE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_RESTORE
            )
        )

        assertTrue(capturedPaywallEvents.contains("didFinishRestore"))
        assertPaywallViewId()
        assertNotNull(capturedProfile)
        assertEquals("1", capturedProfile!!.profileId)
    }

    @Test
    fun `paywall view did fail restore`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RESTORE,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RESTORE
            )
        )

        assertTrue(capturedPaywallEvents.contains("didFailRestore"))
        assertPaywallViewId()
        assertNotNull(capturedError)
        assertEquals("Test error message", capturedError!!.message)
    }

    @Test
    fun `paywall view did fail rendering`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RENDERING,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RENDERING
            )
        )

        assertTrue(capturedPaywallEvents.contains("didFailRendering"))
        assertPaywallViewId()
        assertNotNull(capturedError)
    }

    @Test
    fun `paywall view did fail loading products`() = runTest(testDispatcher) {
        sendEventAndWait(
            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_LOADING_PRODUCTS,
            AdaptyPluginResponseTemplate.getEventJsonString(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_LOADING_PRODUCTS
            )
        )

        assertTrue(capturedPaywallEvents.contains("didFailLoadingProducts"))
        assertPaywallViewId()
        assertNotNull(capturedError)
    }

    @Test
    fun `paywall view did finish web payment navigation - with product and error`() =
        runTest(testDispatcher) {
            sendEventAndWait(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                    mapOf("include_product" to true, "include_error" to true)
                )
            )

            assertTrue(capturedPaywallEvents.contains("didFinishWebPaymentNavigation"))
            assertPaywallViewId()
            assertNotNull(capturedProduct)
            assertNotNull(capturedError)
        }

    @Test
    fun `paywall view did finish web payment navigation - no product no error`() =
        runTest(testDispatcher) {
            sendEventAndWait(
                AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                AdaptyPluginResponseTemplate.getEventJsonString(
                    AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION,
                    mapOf("include_product" to false, "include_error" to false)
                )
            )

            assertTrue(capturedPaywallEvents.contains("didFinishWebPaymentNavigation"))
            assertPaywallViewId()
            assertNull(capturedProduct)
            assertNull(capturedError)
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
