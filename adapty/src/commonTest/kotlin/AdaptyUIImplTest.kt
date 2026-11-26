import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateOnboardingViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateFlowViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDestroyViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDismissViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIIOSPresentationStyleRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIPresentViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIShowDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPresentationRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyOnboardingRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyFlowRequest
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyResult
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class AdaptyUIImplTest {

    private lateinit var fakeAdaptyPlugin: FakeAdaptyPlugin
    private lateinit var adaptyUIImpl: AdaptyUIImpl

    @BeforeTest
    fun setUp() {
        fakeAdaptyPlugin = FakeAdaptyPlugin()
        val testDispatcher = StandardTestDispatcher()
        adaptyUIImpl = AdaptyUIImpl(
            adaptyPlugin = fakeAdaptyPlugin,
            appMainScope = TestScope(),
            defaultDispatcher = testDispatcher,
            mainDispatcher = testDispatcher
        )
    }


    @Test
    fun `createFlowView method - verify request and response`() = runTest {
        val flow = AdaptyFakeTestData.getFlow()
        val expectedView = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.createFlowView(flow = flow, locale = AdaptyFakeTestData.LOCALE)
            },
            method = AdaptyPluginMethod.CREATE_FLOW_VIEW,
            param = AdaptyUICreateFlowViewRequest(
                flow = flow.asAdaptyFlowRequest(),
                locale = AdaptyFakeTestData.LOCALE,
                loadTimeOutInSeconds = null,
                preloadProducts = false
            ),
            expectedSuccessData = expectedView
        )
    }

    @Test
    fun `presentFlowView method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.presentFlowView(
                    view = view,
                    iosPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
                )
            },
            method = AdaptyPluginMethod.PRESENT_FLOW_VIEW,
            param = AdaptyUIPresentViewRequest(
                id = view.id,
                iosPresentationStyle = AdaptyUIIOSPresentationStyleRequest.FULLSCREEN
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `dismissFlowView method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.dismissFlowView(view = view)
            },
            method = AdaptyPluginMethod.DISMISS_FLOW_VIEW,
            param = AdaptyUIDismissViewRequest(id = view.id, destroy = true),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `dismissFlowView method - destroy false - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.dismissFlowView(view = view, destroy = false)
            },
            method = AdaptyPluginMethod.DISMISS_FLOW_VIEW,
            param = AdaptyUIDismissViewRequest(id = view.id, destroy = false),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `sendAppMessage method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(AdaptyPluginMethod.SEND_APP_MESSAGE, Unit)

        val result = adaptyUIImpl.sendAppMessage(
            view = view,
            payload = mapOf(
                "action" to "confirmed",
                "count" to 2,
                "price" to 9.99,
                "trial" to false,
                "note" to null,
                "tags" to listOf("a", 1, null),
                "user" to mapOf("id" to "u1"),
            )
        )

        assertIs<AdaptyResult.Success<Unit>>(result)
        assertEquals(AdaptyPluginMethod.SEND_APP_MESSAGE.methodName, fakeAdaptyPlugin.capturedRequestMethodName)
        val request = fakeAdaptyPlugin.capturedRequestJson
        assertEquals(setOf("id", "message_id", "payload"), request.keys - "method")
        assertEquals(view.id, request.getValue("id").jsonPrimitive.content)
        assertTrue(request.getValue("message_id").jsonPrimitive.content.isNotBlank())
        assertEquals(
            buildJsonObject {
                put("action", "confirmed")
                put("count", 2)
                put("price", 9.99)
                put("trial", false)
                put("note", JsonNull)
                put("tags", buildJsonArray { add("a"); add(1); add(JsonNull) })
                put("user", buildJsonObject { put("id", "u1") })
            },
            request.getValue("payload")
        )
    }

    @Test
    fun `sendAppMessage method - generates a new message id for each call`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(AdaptyPluginMethod.SEND_APP_MESSAGE, Unit)

        adaptyUIImpl.sendAppMessage(view = view, payload = mapOf("n" to 1))
        val firstId = fakeAdaptyPlugin.capturedRequestJson.getValue("message_id").jsonPrimitive.content
        adaptyUIImpl.sendAppMessage(view = view, payload = mapOf("n" to 2))
        val secondId = fakeAdaptyPlugin.capturedRequestJson.getValue("message_id").jsonPrimitive.content

        assertNotEquals(firstId, secondId)
    }

    @Test
    fun `sendAppMessage method - payload JSON can't hold - fails without sending`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        val unsupportedType = adaptyUIImpl.sendAppMessage(view = view, payload = mapOf("view" to view))
        val notFinite = adaptyUIImpl.sendAppMessage(view = view, payload = mapOf("n" to Double.NaN))

        assertIs<AdaptyResult.Error>(unsupportedType)
        assertEquals(AdaptyErrorCode.WRONG_PARAMETER, unsupportedType.error.code)
        assertIs<AdaptyResult.Error>(notFinite)
        assertEquals(AdaptyErrorCode.WRONG_PARAMETER, notFinite.error.code)
        assertNull(fakeAdaptyPlugin.capturedRequestMethodName)
    }

    @Test
    fun `destroyFlowView method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.destroyFlowView(view = view)
            },
            method = AdaptyPluginMethod.DESTROY_FLOW_VIEW,
            param = AdaptyUIDestroyViewRequest(id = view.id),
            expectedSuccessData = Unit
        )
    }

    @OptIn(AdaptyKMPInternal::class)
    @Test
    fun `isStandaloneView - presented flow view true, embedded platform view false`() {
        val presented = AdaptyFakeTestData.getUIFlowView()
        val embedded = presented.copy(id = AdaptyFakeTestData.getFlow().createNativePlatformViewId())

        assertTrue(presented.isStandaloneView)
        assertFalse(embedded.isStandaloneView)
    }

    @Test
    fun `showDialog method - verify request and response`() = runTest {
        val viewId = AdaptyFakeTestData.VIEW_ID
        val expectedActionType = AdaptyFakeTestData.getDialogActionType()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.showDialog(
                    viewId = viewId,
                    title = "Test Title",
                    content = "Test Content",
                    primaryActionTitle = "OK",
                    secondaryActionTitle = "Cancel"
                )
            },
            method = AdaptyPluginMethod.SHOW_DIALOG,
            param = AdaptyUIShowDialogRequest(
                id = viewId,
                configuration = AdaptyUIDialogRequest(
                    title = "Test Title",
                    content = "Test Content",
                    defaultActionTitle = "OK",
                    secondaryActionTitle = "Cancel"
                )
            ),
            expectedSuccessData = expectedActionType
        )
    }

    @Test
    fun `createOnboardingView method - verify request and response`() = runTest {
        val onboarding = AdaptyFakeTestData.getOnboarding()
        val expectedView = AdaptyFakeTestData.getUIOnboardingView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.createOnboardingView(
                    onboarding = onboarding,
                    externalUrlsPresentation = AdaptyWebPresentation.IN_APP_BROWSER
                )
            },
            method = AdaptyPluginMethod.CREATE_ONBOARDING_VIEW,
            param = AdaptyUICreateOnboardingViewRequest(
                onboarding = onboarding.asAdaptyOnboardingRequest(),
                externalUrlsPresentation = AdaptyWebPresentationRequest.IN_APP_BROWSER
            ),
            expectedSuccessData = expectedView
        )
    }

    @Test
    fun `presentOnboardingView method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIOnboardingView()
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.presentOnboardingView(
                    view = view,
                    iosPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
                )
            },
            method = AdaptyPluginMethod.PRESENT_ONBOARDING_VIEW,
            param = AdaptyUIPresentViewRequest(
                id = view.id,
                iosPresentationStyle = AdaptyUIIOSPresentationStyleRequest.FULLSCREEN
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `dismissOnboardingView method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIOnboardingView()
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.dismissOnboardingView(view = view)
            },
            method = AdaptyPluginMethod.DISMISS_ONBOARDING_VIEW,
            param = AdaptyUIDismissViewRequest(id = view.id, destroy = false),
            expectedSuccessData = Unit
        )
    }
}
