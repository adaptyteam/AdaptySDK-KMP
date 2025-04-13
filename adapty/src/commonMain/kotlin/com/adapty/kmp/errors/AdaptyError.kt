package com.adapty.kmp.errors

public class AdaptyError internal constructor(
    public val originalError: Throwable? = null,
    message: String = "",
    public val adaptyErrorCode: AdaptyErrorCode = AdaptyErrorCode.UNKNOWN,
) : Exception(message, originalError)