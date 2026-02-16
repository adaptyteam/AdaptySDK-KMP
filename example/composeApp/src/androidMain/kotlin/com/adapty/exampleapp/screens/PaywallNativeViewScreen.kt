@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.adapty.exampleapp.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelStoreOwner
import com.adapty.internal.crossplatform.ui.Dependencies.safeInject
import com.adapty.internal.crossplatform.ui.PaywallUiManager
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.AdaptyUIPaywallsEventsObserver
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.request.createPaywallViewRequestJsonString
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
import com.adapty.ui.AdaptyPaywallView
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class, AdaptyKMPInternal::class)
@Composable
fun PaywallNativeViewScreen(
    paywall: AdaptyPaywall,
    showToastEvents: Boolean,
    modifier: Modifier = Modifier,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
    onNavigateBack: () -> Unit,
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
    val snackbarHostState = remember { SnackbarHostState() }

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

    Scaffold(
        modifier = modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text("Paywall ${paywall.placement.id}") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateBack()
                    }) {
                        Icon(
                            painterResource(com.adapty.exampleapp.R.drawable.ic_close),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (showToastEvents) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }

    ) { paddingValues ->

        //TODO Check adapty-ui module for implementation
        val viewModelStoreOwner = LocalActivity.current as? ViewModelStoreOwner ?: return@Scaffold
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

        AndroidView(modifier = modifier.padding(paddingValues), factory = { paywallView })
        DisposableEffect(Unit) {
            onDispose {
                paywallUiManager?.clearPaywallView(paywallView)
            }
        }
    }

}