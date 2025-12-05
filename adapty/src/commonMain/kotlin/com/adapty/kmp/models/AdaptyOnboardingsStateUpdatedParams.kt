package com.adapty.kmp.models

/**
 * Represents updated state parameters in an Adapty onboarding flow.
 *
 * This is a sealed interface; specific types of updates are represented by the subclasses.
 */
public sealed interface AdaptyOnboardingsStateUpdatedParams

/**
 * Represents a single-select input update.
 *
 * @property id The identifier of the selection field.
 * @property value The selected value.
 * @property label The display label for the selected value.
 */
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
public data class AdaptyOnboardingsMultiSelectParams(
    val params: List<AdaptyOnboardingsSelectParams>
) : AdaptyOnboardingsStateUpdatedParams

/**
 * Represents a text, email, or numeric input update.
 *
 * @property input The user input, represented by [AdaptyOnboardingsInput].
 */
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
public data class AdaptyOnboardingsDatePickerParams(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
) : AdaptyOnboardingsStateUpdatedParams
