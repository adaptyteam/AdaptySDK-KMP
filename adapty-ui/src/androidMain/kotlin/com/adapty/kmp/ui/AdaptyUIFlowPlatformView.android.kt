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
import com.adapty.internal.crossplatform.ui.FlowUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.request.createFlowViewRequestJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.ui.AdaptyFlowView
import kotlinx.datetime.LocalDateTime

@OptIn(AdaptyKMPInternal::class)
@Composable
internal actual fun AdaptyUIFlowPlatformView(
    flow: AdaptyFlow,
    viewId: String,
    modifier: Modifier,
    customTags: Map<String, String>?,
    customTimers: Map<String, LocalDateTime>?,
    customAssets: Map<String, AdaptyCustomAsset>?,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?
) {

    val viewModelStoreOwner = LocalActivity.current as? ViewModelStoreOwner ?: return
    val context = LocalContext.current
    val flowUiManager: FlowUiManager? by safeInject<FlowUiManager>()

    val flowView = remember {
        AdaptyFlowView(context).apply {
            flowUiManager?.setupFlowView(
                flowView = this,
                viewModelStoreOwner = viewModelStoreOwner,
                args = createFlowViewRequestJsonString(
                    flow = flow,
                    customTags = customTags,
                    customTimers = customTimers,
                    customAssets = customAssets,
                    productPurchaseParams = productPurchaseParams
                ),
                id = viewId,
            )
        }
    }

    AndroidView(modifier = modifier, factory = { flowView })
    DisposableEffect(Unit) {
        onDispose {
            flowUiManager?.clearFlowView(flowView)
        }
    }
}
