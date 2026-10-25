import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyPreloadFlowsForDefaultAudienceRequest
import com.adapty.kmp.internal.plugin.request.AdaptyPreloadFlowsRequest
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.fold
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.fail
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class AdaptyPreloadFlowsTest {

    private lateinit var fakeAdaptyPlugin: FakeAdaptyPlugin
    private lateinit var adaptyImpl: AdaptyImpl

    private val placementIds = listOf(
        AdaptyFakeTestData.PLACEMENT_ID,
        AdaptyFakeTestData.SECOND_PLACEMENT_ID
    )

    @BeforeTest
    fun setUp() {
        fakeAdaptyPlugin = FakeAdaptyPlugin()
        adaptyImpl = AdaptyImpl(
            adaptyPlugin = fakeAdaptyPlugin,
            appMainScope = TestScope()
        )
    }

    @Test
    fun `preloadFlows method - default load timeout - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.preloadFlows(placementIds = placementIds) },
            method = AdaptyPluginMethod.PRELOAD_FLOWS,
            param = AdaptyPreloadFlowsRequest(
                placementIds = placementIds,
                loadTimeoutInSeconds = 5.0
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `preloadFlows method - custom load timeout - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.preloadFlows(
                    placementIds = placementIds,
                    loadTimeout = 30.seconds
                )
            },
            method = AdaptyPluginMethod.PRELOAD_FLOWS,
            param = AdaptyPreloadFlowsRequest(
                placementIds = placementIds,
                loadTimeoutInSeconds = 30.0
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `preloadFlows method - sub-second load timeout is sent as fractional seconds`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                adaptyImpl.preloadFlows(
                    placementIds = listOf(AdaptyFakeTestData.PLACEMENT_ID),
                    loadTimeout = 1500.milliseconds
                )
            },
            method = AdaptyPluginMethod.PRELOAD_FLOWS,
            param = AdaptyPreloadFlowsRequest(
                placementIds = listOf(AdaptyFakeTestData.PLACEMENT_ID),
                loadTimeoutInSeconds = 1.5
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `preloadFlows method - empty placement list is forwarded to native`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.preloadFlows(placementIds = emptyList()) },
            method = AdaptyPluginMethod.PRELOAD_FLOWS,
            param = AdaptyPreloadFlowsRequest(
                placementIds = emptyList(),
                loadTimeoutInSeconds = 5.0
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `preloadFlows request - without load timeout omits load_timeout`() {
        val request = AdaptyPreloadFlowsRequest(placementIds = placementIds)

        val actualJson = jsonInstance.encodeToJsonElement(AdaptyPreloadFlowsRequest.serializer(), request)
        val expectedJson = jsonInstance.parseToJsonElement(
            AdaptyPluginRequestTemplate.getJsonString(
                method = AdaptyPluginMethod.PRELOAD_FLOWS,
                param = request
            )
        )

        assertEquals(expectedJson, actualJson)
        assertFalse(actualJson.jsonObject.containsKey("load_timeout"))
    }

    @Test
    fun `preloadFlowsForDefaultAudience method - verify request and response`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { adaptyImpl.preloadFlowsForDefaultAudience(placementIds = placementIds) },
            method = AdaptyPluginMethod.PRELOAD_FLOWS_FOR_DEFAULT_AUDIENCE,
            param = AdaptyPreloadFlowsForDefaultAudienceRequest(placementIds = placementIds),
            expectedSuccessData = Unit
        )

        // The default-audience variant carries no timeout.
        val sentJson = jsonInstance.parseToJsonElement(
            fakeAdaptyPlugin.capturedRequestJsonString.orEmpty()
        ).jsonObject
        assertFalse(sentJson.containsKey("load_timeout"))
    }

    @Test
    fun `preloadFlows method - aggregated native error is mapped to AdaptyResult Error`() = runTest {
        val aggregatedError = AdaptyError(
            code = AdaptyErrorCode.NETWORK_FAILED,
            message = "Network failed",
            detail = "Failed to preload one or more placements"
        )
        fakeAdaptyPlugin.simulateErrorResponse(aggregatedError)

        adaptyImpl.preloadFlows(placementIds = placementIds).fold(
            onSuccess = { fail("Expected error but got success") },
            onError = { error -> assertEquals(aggregatedError, error) }
        )
    }

    @Test
    fun `preloadFlowsForDefaultAudience method - aggregated native error is mapped to AdaptyResult Error`() =
        runTest {
            val aggregatedError = AdaptyError(
                code = AdaptyErrorCode.NETWORK_FAILED,
                message = "Network failed",
                detail = "Failed to preload one or more placements"
            )
            fakeAdaptyPlugin.simulateErrorResponse(aggregatedError)

            adaptyImpl.preloadFlowsForDefaultAudience(placementIds = placementIds).fold(
                onSuccess = { fail("Expected error but got success") },
                onError = { error -> assertEquals(aggregatedError, error) }
            )
        }
}
