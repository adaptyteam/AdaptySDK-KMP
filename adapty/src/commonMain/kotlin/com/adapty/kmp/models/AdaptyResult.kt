package com.adapty.kmp.models

/**
 * A wrapper class representing the result of an operation.
 *
 * @param T The type of the successful result.
 */
public sealed interface AdaptyResult<out T> {

    /** Represents a successful result containing a value of type [T]. */
    public data class Success<T> internal constructor(public val value: T) : AdaptyResult<T>

    /** Represents a failed result containing an [AdaptyError]. */
    public data class Error internal constructor(public val error: AdaptyError) :
        AdaptyResult<Nothing>
}

/** Returns the value if this is [AdaptyResult.Success], or null if it is [AdaptyResult.Error]. */
public inline fun <T> AdaptyResult<T>.getOrNull(): T? = when (this) {
    is AdaptyResult.Success -> value
    is AdaptyResult.Error -> null
}

/** Returns the [AdaptyError] if this is [AdaptyResult.Error], or null if it is [AdaptyResult.Success]. */
public inline fun <T> AdaptyResult<T>.exceptionOrNull(): AdaptyError? = when (this) {
    is AdaptyResult.Success -> null
    is AdaptyResult.Error -> error
}

/** Returns true if this is a [AdaptyResult.Success] result. */
public val <T> AdaptyResult<T>.isSuccess: Boolean
    get() = this is AdaptyResult.Success

/** Returns true if this is an [AdaptyResult.Error] result. */
public val <T> AdaptyResult<T>.isError: Boolean
    get() = this is AdaptyResult.Error

/**
 * Executes [action] if this is a [AdaptyResult.Success] result, then returns the receiver.
 */
public inline fun <T> AdaptyResult<T>.onSuccess(action: (T) -> Unit): AdaptyResult<T> {
    if (this is AdaptyResult.Success) action(value)
    return this
}

/**
 * Executes [action] if this is an [AdaptyResult.Error] result, then returns the receiver.
 */
public inline fun <T> AdaptyResult<T>.onError(action: (AdaptyError) -> Unit): AdaptyResult<T> {
    if (this is AdaptyResult.Error) action(error)
    return this
}

/**
 * Transforms the [AdaptyResult] into a value of type [R] using [onSuccess] if successful
 * or [onError] if it is an error.
 */
public inline fun <T, R> AdaptyResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (AdaptyError) -> R
): R = when (this) {
    is AdaptyResult.Success -> onSuccess(value)
    is AdaptyResult.Error -> onError(error)
}

