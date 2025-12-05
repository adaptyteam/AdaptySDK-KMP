package com.adapty.kmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.AdaptyUIPaywallsEventsObserver
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIPaywallView
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDateTime

@OptIn(AdaptyKMPInternal::class)
@Composable
public fun AdaptyUIPaywallPlatformView(
    paywall: AdaptyPaywall,
    modifier: Modifier = Modifier,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
    onDidAppear: (view: AdaptyUIPaywallView) -> Unit = {},
    onDidDisappear: (view: AdaptyUIPaywallView) -> Unit = {},
    onDidPerformAction: (view: AdaptyUIPaywallView, action: AdaptyUIAction) -> Unit = { _, _ -> },
    onDidSelectProduct: (view: AdaptyUIPaywallView, productId: String) -> Unit = { _, _ -> },
    onDidStartPurchase: (view: AdaptyUIPaywallView, product: AdaptyPaywallProduct) -> Unit = { _, _ -> },
    onDidFinishPurchase: (view: AdaptyUIPaywallView, product: AdaptyPaywallProduct, result: AdaptyPurchaseResult) -> Unit = { _, _, _ -> },
    onDidFailPurchase: (view: AdaptyUIPaywallView, product: AdaptyPaywallProduct, error: AdaptyError) -> Unit = { _, _, _ -> },
    onDidStartRestore: (view: AdaptyUIPaywallView) -> Unit = {},
    onDidFinishRestore: (view: AdaptyUIPaywallView, profile: AdaptyProfile) -> Unit = { _, _ -> },
    onDidFailRestore: (view: AdaptyUIPaywallView, error: AdaptyError) -> Unit = { _, _ -> },
    onDidFailRendering: (view: AdaptyUIPaywallView, error: AdaptyError) -> Unit = { _, _ -> },
    onDidFailLoadingProducts: (view: AdaptyUIPaywallView, error: AdaptyError) -> Unit = { _, _ -> },
    onDidFinishWebPaymentNavigation: (view: AdaptyUIPaywallView, product: AdaptyPaywallProduct?, error: AdaptyError?) -> Unit = { _, _, _ -> }
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {

        AdaptyUI.registerPaywallEventsListener(
            viewId = paywall.idForNativePlatformView,
            observer = object : AdaptyUIPaywallsEventsObserver {
                override val mainUiScope: CoroutineScope = coroutineScope
                override fun paywallViewDidAppear(view: AdaptyUIPaywallView) = onDidAppear(view)
                override fun paywallViewDidDisappear(view: AdaptyUIPaywallView) =
                    onDidDisappear(view)

                override fun paywallViewDidPerformAction(
                    view: AdaptyUIPaywallView,
                    action: AdaptyUIAction
                ) = onDidPerformAction(view, action)

                override fun paywallViewDidSelectProduct(
                    view: AdaptyUIPaywallView,
                    productId: String
                ) = onDidSelectProduct(view, productId)

                override fun paywallViewDidStartPurchase(
                    view: AdaptyUIPaywallView,
                    product: AdaptyPaywallProduct
                ) = onDidStartPurchase(view, product)

                override fun paywallViewDidFinishPurchase(
                    view: AdaptyUIPaywallView,
                    product: AdaptyPaywallProduct,
                    purchaseResult: AdaptyPurchaseResult
                ) = onDidFinishPurchase(view, product, purchaseResult)

                override fun paywallViewDidFailPurchase(
                    view: AdaptyUIPaywallView,
                    product: AdaptyPaywallProduct,
                    error: AdaptyError
                ) =
                    onDidFailPurchase(view, product, error)

                override fun paywallViewDidStartRestore(view: AdaptyUIPaywallView) =
                    onDidStartRestore(view)

                override fun paywallViewDidFinishRestore(
                    view: AdaptyUIPaywallView,
                    profile: AdaptyProfile
                ) = onDidFinishRestore(view, profile)

                override fun paywallViewDidFailRestore(
                    view: AdaptyUIPaywallView,
                    error: AdaptyError
                ) = onDidFailRestore(view, error)

                override fun paywallViewDidFailRendering(
                    view: AdaptyUIPaywallView,
                    error: AdaptyError
                ) = onDidFailRendering(view, error)

                override fun paywallViewDidFailLoadingProducts(
                    view: AdaptyUIPaywallView,
                    error: AdaptyError
                ) = onDidFailLoadingProducts(view, error)

                override fun paywallViewDidFinishWebPaymentNavigation(
                    view: AdaptyUIPaywallView,
                    product: AdaptyPaywallProduct?,
                    error: AdaptyError?
                ) = onDidFinishWebPaymentNavigation(view, product, error)
            })
    }
    DisposableEffect(Unit) {
        onDispose {
            AdaptyUI.unregisterPaywallEventsListener(paywall.idForNativePlatformView)
        }
    }

    AdaptyUIPaywallPlatformView(
        paywall = paywall,
        modifier = modifier,
        customTags = customTags,
        customTimers = customTimers,
        customAssets = customAssets,
        productPurchaseParams = productPurchaseParams
    )
}

@Composable
internal expect fun AdaptyUIPaywallPlatformView(
    paywall: AdaptyPaywall,
    modifier: Modifier = Modifier,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
)