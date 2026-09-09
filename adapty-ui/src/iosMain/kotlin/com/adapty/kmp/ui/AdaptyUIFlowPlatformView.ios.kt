package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.request.createFlowViewRequestJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.LocalDateTime

@OptIn(AdaptyKMPInternal::class, ExperimentalForeignApi::class)
@Composable
internal actual fun AdaptyUIFlowPlatformView(
    flow: AdaptyFlow,
    viewId: String,
    locale: String?,
    customLayoutId: String?,
    modifier: Modifier,
    customTags: Map<String, String>?,
    customTimers: Map<String, LocalDateTime>?,
    customAssets: Map<String, AdaptyCustomAsset>?,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?,
    androidEnableSafeArea: Boolean
) {

    key(viewId) {
        val factory = remember { IosNativeViewFactory() }
        val view = remember(factory) {

            val jsonString = createFlowViewRequestJsonString(
                flow = flow,
                locale = locale,
                customLayoutId = customLayoutId,
                customTags = customTags,
                customTimers = customTimers,
                customAssets = customAssets,
                productPurchaseParams = productPurchaseParams,
                enableSafeAreaPaddings = androidEnableSafeArea
            )

            factory.createNativePaywallView(
                jsonString = jsonString,
                id = viewId,
                onEvent = { eventName, eventDataJsonString ->
                    AdaptyPluginEventHandler.onNewEvent(
                        eventName = eventName,
                        eventDataJsonString = eventDataJsonString ?: ""
                    )
                }
            )
        }
        UIKitViewController(
            modifier = modifier,
            update = {},
            factory = { view },
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
    }
}
