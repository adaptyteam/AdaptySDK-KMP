import com.adapty.kmp.internal.AdaptyImpl
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyShowStoreMessagesRequest
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyLogLevel
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyStoreMessageType
import com.adapty.kmp.models.AdaptyStoreMessagesHandling
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Store messages API (cross_platform 4.3.1): `store_messages_handling` activation option,
 * `get_pending_store_message_types` and `show_store_messages`.
 *
 * The platform branch is injected into [AdaptyImpl] so both the iOS path (native call) and the
 * Android path (mirrors the React Native wrapper) run on every test target.
 */
class AdaptyStoreMessagesTest {

    private val fakeAdaptyPlugin = FakeAdaptyPlugin()

    private fun adaptyImpl(isAndroid: Boolean) = AdaptyImpl(
        adaptyPlugin = fakeAdaptyPlugin,
        appMainScope = TestScope(),
        isAndroid = isAndroid
    )

    private val iosAdapty = adaptyImpl(isAndroid = false)
    private val androidAdapty = adaptyImpl(isAndroid = true)

    private val allWireTypes = listOf(
        "generic",
        "price_increase_consent",
        "billing_issue",
        "win_back_offer",
        AdaptyFakeTestData.STORE_MESSAGE_TYPE_STOREKIT_RAW,
        AdaptyFakeTestData.STORE_MESSAGE_TYPE_STOREKIT_NEGATIVE_RAW,
    )

    private fun capturedRequestJson(): JsonObject =
        jsonInstance.parseToJsonElement(fakeAdaptyPlugin.capturedRequestJsonString ?: "").jsonObject

    // region activate

    @Test
    fun `activate - store messages handling is serialized and success and error are handled`() = runTest {
        // Same baseline as AdaptyImplTest's activate test (the shared template expects these fields)
        val config = AdaptyConfig.Builder(AdaptyFakeTestData.API_KEY)
            .withCustomerUserId(AdaptyFakeTestData.CUSTOMER_USER_ID)
            .withServerCluster(AdaptyConfig.ServerCluster.EU)
            .withLogLevel(AdaptyLogLevel.DEBUG)
            .withStoreMessagesHandling(AdaptyStoreMessagesHandling.MANUAL)
            .build()

        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { iosAdapty.activate(config) },
            method = AdaptyPluginMethod.ACTIVATE,
            param = config,
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `activate - store_messages_handling wire values`() = runTest {
        fakeAdaptyPlugin.simulateSuccessResponse()

        val expected = mapOf(
            AdaptyStoreMessagesHandling.AUTO to "auto",
            AdaptyStoreMessagesHandling.MANUAL to "manual",
        )
        expected.forEach { (handling, wireValue) ->
            iosAdapty.activate(
                AdaptyConfig.Builder(AdaptyFakeTestData.API_KEY)
                    .withStoreMessagesHandling(handling)
                    .build()
            )
            val configuration = capturedRequestJson().getValue("configuration").jsonObject
            assertEquals(JsonPrimitive(wireValue), configuration["store_messages_handling"])
        }
    }

    @Test
    fun `activate - store_messages_handling is omitted when not set`() = runTest {
        fakeAdaptyPlugin.simulateSuccessResponse()

        iosAdapty.activate(AdaptyConfig.Builder(AdaptyFakeTestData.API_KEY).build())

        val configuration = capturedRequestJson().getValue("configuration").jsonObject
        assertFalse("store_messages_handling" in configuration)
    }

    // endregion

    // region getPendingStoreMessageTypes

    @Test
    fun `getPendingStoreMessageTypes - iOS decodes known and storekit_N types`() = runTest {
        val types = allWireTypes.map(::AdaptyStoreMessageType)
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.GET_PENDING_STORE_MESSAGE_TYPES,
            types
        )

        val result = iosAdapty.getPendingStoreMessageTypes()

        assertEquals(
            AdaptyPluginMethod.GET_PENDING_STORE_MESSAGE_TYPES.methodName,
            fakeAdaptyPlugin.capturedRequestMethodName
        )
        assertEquals(
            jsonInstance.parseToJsonElement(
                AdaptyPluginRequestTemplate.getJsonString(
                    AdaptyPluginMethod.GET_PENDING_STORE_MESSAGE_TYPES,
                    Unit
                )
            ),
            capturedRequestJson()
        )

        val decoded = (result as? AdaptyResult.Success)?.value ?: fail("Expected success: $result")
        assertEquals(
            listOf(
                AdaptyStoreMessageType.GENERIC,
                AdaptyStoreMessageType.PRICE_INCREASE_CONSENT,
                AdaptyStoreMessageType.BILLING_ISSUE,
                AdaptyStoreMessageType.WIN_BACK_OFFER,
                AdaptyStoreMessageType("storekit_7"),
                AdaptyStoreMessageType("storekit_-42"),
            ),
            decoded
        )
        // Unknown reasons keep their raw identifier instead of collapsing into a constant
        assertEquals(allWireTypes, decoded.map { it.value })
    }

    @Test
    fun `getPendingStoreMessageTypes - iOS decodes an empty queue`() = runTest {
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.GET_PENDING_STORE_MESSAGE_TYPES,
            emptyList<AdaptyStoreMessageType>()
        )

        val result = iosAdapty.getPendingStoreMessageTypes()

        assertEquals(AdaptyResult.Success(emptyList()), result)
    }

    @Test
    fun `getPendingStoreMessageTypes - iOS maps native error`() = runTest {
        val error = AdaptyError(
            code = AdaptyErrorCode.UNKNOWN,
            message = "Store messages require iOS 16+",
            detail = "unavailable"
        )
        fakeAdaptyPlugin.simulateErrorResponse(error)

        val result = iosAdapty.getPendingStoreMessageTypes()

        assertEquals(AdaptyResult.Error(error), result)
    }

    @Test
    fun `getPendingStoreMessageTypes - Android returns null without calling native`() = runTest {
        val result = androidAdapty.getPendingStoreMessageTypes()

        assertTrue(result is AdaptyResult.Success)
        assertNull(result.value)
        assertNull(fakeAdaptyPlugin.capturedRequestMethodName)
    }

    // endregion

    // region showStoreMessages

    @Test
    fun `showStoreMessages - iOS without filter omits filter and handles success and error`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = { iosAdapty.showStoreMessages() },
            method = AdaptyPluginMethod.SHOW_STORE_MESSAGES,
            param = AdaptyShowStoreMessagesRequest(filter = null),
            expectedSuccessData = Unit
        )
        assertFalse("filter" in capturedRequestJson())
    }

    @Test
    fun `showStoreMessages - iOS with filter sends raw types and handles success and error`() = runTest {
        fakeAdaptyPlugin.verifyApiCallResultBehavior(
            apiCall = {
                iosAdapty.showStoreMessages(
                    iosFilter = listOf(
                        AdaptyStoreMessageType.PRICE_INCREASE_CONSENT,
                        AdaptyStoreMessageType.BILLING_ISSUE,
                        AdaptyStoreMessageType(AdaptyFakeTestData.STORE_MESSAGE_TYPE_STOREKIT_RAW),
                    )
                )
            },
            method = AdaptyPluginMethod.SHOW_STORE_MESSAGES,
            param = AdaptyShowStoreMessagesRequest(
                filter = listOf(
                    "price_increase_consent",
                    "billing_issue",
                    AdaptyFakeTestData.STORE_MESSAGE_TYPE_STOREKIT_RAW,
                )
            ),
            expectedSuccessData = Unit
        )
    }

    @Test
    fun `showStoreMessages - iOS keeps an empty filter (show none, not show all)`() = runTest {
        fakeAdaptyPlugin.simulateSuccessResponse()

        iosAdapty.showStoreMessages(iosFilter = emptyList())

        assertEquals(JsonArray(emptyList()), capturedRequestJson()["filter"])
    }

    @Test
    fun `showStoreMessages - types read from getPendingStoreMessageTypes round-trip unchanged`() = runTest {
        fakeAdaptyPlugin.simulateSuccessResponseForMethod(
            AdaptyPluginMethod.GET_PENDING_STORE_MESSAGE_TYPES,
            allWireTypes.map(::AdaptyStoreMessageType)
        )
        val pending = (iosAdapty.getPendingStoreMessageTypes() as AdaptyResult.Success).value
            ?: fail("Expected pending types on iOS")

        fakeAdaptyPlugin.simulateSuccessResponse()
        iosAdapty.showStoreMessages(iosFilter = pending)

        assertEquals(
            JsonArray(allWireTypes.map(::JsonPrimitive)),
            capturedRequestJson()["filter"]
        )
    }

    @Test
    fun `showStoreMessages - iOS maps operation in progress and resolver failure errors`() = runTest {
        listOf(
            3201 to AdaptyErrorCode.OPERATION_IN_PROGRESS,
            3202 to AdaptyErrorCode.RESOLVER_FAILURE,
        ).forEach { (rawCode, expectedCode) ->
            // The wire carries the raw adapty_code; it must resolve to the dedicated constant, not UNKNOWN
            assertEquals(rawCode, expectedCode.value)
            val nativeError = AdaptyError(
                code = expectedCode,
                message = "Store message error",
                detail = "detail"
            )
            fakeAdaptyPlugin.simulateErrorResponse(nativeError)

            val result = iosAdapty.showStoreMessages()

            assertEquals(AdaptyResult.Error(nativeError), result)
        }
    }

    @Test
    fun `showStoreMessages - Android calls native and drops the iOS-only filter`() = runTest {
        listOf(
            null,
            emptyList(),
            listOf(AdaptyStoreMessageType.BILLING_ISSUE),
        ).forEach { filter ->
            fakeAdaptyPlugin.simulateSuccessResponse()

            val result = androidAdapty.showStoreMessages(iosFilter = filter)

            assertEquals(AdaptyResult.Success(Unit), result)
            assertEquals(
                AdaptyPluginMethod.SHOW_STORE_MESSAGES.methodName,
                fakeAdaptyPlugin.capturedRequestMethodName
            )
            assertEquals(
                jsonInstance.parseToJsonElement(
                    AdaptyPluginRequestTemplate.getJsonString(
                        AdaptyPluginMethod.SHOW_STORE_MESSAGES,
                        AdaptyShowStoreMessagesRequest(filter = null)
                    )
                ),
                capturedRequestJson()
            )
        }
    }

    // endregion
}
