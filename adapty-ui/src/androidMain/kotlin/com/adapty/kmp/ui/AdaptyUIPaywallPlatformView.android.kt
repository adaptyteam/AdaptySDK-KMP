@file:OptIn(InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.adapty.kmp.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelStoreOwner
import com.adapty.internal.crossplatform.ui.Dependencies.safeInject
import com.adapty.internal.crossplatform.ui.PaywallUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.request.createPaywallViewRequestJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.ui.AdaptyPaywallView
import kotlinx.datetime.LocalDateTime


@OptIn(AdaptyKMPInternal::class)
@Composable
internal actual fun AdaptyUIPaywallPlatformView(
    paywall: AdaptyPaywall,
    modifier: Modifier,
    customTags: Map<String, String>?,
    customTimers: Map<String, LocalDateTime>?,
    customAssets: Map<String, AdaptyCustomAsset>?,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?
) {

    val viewModelStoreOwner = LocalActivity.current as? ViewModelStoreOwner ?: return
    val context = LocalContext.current
    val paywallUiManager: PaywallUiManager? by safeInject<PaywallUiManager>()

    val paywallView = remember {
        AdaptyPaywallView(context).apply {
            paywallUiManager?.setupPaywallView(
                paywallView = this,
                viewModelStoreOwner = viewModelStoreOwner,
                args = createPaywallViewRequestJsonString(
                    paywall = paywall,
                    customTags = customTags,
                    customTimers = customTimers,
                    customAssets = customAssets,
                    productPurchaseParams = productPurchaseParams
                ),
                id = paywall.idForNativePlatformView,
            )
        }
    }

    AndroidView(modifier = modifier, factory = { paywallView })
    DisposableEffect(Unit) {
        onDispose {
            paywallUiManager?.clearPaywallView(paywallView)
        }
    }
}