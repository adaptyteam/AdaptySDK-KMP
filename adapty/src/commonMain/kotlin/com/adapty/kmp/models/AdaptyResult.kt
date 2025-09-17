package com.adapty.kmp.models

public sealed interface AdaptyResult<out T> {
    public data class Success<T> internal constructor(public val value: T) : AdaptyResult<T>
    public data class Error internal constructor(public val error: AdaptyError) :
        AdaptyResult<Nothing>
}

public inline fun <T> AdaptyResult<T>.getOrNull(): T? = when (this) {
    is AdaptyResult.Success -> value
    is AdaptyResult.Error -> null
}

public inline fun <T> AdaptyResult<T>.exceptionOrNull(): AdaptyError? = when (this) {
    is AdaptyResult.Success -> null
    is AdaptyResult.Error -> error
}

public val <T> AdaptyResult<T>.isSuccess: Boolean
    get() = this is AdaptyResult.Success

public val <T> AdaptyResult<T>.isError: Boolean
    get() = this is AdaptyResult.Error

public inline fun <T> AdaptyResult<T>.onSuccess(action: (T) -> Unit): AdaptyResult<T> {
    if (this is AdaptyResult.Success) action(value)
    return this
}

public inline fun <T> AdaptyResult<T>.onError(action: (AdaptyError) -> Unit): AdaptyResult<T> {
    if (this is AdaptyResult.Error) action(error)
    return this
}

public inline fun <T, R> AdaptyResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (AdaptyError) -> R
): R = when (this) {
    is AdaptyResult.Success -> onSuccess(value)
    is AdaptyResult.Error -> onError(error)
}

