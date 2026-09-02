@file:Suppress("DEPRECATION") // references the deprecated onboarding API

package com.adapty.kmp.models

/**
 * Represents updated state parameters in an Adapty onboarding flow.
 *
 * This is a sealed interface; specific types of updates are represented by the subclasses.
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public sealed interface AdaptyOnboardingsStateUpdatedParams

/**
 * Represents a single-select input update.
 *
 * @property id The identifier of the selection field.
 * @property value The selected value.
 * @property label The display label for the selected value.
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboardingsSelectParams(
    val id: String,
    val value: String,
    val label: String,
) : AdaptyOnboardingsStateUpdatedParams

/**
 * Represents a multi-select input update.
 *
 * @property params The list of selected values, each represented as [AdaptyOnboardingsSelectParams].
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboardingsMultiSelectParams(
    val params: List<AdaptyOnboardingsSelectParams>
) : AdaptyOnboardingsStateUpdatedParams

/**
 * Represents a text, email, or numeric input update.
 *
 * @property input The user input, represented by [AdaptyOnboardingsInput].
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboardingsInputParams(
    val input: AdaptyOnboardingsInput,
) : AdaptyOnboardingsStateUpdatedParams

/**
 * Represents a date picker input update.
 *
 * @property day The selected day (optional).
 * @property month The selected month (optional).
 * @property year The selected year (optional).
 */
@Deprecated(
    "Onboarding is deprecated as of 4.0.0 and will be removed in a future release. Migrate to the Adapty Flow Builder.",
    level = DeprecationLevel.WARNING
)
public data class AdaptyOnboardingsDatePickerParams(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
) : AdaptyOnboardingsStateUpdatedParams
