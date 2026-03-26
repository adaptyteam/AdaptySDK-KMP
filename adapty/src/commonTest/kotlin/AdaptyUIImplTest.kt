import com.adapty.kmp.internal.AdaptyUIImpl
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateOnboardingViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUICreatePaywallViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDismissViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIIOSPresentationStyleRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIPresentViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIShowDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyWebPresentationRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyOnboardingRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallRequest
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyWebPresentation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test


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
    fun `createPaywallView method - verify request and response`() = runTest {
        val paywall = AdaptyFakeTestData.getPaywall()
        val expectedView = AdaptyFakeTestData.getUIPaywallView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.createPaywallView(paywall = paywall)
            },
            method = AdaptyPluginMethod.CREATE_PAYWALL_VIEW,
            param = AdaptyUICreatePaywallViewRequest(
                paywall = paywall.asAdaptyPaywallRequest(),
                loadTimeOutInSeconds = null,
                preloadProducts = false
            ),
            expectedSuccessData = expectedView
        )
    }

    @Test
    fun `presentPaywallView method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIPaywallView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.presentPaywallView(
                    view = view,
                    iosPresentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN
                )
            },
            method = AdaptyPluginMethod.PRESENT_PAYWALL_VIEW,
            param = AdaptyUIPresentViewRequest(
                id = view.id,
                iosPresentationStyle = AdaptyUIIOSPresentationStyleRequest.FULLSCREEN
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `dismissPaywallView method - verify request and response`() = runTest {
        val view = AdaptyFakeTestData.getUIPaywallView()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyUIImpl.dismissPaywallView(view = view)
            },
            method = AdaptyPluginMethod.DISMISS_PAYWALL_VIEW,
            param = AdaptyUIDismissViewRequest(id = view.id),
            expectedSuccessData = Unit
        )
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
            param = AdaptyUIDismissViewRequest(id = view.id),
            expectedSuccessData = Unit
        )
    }
}
