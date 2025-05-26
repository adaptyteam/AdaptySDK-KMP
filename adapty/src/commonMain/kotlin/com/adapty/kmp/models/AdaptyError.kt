package com.adapty.kmp.models

public data class AdaptyError internal constructor(
    public val code: AdaptyErrorCode = AdaptyErrorCode.UNKNOWN,
    override val message: String = "",
    public val detail: String? = null,
) : Exception(message)