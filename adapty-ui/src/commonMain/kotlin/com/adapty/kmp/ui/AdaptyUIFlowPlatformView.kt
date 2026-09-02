package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.AdaptyUIFlowsEventsObserver
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.request.createFlowViewRequestJsonString
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
    locale: String? = null,
    customLayoutId: String? = null,
    modifier: Modifier = Modifier,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
    androidEnableSafeArea: Boolean = false,
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

    val setupArgs = remember(flow, locale, customLayoutId, customTags, customTimers, customAssets, productPurchaseParams, androidEnableSafeArea) {
        createFlowViewRequestJsonString(
            flow = flow,
            locale = locale,
            customLayoutId = customLayoutId,
            customTags = customTags,
            customTimers = customTimers,
            customAssets = customAssets,
            productPurchaseParams = productPurchaseParams,
            enableSafeAreaPaddings = androidEnableSafeArea
        )
    }


    val viewId = rememberSaveable(setupArgs) { flow.createNativePlatformViewId() }

    // The observer is registered once per viewId, so it must not capture the callback lambdas of
    // the composition that registered it — read them through state instead.
    val currentOnDidAppear by rememberUpdatedState(onDidAppear)
    val currentOnDidDisappear by rememberUpdatedState(onDidDisappear)
    val currentOnDidPerformAction by rememberUpdatedState(onDidPerformAction)
    val currentOnDidSelectProduct by rememberUpdatedState(onDidSelectProduct)
    val currentOnDidStartPurchase by rememberUpdatedState(onDidStartPurchase)
    val currentOnDidFinishPurchase by rememberUpdatedState(onDidFinishPurchase)
    val currentOnDidFailPurchase by rememberUpdatedState(onDidFailPurchase)
    val currentOnDidStartRestore by rememberUpdatedState(onDidStartRestore)
    val currentOnDidFinishRestore by rememberUpdatedState(onDidFinishRestore)
    val currentOnDidFailRestore by rememberUpdatedState(onDidFailRestore)
    val currentOnDidReceiveError by rememberUpdatedState(onDidReceiveError)
    val currentOnDidFailLoadingProducts by rememberUpdatedState(onDidFailLoadingProducts)
    val currentOnDidFinishWebPaymentNavigation by rememberUpdatedState(onDidFinishWebPaymentNavigation)
    val currentOnDidReceiveAnalyticEvent by rememberUpdatedState(onDidReceiveAnalyticEvent)

    LaunchedEffect(viewId) {
        AdaptyUI.registerFlowEventsListener(
            viewId = viewId,
            observer = object : AdaptyUIFlowsEventsObserver {
                override val mainUiScope: CoroutineScope = coroutineScope
                override fun flowViewDidAppear(view: AdaptyUIFlowView) = currentOnDidAppear(view)
                override fun flowViewDidDisappear(view: AdaptyUIFlowView) = currentOnDidDisappear(view)
                override fun flowViewDidPerformAction(view: AdaptyUIFlowView, action: AdaptyUIAction) =
                    currentOnDidPerformAction(view, action)

                override fun flowViewDidSelectProduct(view: AdaptyUIFlowView, productId: String) =
                    currentOnDidSelectProduct(view, productId)

                override fun flowViewDidStartPurchase(view: AdaptyUIFlowView, product: AdaptyPaywallProduct) =
                    currentOnDidStartPurchase(view, product)

                override fun flowViewDidFinishPurchase(
                    view: AdaptyUIFlowView,
                    product: AdaptyPaywallProduct,
                    purchaseResult: AdaptyPurchaseResult
                ) = currentOnDidFinishPurchase(view, product, purchaseResult)

                override fun flowViewDidFailPurchase(
                    view: AdaptyUIFlowView,
                    product: AdaptyPaywallProduct,
                    error: AdaptyError
                ) = currentOnDidFailPurchase(view, product, error)

                override fun flowViewDidStartRestore(view: AdaptyUIFlowView) = currentOnDidStartRestore(view)
                override fun flowViewDidFinishRestore(view: AdaptyUIFlowView, profile: AdaptyProfile) =
                    currentOnDidFinishRestore(view, profile)

                override fun flowViewDidFailRestore(view: AdaptyUIFlowView, error: AdaptyError) =
                    currentOnDidFailRestore(view, error)

                override fun flowViewDidReceiveError(view: AdaptyUIFlowView, error: AdaptyError) =
                    currentOnDidReceiveError(view, error)

                override fun flowViewDidFailLoadingProducts(view: AdaptyUIFlowView, error: AdaptyError) =
                    currentOnDidFailLoadingProducts(view, error)

                override fun flowViewDidFinishWebPaymentNavigation(
                    view: AdaptyUIFlowView,
                    product: AdaptyPaywallProduct?,
                    error: AdaptyError?
                ) = currentOnDidFinishWebPaymentNavigation(view, product, error)

                override fun flowViewDidReceiveAnalyticEvent(
                    view: AdaptyUIFlowView,
                    name: String,
                    paramsJsonString: String
                ) = currentOnDidReceiveAnalyticEvent(view, name, paramsJsonString)
            })
    }
    DisposableEffect(viewId) {
        onDispose {
            AdaptyUI.unregisterFlowEventsListener(viewId)
        }
    }

    AdaptyUIFlowPlatformView(
        flow = flow,
        viewId = viewId,
        locale = locale,
        customLayoutId = customLayoutId,
        modifier = modifier,
        customTags = customTags,
        customTimers = customTimers,
        customAssets = customAssets,
        productPurchaseParams = productPurchaseParams,
        androidEnableSafeArea = androidEnableSafeArea
    )
}

@Composable
internal expect fun AdaptyUIFlowPlatformView(
    flow: AdaptyFlow,
    viewId: String,
    locale: String? = null,
    customLayoutId: String? = null,
    modifier: Modifier = Modifier,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
    androidEnableSafeArea: Boolean = false,
)
