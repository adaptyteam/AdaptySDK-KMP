package com.adapty.kmp.models

public sealed interface AdaptyOnboardingsInput

public data class AdaptyOnboardingsTextInput(val value: String) : AdaptyOnboardingsInput
public data class AdaptyOnboardingsEmailInput(val value: String) : AdaptyOnboardingsInput
public data class AdaptyOnboardingsNumberInput(val value: Double) : AdaptyOnboardingsInput
