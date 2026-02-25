package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEvent
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventOnboardingCompleted
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventOnboardingStarted
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventProductsScreenPresented
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventRegistrationScreenPresented
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventScreenCompleted
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventScreenPresented
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventSecondScreenPresented
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventUnknown
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEventUserEmailCollected
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class AdaptyOnboardingsAnalyticsEventResponse(
    @SerialName("name")
    val name: String,
    @SerialName("element_id")
    val elementId: String? = null,
    @SerialName("reply")
    val reply: String? = null,
)

internal fun AdaptyOnboardingsAnalyticsEventResponse.asAdaptyOnboardingEvent(): AdaptyOnboardingsAnalyticsEvent {

    return when (this.name) {
        "onboarding_started" -> AdaptyOnboardingsAnalyticsEventOnboardingStarted
        "screen_presented" -> AdaptyOnboardingsAnalyticsEventScreenPresented
        "screen_completed" -> AdaptyOnboardingsAnalyticsEventScreenCompleted(elementId = this.elementId, reply = this.reply)
        "second_screen_presented" -> AdaptyOnboardingsAnalyticsEventSecondScreenPresented
        "registration_screen_presented" -> AdaptyOnboardingsAnalyticsEventRegistrationScreenPresented
        "products_screen_presented" -> AdaptyOnboardingsAnalyticsEventProductsScreenPresented
        "user_email_collected" -> AdaptyOnboardingsAnalyticsEventUserEmailCollected
        "onboarding_completed" -> AdaptyOnboardingsAnalyticsEventOnboardingCompleted
        else -> AdaptyOnboardingsAnalyticsEventUnknown(this.name)
    }

}