package com.adapty.exampleapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.adapty.exampleapp.AppLogger
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.ui.AdaptyUIPaywallPlatformView
import kmpadapty.example.composeapp.generated.resources.Res
import kmpadapty.example.composeapp.generated.resources.ic_close
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallNativeViewScreen(
    paywall: AdaptyPaywall,
    showToastEvents: Boolean,
    customTags: Map<String, String>? = null,
    customTimers: Map<String, LocalDateTime>? = null,
    customAssets: Map<String, AdaptyCustomAsset>? = null,
    productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>? = null,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text("Onboarding ${paywall.placement.id}") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateBack()
                    }) {
                        Icon(painterResource(Res.drawable.ic_close), contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            if (showToastEvents) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }

    ) {

        AdaptyUIPaywallPlatformView(
            modifier = Modifier.fillMaxSize(),
            paywall = paywall,
            customTags = customTags,
            customTimers = customTimers,
            customAssets = customAssets,
            productPurchaseParams = productPurchaseParams,
            onDidAppear = { view ->
                AppLogger.d("#Example# Platform View onDidAppear")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidAppear, View: $view")
                }
            },
            onDidDisappear = { view ->
                AppLogger.d("#Example# Platform View onDidDisappear")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidDisappear, View: $view")
                }
            },
            onDidPerformAction = { view, action ->
                AppLogger.d("#Example# Platform View onDidPerformAction: $action")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidPerformAction, Action: $action")
                }
                if (action is AdaptyUIAction.CloseAction) {
                    onNavigateBack()
                }
            },
            onDidSelectProduct = { view, productId ->
                AppLogger.d("#Example# Platform View onDidSelectProduct: $productId")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidSelectProduct, ProductId: $productId")
                }
            },
            onDidStartPurchase = { view, product ->
                AppLogger.d("#Example# Platform View onDidStartPurchase: $product")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidStartPurchase, Product: $product")
                }
            },
            onDidFinishPurchase = { view, product, result ->
                AppLogger.d("#Example# Platform View onDidFinishPurchase: $product, $result")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidFinishPurchase, Product: $product, Result: $result")
                }
            },
            onDidFailPurchase = { view, product, error ->
                AppLogger.d("#Example# Platform View onDidFailPurchase: $product, $error")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidFailPurchase, Product: $product, Error: ${error.message}")
                }
            },
            onDidStartRestore = { view ->
                AppLogger.d("#Example# Platform View onDidStartRestore")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidStartRestore, View: $view")
                }
            },
            onDidFinishRestore = { view, profile ->
                AppLogger.d("#Example# Platform View onDidFinishRestore: $profile")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidFinishRestore, Profile: $profile")
                }
            },
            onDidFailRestore = { view, error ->
                AppLogger.d("#Example# Platform View onDidFailRestore: $error")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidFailRestore, Error: ${error.message}")
                }
            },
            onDidFailRendering = { view, error ->
                AppLogger.d("#Example# Platform View onDidFailRendering: $error")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidFailRendering, Error: ${error.message}")
                }
            },
            onDidFailLoadingProducts = { view, error ->
                AppLogger.d("#Example# Platform View onDidFailLoadingProducts: $error")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidFailLoadingProducts, Error: ${error.message}")
                }
            },
            onDidFinishWebPaymentNavigation = { view, product, error ->
                AppLogger.d("#Example# Platform View onDidFinishWebPaymentNavigation: $product, $error")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Action: onDidFinishWebPaymentNavigation, Product: $product, Error: ${error?.message}")
                }
            }
        )
    }

}