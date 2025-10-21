package com.adapty.kmp.models

/**
 * Represents an input value in an Adapty onboarding flow.
 *
 * This is a sealed interface; specific input types are represented by the subclasses.
 */
public sealed interface AdaptyOnboardingsInput

/**
 * Represents a text input in an onboarding form.
 *
 * @property value The string entered by the user.
 */
public data class AdaptyOnboardingsTextInput(val value: String) : AdaptyOnboardingsInput

/**
 * Represents an email input in an onboarding form.
 *
 * @property value The email entered by the user.
 */
public data class AdaptyOnboardingsEmailInput(val value: String) : AdaptyOnboardingsInput

/**
 * Represents a numeric input in an onboarding form.
 *
 * @property value The number entered by the user.
 */
public data class AdaptyOnboardingsNumberInput(val value: Double) : AdaptyOnboardingsInput
