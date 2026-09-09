package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import kotlinx.datetime.LocalDateTime

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
    //NOT Implemented
}
