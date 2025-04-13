import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.models.AdaptyError

internal class FakeAdaptyPlugin : AdaptyPlugin {

    var capturedRequestJsonString: String? = null
    var capturedRequestMethodName: String? = null
    private var responseJsonString: String? = null

    override fun initialize() {}

    override fun executePlatformSpecific(
        method: String,
        data: String,
        onResult: (String?) -> Unit
    ) {
        capturedRequestJsonString = data
        capturedRequestMethodName = method
        onResult(responseJsonString)
    }

    fun simulateSuccessResponseForMethod(method: AdaptyPluginMethod, successData: Any) {
        responseJsonString =
            AdaptyPluginResponseTemplate.getJsonString(method = method, successData = successData)
    }

    fun simulateSuccessResponse() {
        responseJsonString = AdaptyPluginResponseTemplate.genericSuccessResponse()
    }

    fun simulateErrorResponse(error: AdaptyError) {
        responseJsonString = AdaptyPluginResponseTemplate.errorResponse(error)
    }
}