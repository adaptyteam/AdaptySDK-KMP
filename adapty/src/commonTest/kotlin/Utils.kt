import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.utils.jsonInstance
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyErrorCode
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.fold
import kotlinx.serialization.json.jsonObject
import kotlin.test.assertEquals
import kotlin.test.fail

private val testError = AdaptyError(
    code = AdaptyErrorCode.UNKNOWN,
    message = "Test Error Message",
    detail = "Test Error Message Detail"
)

internal suspend fun <T : Any> FakeAdaptyPlugin.verifyApiCallResultBehavior(
    apiCall: suspend () -> AdaptyResult<T>,
    method: AdaptyPluginMethod,
    param: Any = Unit,
    expectedSuccessData: T,
    onSuccess: (T) -> Unit = { actual -> assertEquals(expectedSuccessData, actual) },
    onError: (AdaptyError) -> Unit = { error -> assertEquals(testError, error) },
    simulateSuccessResponse: () -> Unit = {
        simulateSuccessResponseForMethod(method, expectedSuccessData)
    },
    simulateErrorResponse: () -> Unit = {
        simulateErrorResponse(testError)
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


private fun FakeAdaptyPlugin.verifyRequestJson(method: AdaptyPluginMethod, param: Any) {
    val expectedMethodName = method.methodName
    val expectedRequestJson =
        AdaptyPluginRequestTemplate.getJsonString(method = method, param = param)

    val actualRequestJson = capturedRequestJsonString
    val actualMethodName = capturedRequestMethodName

    assertEquals(expectedMethodName, actualMethodName)
    assertJsonStringEquals(expectedRequestJson, actualRequestJson)
}


// Helper function to assert that two JSON strings are equivalent
private fun assertJsonStringEquals(
    expectedJsonString: String?,
    actualJsonString: String?,
    message: String? = null
) {
    val actualJson = jsonInstance.parseToJsonElement(actualJsonString ?: "").jsonObject
    val expectedJson =
        jsonInstance.parseToJsonElement(expectedJsonString ?: "").jsonObject

    assertEquals(expectedJson, actualJson, message)
}