package com.adapty.kmp.models

/**
 * Represents an error returned by the Adapty SDK.
 *
 * Each instance contains a [code] indicating the type of error, a human-readable [message],
 * and an optional [detail] field with additional context.
 *
 * You can use this to handle specific error cases, such as network issues or invalid configurations.
 *
 * Example:
 * ```
 * when (error.code) {
 *     AdaptyErrorCode.NETWORK -> showRetryDialog()
 *     else -> logError(error)
 * }
 * ```
 *
 * @property code The specific error code describing what went wrong.
 * @property message A human-readable error message.
 * @property detail Optional additional information or context about the error.
 *
 * @see AdaptyErrorCode
 */
public data class AdaptyError internal constructor(
    public val code: AdaptyErrorCode = AdaptyErrorCode.UNKNOWN,
    override val message: String = "",
    public val detail: String? = null,
) : Exception(message)