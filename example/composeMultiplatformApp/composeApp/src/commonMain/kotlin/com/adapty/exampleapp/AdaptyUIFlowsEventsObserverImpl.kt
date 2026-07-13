package com.adapty.exampleapp

import androidx.compose.ui.platform.UriHandler
import com.adapty.kmp.AdaptyUIFlowsEventsObserver
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIFlowView
import com.adapty.kmp.models.getOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AdaptyUIFlowsEventsObserverImpl(
    private val uriHandler: UriHandler,
    private val uiCoroutineScope: CoroutineScope = MainScope(),
) :
    AdaptyUIFlowsEventsObserver {
    override fun flowViewDidFinishRestore(view: AdaptyUIFlowView, profile: AdaptyProfile) {
        AppLogger.d("Flow view did finish restore of view $view with profile $profile")
        uiCoroutineScope.launch {
            view.showDialog(
                title = "Success",
                content = "Purchases were successfully restored.",
                primaryActionTitle = "OK"
            )
            if (profile.accessLevels["premium"]?.isActive == true) {
                view.dismiss()
            }
        }
    }

    override fun flowViewDidReceiveError(view: AdaptyUIFlowView, error: AdaptyError) {
        AppLogger.e("Flow view did receive error of view $view with error $error")
        uiCoroutineScope.launch { view.dismiss() }
    }

    override fun flowViewDidPerformAction(view: AdaptyUIFlowView, action: AdaptyUIAction) {
        AppLogger.d("Flow view did perform action of view $view with action $action")
        uiCoroutineScope.launch {
            when (action) {
                AdaptyUIAction.CloseAction, AdaptyUIAction.AndroidSystemBackAction -> view.dismiss()
                is AdaptyUIAction.OpenUrlAction -> {
                    val selectedAction = view.showDialog(
                        title = "Open URL?",
                        content = action.url,
                        primaryActionTitle = "Cancel",
                        secondaryActionTitle = "OK"
                    ).getOrNull()

                    when (selectedAction) {
                        AdaptyUIDialogActionType.PRIMARY -> {
                            AppLogger.d("User chose primary action")
                        }

                        AdaptyUIDialogActionType.SECONDARY -> {
                            uriHandler.openUri(action.url)
                        }
                        else -> Unit
                    }
                }

                is AdaptyUIAction.CustomAction -> Unit
            }
        }
    }

    override fun flowViewDidSelectProduct(view: AdaptyUIFlowView, productId: String) {
        AppLogger.d("Flow view did select product of view $view with productId $productId")
    }

    override fun flowViewDidStartPurchase(view: AdaptyUIFlowView, product: AdaptyPaywallProduct) {
        AppLogger.d("Flow view did start purchase of view $view with product $product")
    }

    override fun flowViewDidFinishPurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        purchaseResult: AdaptyPurchaseResult
    ) {
        AppLogger.d("Flow view did finish purchase of view $view with product $product and purchaseResult $purchaseResult")
        when (purchaseResult) {
            is AdaptyPurchaseResult.Success -> {
                uiCoroutineScope.launch {
                    if (purchaseResult.profile.accessLevels["premium"]?.isActive == true) {
                        view.dismiss()
                    }
                }
            }

            AdaptyPurchaseResult.Pending -> Unit
            AdaptyPurchaseResult.UserCanceled -> Unit
        }
    }

    override fun flowViewDidFailPurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {
        AppLogger.e("Flow view did fail purchase of view $view with product $product and error $error")
    }

    override fun flowViewDidStartRestore(view: AdaptyUIFlowView) {
        AppLogger.d("Flow view did start restore of view $view")
    }

    override fun flowViewDidFailRestore(view: AdaptyUIFlowView, error: AdaptyError) {
        AppLogger.e("Flow view did fail restore of view $view with error $error")
        uiCoroutineScope.launch {
            view.showDialog(
                title = "Error",
                content = error.message,
                primaryActionTitle = "OK"
            )
        }
    }

    override fun flowViewDidFailLoadingProducts(view: AdaptyUIFlowView, error: AdaptyError) {
        AppLogger.e("Flow view did fail loading products of view $view with error $error")
    }

    override fun flowViewDidFinishWebPaymentNavigation(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct?,
        error: AdaptyError?
    ) {
        AppLogger.e("Flow view did finish web payment navigation of view $view with product $product and error $error")

    }

    override fun flowViewDidAppear(view: AdaptyUIFlowView) {
        AppLogger.d("Flow view did appear of view $view")
    }

    override fun flowViewDidDisappear(view: AdaptyUIFlowView) {
        AppLogger.d("Flow view did disappear of view $view")
    }
}