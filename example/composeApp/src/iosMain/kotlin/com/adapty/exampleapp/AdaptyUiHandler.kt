package com.adapty.exampleapp

import com.adapty.kmp.Adapty
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object AdaptyUiHandler {
    val mainUiScope by lazy { MainScope() }


    //You can create viewmodel object maybe as well, or any other uistate holder (NOT IN SINGLETON though)

    fun showFullScreenPaywallView(placementId: String) {
        showPaywallView(placementId = placementId, style = AdaptyUIIOSPresentationStyle.FULLSCREEN)
    }

    fun showModalSheetPaywallView(placementId: String) {
        showPaywallView(placementId = placementId, style = AdaptyUIIOSPresentationStyle.PAGESHEET)
    }

    @OptIn(AdaptyKMPInternal::class)
    fun getJsonStringAndId(){

//        val jsonString = createPaywallViewRequestJsonString(
//            paywall = paywall,
//            customTags = customTags,
//            customTimers = customTimers,
//            customAssets = customAssets,
//            productPurchaseParams = productPurchaseParams
//        )
//
//        factory.createNativePaywallView(
//            jsonString = jsonString,
//            id = paywall.idForNativePlatformView,
//            onEvent = { eventName, eventDataJsonString ->
//                AdaptyPluginEventHandler.onNewEvent(
//                    eventName = eventName,
//                    eventDataJsonString = eventDataJsonString ?: ""
//                )
//            }
//        )
    }

    private fun showPaywallView(placementId: String, style: AdaptyUIIOSPresentationStyle) =
        mainUiScope.launch {

            Adapty.getPaywall(placementId = placementId)
                .onSuccess { paywall ->
                    AdaptyUI.createPaywallView(
                        paywall = paywall,
                        customTags = mapOf(
                            "CUSTOM_TAG_NAME" to "Walter White",
                            "CUSTOM_TAG_PHONE" to "+1 234 567890",
                            "CUSTOM_TAG_CITY" to "Albuquerque",
                            "CUSTOM_TAG_EMAIL" to "walter@white.com",
                        ),
                    ).onSuccess { view ->
                        view.present(iosPresentationStyle = style)
                    }.onError { error ->
                        AppLogger.e("Error loading paywall: $error")
                    }
                }
                .onError { error ->
                    AppLogger.e("Error loading paywall: $error")
                }


        }

}