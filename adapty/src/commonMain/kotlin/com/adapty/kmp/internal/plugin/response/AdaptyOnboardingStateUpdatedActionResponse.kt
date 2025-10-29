package com.adapty.kmp.internal.plugin.response


import com.adapty.kmp.models.AdaptyOnboardingsDatePickerParams
import com.adapty.kmp.models.AdaptyOnboardingsEmailInput
import com.adapty.kmp.models.AdaptyOnboardingsInputParams
import com.adapty.kmp.models.AdaptyOnboardingsMultiSelectParams
import com.adapty.kmp.models.AdaptyOnboardingsNumberInput
import com.adapty.kmp.models.AdaptyOnboardingsSelectParams
import com.adapty.kmp.models.AdaptyOnboardingsStateUpdatedParams
import com.adapty.kmp.models.AdaptyOnboardingsTextInput
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator


@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("element_type")
@Serializable
internal sealed interface AdaptyOnboardingStateUpdatedActionResponse {

    @SerialName("element_id")
    val elementId: String

    @Serializable
    @SerialName("select")
    data class SelectAction(
        override val elementId: String,
        @SerialName("value") val value: AdaptyUIOnboardingsSelectParamsResponse
    ) : AdaptyOnboardingStateUpdatedActionResponse

    @Serializable
    @SerialName("multi_select")
    data class MultiSelectAction(
        override val elementId: String,
        @SerialName("value") val value: List<AdaptyUIOnboardingsSelectParamsResponse>
    ) : AdaptyOnboardingStateUpdatedActionResponse

    @Serializable
    @SerialName("input")
    data class InputAction(
        override val elementId: String,
        @SerialName("value") val value: InputValue
    ) : AdaptyOnboardingStateUpdatedActionResponse {

        @JsonClassDiscriminator("type")
        @Serializable
        sealed interface InputValue {
            @Serializable
            @SerialName("text")
            data class Text(@SerialName("value") val value: String) : InputValue

            @Serializable
            @SerialName("email")
            data class Email(@SerialName("value") val value: String) : InputValue

            @Serializable
            @SerialName("number")
            data class Number(@SerialName("value") val value: Double) : InputValue
        }
    }

    @Serializable
    @SerialName("date_picker")
    data class DatePickerAction(
        override val elementId: String,
        @SerialName("value") val value: DateValue
    ) : AdaptyOnboardingStateUpdatedActionResponse {
        @Serializable
        data class DateValue(
            @SerialName("day") val day: Int? = null,
            @SerialName("month") val month: Int? = null,
            @SerialName("year") val year: Int? = null
        )
    }
}

internal fun AdaptyOnboardingStateUpdatedActionResponse.asAdaptyOnboardingsStateUpdatedParams(): AdaptyOnboardingsStateUpdatedParams {
    return when (this) {
        is AdaptyOnboardingStateUpdatedActionResponse.SelectAction -> AdaptyOnboardingsSelectParams(
            id = value.id,
            value = value.value,
            label = value.label
        )

        is AdaptyOnboardingStateUpdatedActionResponse.MultiSelectAction -> {
            AdaptyOnboardingsMultiSelectParams(
                params = value.map {
                    AdaptyOnboardingsSelectParams(
                        id = it.id,
                        value = it.value,
                        label = it.label
                    )
                }

            )
        }

        is AdaptyOnboardingStateUpdatedActionResponse.InputAction -> {
            AdaptyOnboardingsInputParams(
                input = when (value) {
                    is AdaptyOnboardingStateUpdatedActionResponse.InputAction.InputValue.Email -> {
                        AdaptyOnboardingsEmailInput(value = value.value)
                    }

                    is AdaptyOnboardingStateUpdatedActionResponse.InputAction.InputValue.Number -> {
                        AdaptyOnboardingsNumberInput(value = value.value)
                    }

                    is AdaptyOnboardingStateUpdatedActionResponse.InputAction.InputValue.Text -> {
                        AdaptyOnboardingsTextInput(value = value.value)
                    }
                }
            )
        }

        is AdaptyOnboardingStateUpdatedActionResponse.DatePickerAction -> {
            AdaptyOnboardingsDatePickerParams(
                day = value.day,
                month = value.month,
                year = value.year
            )
        }
    }
}


