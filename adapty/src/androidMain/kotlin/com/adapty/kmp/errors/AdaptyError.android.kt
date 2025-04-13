package com.adapty.kmp.errors

import com.adapty.errors.AdaptyError as AdaptyErrorAndroid

internal fun AdaptyErrorAndroid?.asAdaptyError(): AdaptyError? {
    return this?.let { adaptyErrorAndroid ->
        AdaptyError(
            originalError = adaptyErrorAndroid.originalError,
            message = adaptyErrorAndroid.message ?: "",
            adaptyErrorCode = adaptyErrorAndroid.adaptyErrorCode.asAdaptyErrorCode()
        )
    }
}