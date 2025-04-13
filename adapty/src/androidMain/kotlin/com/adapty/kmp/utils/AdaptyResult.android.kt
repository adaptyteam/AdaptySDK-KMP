package com.adapty.kmp.utils

import com.adapty.utils.AdaptyResult as AdaptyResultAndroid

internal fun <T, R> AdaptyResultAndroid<T>.asResult(transform: (T) -> R): Result<R> {
    return when (this) {
        is AdaptyResultAndroid.Success -> Result.success(transform(value))
        is AdaptyResultAndroid.Error -> Result.failure(error)
    }
}
