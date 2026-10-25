@file:Suppress("DEPRECATION") // dismissOnboardingView is deprecated

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyUIDestroyViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDismissViewRequest
import com.adapty.kmp.internal.utils.jsonInstance
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Flow view `destroy` support (cross_platform 4.3.1): `adapty_ui_dismiss_flow_view` always carries
 * `destroy` (default `true`), `adapty_ui_destroy_flow_view` carries only `id`, and onboarding
 * dismiss keeps sending `destroy = false`.
 */
class AdaptyFlowViewDestroyTest {

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

    private fun assertCapturedRequest(method: AdaptyPluginMethod, expectedJson: String) {
        assertEquals(method.methodName, fakeAdaptyPlugin.capturedRequestMethodName)
        assertEquals(
            jsonInstance.parseToJsonElement(expectedJson).jsonObject,
            jsonInstance.parseToJsonElement(fakeAdaptyPlugin.capturedRequestJsonString.orEmpty()).jsonObject
        )
    }

    @Test
    fun `dismissFlowView - default sends destroy true`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyUIImpl.dismissFlowView(view = view) },
            method = AdaptyPluginMethod.DISMISS_FLOW_VIEW,
            param = AdaptyUIDismissViewRequest(id = view.id, destroy = true),
            expectedSuccessData = Unit
        )

        assertCapturedRequest(
            method = AdaptyPluginMethod.DISMISS_FLOW_VIEW,
            expectedJson = buildJsonObject {
                put("id", view.id)
                put("destroy", true)
            }.toString()
        )
    }

    @Test
    fun `dismissFlowView - destroy false is sent explicitly`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyUIImpl.dismissFlowView(view = view, destroy = false) },
            method = AdaptyPluginMethod.DISMISS_FLOW_VIEW,
            param = AdaptyUIDismissViewRequest(id = view.id, destroy = false),
            expectedSuccessData = Unit
        )

        // `destroy` is required by the contract, so `false` must be serialized, not omitted.
        assertCapturedRequest(
            method = AdaptyPluginMethod.DISMISS_FLOW_VIEW,
            expectedJson = buildJsonObject {
                put("id", view.id)
                put("destroy", false)
            }.toString()
        )
    }

    @Test
    fun `destroyFlowView - sends adapty_ui_destroy_flow_view with only id`() = runTest {
        val view = AdaptyFakeTestData.getUIFlowView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyUIImpl.destroyFlowView(view = view) },
            method = AdaptyPluginMethod.DESTROY_FLOW_VIEW,
            param = AdaptyUIDestroyViewRequest(id = view.id),
            expectedSuccessData = Unit
        )

        assertEquals("adapty_ui_destroy_flow_view", fakeAdaptyPlugin.capturedRequestMethodName)
        assertCapturedRequest(
            method = AdaptyPluginMethod.DESTROY_FLOW_VIEW,
            expectedJson = buildJsonObject {
                put("id", view.id)
            }.toString()
        )
    }

    // The default close / error handlers release only presented views (destroy = isStandaloneView):
    // embedded platform views are owned by their host and must keep being dismissed with destroy = false.
    @OptIn(AdaptyKMPInternal::class)
    @Test
    fun `isStandaloneView - presented views true, embedded platform views false`() {
        val presented = AdaptyFakeTestData.getUIFlowView()
        val embedded = presented.copy(id = AdaptyFakeTestData.getFlow().createNativePlatformViewId())

        assertTrue(presented.isStandaloneView)
        assertFalse(embedded.isStandaloneView)
    }

    @Test
    fun `dismissOnboardingView - unchanged, still sends destroy false`() = runTest {
        val view = AdaptyFakeTestData.getUIOnboardingView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyUIImpl.dismissOnboardingView(view = view) },
            method = AdaptyPluginMethod.DISMISS_ONBOARDING_VIEW,
            param = AdaptyUIDismissViewRequest(id = view.id, destroy = false),
            expectedSuccessData = Unit
        )

        assertCapturedRequest(
            method = AdaptyPluginMethod.DISMISS_ONBOARDING_VIEW,
            expectedJson = buildJsonObject {
                put("id", view.id)
                put("destroy", false)
            }.toString()
        )
    }
}
