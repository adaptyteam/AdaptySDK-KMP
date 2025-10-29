package com.adapty.kmp.models

public sealed interface AdaptyOnboardingsStateUpdatedParams

public data class AdaptyOnboardingsSelectParams(
    val id: String,
    val value: String,
    val label: String,
) : AdaptyOnboardingsStateUpdatedParams

public data class AdaptyOnboardingsMultiSelectParams(
    val params: List<AdaptyOnboardingsSelectParams>
) : AdaptyOnboardingsStateUpdatedParams

public data class AdaptyOnboardingsInputParams(
    val input: AdaptyOnboardingsInput,
) : AdaptyOnboardingsStateUpdatedParams

public data class AdaptyOnboardingsDatePickerParams(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
) : AdaptyOnboardingsStateUpdatedParams
