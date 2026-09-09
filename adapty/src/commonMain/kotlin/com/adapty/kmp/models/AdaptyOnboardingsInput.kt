@file:Suppress("DEPRECATION") // references the deprecated onboarding API

package com.adapty.kmp.models

/**
 * Represents an input value in an Adapty onboarding flow.
 *
 * This is a sealed interface; specific input types are represented by the subclasses.
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public sealed interface AdaptyOnboardingsInput

/**
 * Represents a text input in an onboarding form.
 *
 * @property value The string entered by the user.
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboardingsTextInput(val value: String) : AdaptyOnboardingsInput

/**
 * Represents an email input in an onboarding form.
 *
 * @property value The email entered by the user.
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboardingsEmailInput(val value: String) : AdaptyOnboardingsInput

/**
 * Represents a numeric input in an onboarding form.
 *
 * @property value The number entered by the user.
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboardingsNumberInput(val value: Double) : AdaptyOnboardingsInput
