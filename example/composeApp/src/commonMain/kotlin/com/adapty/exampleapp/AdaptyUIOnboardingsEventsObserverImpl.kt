package com.adapty.exampleapp

import com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
import com.adapty.kmp.models.AdaptyError
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
import com.adapty.kmp.models.AdaptyOnboardingsDatePickerParams
import com.adapty.kmp.models.AdaptyOnboardingsEmailInput
import com.adapty.kmp.models.AdaptyOnboardingsInputParams
import com.adapty.kmp.models.AdaptyOnboardingsMultiSelectParams
import com.adapty.kmp.models.AdaptyOnboardingsNumberInput
import com.adapty.kmp.models.AdaptyOnboardingsSelectParams
import com.adapty.kmp.models.AdaptyOnboardingsStateUpdatedParams
import com.adapty.kmp.models.AdaptyOnboardingsTextInput
import com.adapty.kmp.models.AdaptyUIOnboardingMeta
import com.adapty.kmp.models.AdaptyUIOnboardingView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class AdaptyUIOnboardingsEventsObserverImpl(
    private val uiCoroutineScope: CoroutineScope = MainScope(),
) : AdaptyUIOnboardingsEventsObserver {

    override val mainUiScope: CoroutineScope get() = uiCoroutineScope

    override fun onboardingViewDidFinishLoading(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta
    ) {
        AppLogger.d("Onboarding view did finish loading of view $view with meta $meta")
    }

    override fun onboardingViewDidFailWithError(
        view: AdaptyUIOnboardingView,
        error: AdaptyError
    ) {
        AppLogger.e("Onboarding view did fail with error $error")
    }

    override fun onboardingViewOnCloseAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String
    ) {
        AppLogger.d("Onboarding view on close action of view $view with meta $meta and actionId $actionId")
    }

    override fun onboardingViewOnPaywallAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String
    ) {
        AppLogger.d("Onboarding view on paywall action of view $view with meta $meta and actionId $actionId")
    }

    override fun onboardingViewOnCustomAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String
    ) {
        AppLogger.d("Onboarding view on custom action of view $view with meta $meta and actionId $actionId")
    }

    override fun onboardingViewOnStateUpdatedAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        elementId: String,
        params: AdaptyOnboardingsStateUpdatedParams
    ) {
        AppLogger.d("Onboarding view on state updated action of view $view with meta $meta and elementId $elementId")

        when (params) {
            is AdaptyOnboardingsSelectParams -> {
                val id = params.id
                val value = params.value
                val label = params.label
                AppLogger.d("#Example# onboardingViewOnStateUpdatedAction select $id $value $label")
            }

            is AdaptyOnboardingsMultiSelectParams -> {
                val paramsString =
                    params.params.joinToString(", ") { "(id: ${it.id}, value: ${it.value}, label: ${it.label})" }
                AppLogger.d("#Example# onboardingViewOnStateUpdatedAction multiSelect: [$paramsString]")
            }

            is AdaptyOnboardingsInputParams -> {
                when (val input = params.input) {
                    is AdaptyOnboardingsTextInput -> AppLogger.d("#Example# onboardingViewOnStateUpdatedAction text ${input.value}")
                    is AdaptyOnboardingsEmailInput -> AppLogger.d("#Example# onboardingViewOnStateUpdatedAction email ${input.value}")
                    is AdaptyOnboardingsNumberInput -> AppLogger.d("#Example# onboardingViewOnStateUpdatedAction number ${input.value}")
                }
            }

            is AdaptyOnboardingsDatePickerParams -> {
                val day = params.day
                val month = params.month
                val year = params.year
                AppLogger.d("#Example# onboardingViewOnStateUpdatedAction datePicker $day $month $year")
            }
        }

    }

    override fun onboardingViewOnAnalyticsEvent(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        event: AdaptyOnboardingsAnalyticsEvent
    ) {
        when (event) {
            is AdaptyOnboardingsAnalyticsEventOnboardingStarted ->
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent onboardingStarted, meta = $meta")

            is AdaptyOnboardingsAnalyticsEventScreenPresented ->
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent screenPresented, meta = $meta")

            is AdaptyOnboardingsAnalyticsEventScreenCompleted -> {
                val elementId = event.elementId
                val reply = event.reply
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent screenCompleted, meta = $meta, elementId = $elementId, reply = $reply")
            }

            is AdaptyOnboardingsAnalyticsEventSecondScreenPresented ->
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent secondScreenPresented, meta = $meta")

            is AdaptyOnboardingsAnalyticsEventRegistrationScreenPresented ->
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent registrationScreenPresented, meta = $meta")

            is AdaptyOnboardingsAnalyticsEventProductsScreenPresented ->
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent productsScreenPresented, meta = $meta")

            is AdaptyOnboardingsAnalyticsEventUserEmailCollected ->
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent userEmailCollected, meta = $meta")

            is AdaptyOnboardingsAnalyticsEventOnboardingCompleted ->
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent onboardingCompleted, meta = $meta")

            is AdaptyOnboardingsAnalyticsEventUnknown -> {
                val name = event.name
                AppLogger.d("#Example# onboardingViewOnAnalyticsEvent unknown $name")
            }
        }

    }

}