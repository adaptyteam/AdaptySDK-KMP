package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.AdaptyUIFlowsEventsObserver
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIFlowView
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDateTime

/**
 * Renders an Adapty flow (cross_platform 4.0.0) as a Compose Multiplatform view.
 *
 * The default runtime behavior follows the 4.0.0 contract (see [AdaptyUIFlowsEventsObserver]):
 * system back keeps the view open, and successful purchase/restore do not auto-dismiss. Override
 * the callbacks to customize.
 *
 * The contract defaults (close dismisses, an URL action opens the URL, an error dismisses) are
 * applied by the global flow observer, which runs alongside these callbacks — so a callback here
 * observes an event, it does not replace that default.
 *
 * Requests that await an answer are not events and are not handled here: register an
 * [com.adapty.kmp.AdaptyUISystemRequestsHandler] via `AdaptyUI.setSystemRequestsHandler` for
 * permissions and in-app review, and an [com.adapty.kmp.AdaptyUIObserverModeResolver] via
 * `AdaptyUI.setObserverModeResolver` for observer-mode purchases and restores.
 */
@OptIn(AdaptyKMPInternal::class)
@Composable
public fun AdaptyUIFlowPlatformView(
    flow: AdaptyFlow,
    modifier: Modifier = Modifier,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
    onDidAppear: (view: AdaptyUIFlowView) -> Unit = {},
    onDidDisappear: (view: AdaptyUIFlowView) -> Unit = {},
    onDidPerformAction: (view: AdaptyUIFlowView, action: AdaptyUIAction) -> Unit = { _, _ -> },
    onDidSelectProduct: (view: AdaptyUIFlowView, productId: String) -> Unit = { _, _ -> },
    onDidStartPurchase: (view: AdaptyUIFlowView, product: AdaptyPaywallProduct) -> Unit = { _, _ -> },
    onDidFinishPurchase: (view: AdaptyUIFlowView, product: AdaptyPaywallProduct, result: AdaptyPurchaseResult) -> Unit = { _, _, _ -> },
    onDidFailPurchase: (view: AdaptyUIFlowView, product: AdaptyPaywallProduct, error: AdaptyError) -> Unit = { _, _, _ -> },
    onDidStartRestore: (view: AdaptyUIFlowView) -> Unit = {},
    onDidFinishRestore: (view: AdaptyUIFlowView, profile: AdaptyProfile) -> Unit = { _, _ -> },
    onDidFailRestore: (view: AdaptyUIFlowView, error: AdaptyError) -> Unit = { _, _ -> },
    onDidReceiveError: (view: AdaptyUIFlowView, error: AdaptyError) -> Unit = { _, _ -> },
    onDidFailLoadingProducts: (view: AdaptyUIFlowView, error: AdaptyError) -> Unit = { _, _ -> },
    onDidFinishWebPaymentNavigation: (view: AdaptyUIFlowView, product: AdaptyPaywallProduct?, error: AdaptyError?) -> Unit = { _, _, _ -> },
    onDidReceiveAnalyticEvent: (view: AdaptyUIFlowView, name: String, paramsJsonString: String) -> Unit = { _, _, _ -> },
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        AdaptyUI.registerFlowEventsListener(
            viewId = flow.idForNativePlatformView,
            observer = object : AdaptyUIFlowsEventsObserver {
                override val mainUiScope: CoroutineScope = coroutineScope
                override fun flowViewDidAppear(view: AdaptyUIFlowView) = onDidAppear(view)
                override fun flowViewDidDisappear(view: AdaptyUIFlowView) = onDidDisappear(view)
                override fun flowViewDidPerformAction(view: AdaptyUIFlowView, action: AdaptyUIAction) =
                    onDidPerformAction(view, action)

                override fun flowViewDidSelectProduct(view: AdaptyUIFlowView, productId: String) =
                    onDidSelectProduct(view, productId)

                override fun flowViewDidStartPurchase(view: AdaptyUIFlowView, product: AdaptyPaywallProduct) =
                    onDidStartPurchase(view, product)

                override fun flowViewDidFinishPurchase(
                    view: AdaptyUIFlowView,
                    product: AdaptyPaywallProduct,
                    purchaseResult: AdaptyPurchaseResult
                ) = onDidFinishPurchase(view, product, purchaseResult)

                override fun flowViewDidFailPurchase(
                    view: AdaptyUIFlowView,
                    product: AdaptyPaywallProduct,
                    error: AdaptyError
                ) = onDidFailPurchase(view, product, error)

                override fun flowViewDidStartRestore(view: AdaptyUIFlowView) = onDidStartRestore(view)
                override fun flowViewDidFinishRestore(view: AdaptyUIFlowView, profile: AdaptyProfile) =
                    onDidFinishRestore(view, profile)

                override fun flowViewDidFailRestore(view: AdaptyUIFlowView, error: AdaptyError) =
                    onDidFailRestore(view, error)

                override fun flowViewDidReceiveError(view: AdaptyUIFlowView, error: AdaptyError) =
                    onDidReceiveError(view, error)

                override fun flowViewDidFailLoadingProducts(view: AdaptyUIFlowView, error: AdaptyError) =
                    onDidFailLoadingProducts(view, error)

                override fun flowViewDidFinishWebPaymentNavigation(
                    view: AdaptyUIFlowView,
                    product: AdaptyPaywallProduct?,
                    error: AdaptyError?
                ) = onDidFinishWebPaymentNavigation(view, product, error)

                override fun flowViewDidReceiveAnalyticEvent(
                    view: AdaptyUIFlowView,
                    name: String,
                    paramsJsonString: String
                ) = onDidReceiveAnalyticEvent(view, name, paramsJsonString)
            })
    }
    DisposableEffect(Unit) {
        onDispose {
            AdaptyUI.unregisterFlowEventsListener(flow.idForNativePlatformView)
        }
    }

    AdaptyUIFlowPlatformView(
        flow = flow,
        modifier = modifier,
        customTags = customTags,
        customTimers = customTimers,
        customAssets = customAssets,
        productPurchaseParams = productPurchaseParams
    )
}

@Composable
internal expect fun AdaptyUIFlowPlatformView(
    flow: AdaptyFlow,
    modifier: Modifier = Modifier,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
)
