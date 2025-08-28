package com.adapty.exampleapp

import androidx.compose.ui.platform.UriHandler
import com.adapty.kmp.AdaptyUIObserver
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AdaptyUIObserverImpl(
    private val uriHandler: UriHandler,
    private val uiCoroutineScope: CoroutineScope = MainScope(),
) :
    AdaptyUIObserver {
    override fun paywallViewDidFinishRestore(view: AdaptyUIView, profile: AdaptyProfile) {
        AppLogger.d("Paywall view did finish restore of view $view with profile $profile")
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

    override fun paywallViewDidFailRendering(view: AdaptyUIView, error: AdaptyError) {
        AppLogger.e("Paywall view did fail rendering of view $view with error $error")
    }

    override fun paywallViewDidPerformAction(view: AdaptyUIView, action: AdaptyUIAction) {
        AppLogger.d("Paywall view did perform action of view $view with action $action")
        uiCoroutineScope.launch {
            when (action) {
                AdaptyUIAction.CloseAction, AdaptyUIAction.AndroidSystemBackAction -> view.dismiss()
                is AdaptyUIAction.OpenUrlAction -> {
                    val selectedAction = view.showDialog(
                        title = "Open URL?",
                        content = action.url,
                        primaryActionTitle = "Cancel",
                        secondaryActionTitle = "OK"
                    )

                    when (selectedAction) {
                        AdaptyUIDialogActionType.PRIMARY -> {
                            AppLogger.d("User chose primary action")
                        }

                        AdaptyUIDialogActionType.SECONDARY -> {
                            uriHandler.openUri(action.url)
                        }
                    }
                }

                is AdaptyUIAction.CustomAction -> Unit
            }
        }
    }

    override fun paywallViewDidSelectProduct(view: AdaptyUIView, productId: String) {
        AppLogger.d("Paywall view did select product of view $view with productId $productId")
    }

    override fun paywallViewDidStartPurchase(view: AdaptyUIView, product: AdaptyPaywallProduct) {
        AppLogger.d("Paywall view did start purchase of view $view with product $product")
    }

    override fun paywallViewDidFinishPurchase(
        view: AdaptyUIView,
        product: AdaptyPaywallProduct,
        purchaseResult: AdaptyPurchaseResult
    ) {
        AppLogger.d("Paywall view did finish purchase of view $view with product $product and purchaseResult $purchaseResult")
        when (purchaseResult) {
            is AdaptyPurchaseResult.Success -> {
                if (purchaseResult.profile.accessLevels["premium"]?.isActive == true) {
                    view.dismiss()
                }
            }

            AdaptyPurchaseResult.Pending -> Unit
            AdaptyPurchaseResult.UserCanceled -> Unit
        }
    }

    override fun paywallViewDidFailPurchase(
        view: AdaptyUIView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {
        AppLogger.e("Paywall view did fail purchase of view $view with product $product and error $error")
    }

    override fun paywallViewDidStartRestore(view: AdaptyUIView) {
        AppLogger.d("Paywall view did start restore of view $view")
    }

    override fun paywallViewDidFailRestore(view: AdaptyUIView, error: AdaptyError) {
        AppLogger.e("Paywall view did fail restore of view $view with error $error")
        uiCoroutineScope.launch {
            view.showDialog(
                title = "Error",
                content = error.message,
                primaryActionTitle = "OK"
            )
        }
    }

    override fun paywallViewDidFailLoadingProducts(view: AdaptyUIView, error: AdaptyError) {
        AppLogger.e("Paywall view did fail loading products of view $view with error $error")
    }

    override fun paywallViewDidFinishWebPaymentNavigation(
        view: AdaptyUIView,
        product: AdaptyPaywallProduct?,
        error: AdaptyError?
    ) {
        AppLogger.e("Paywall view did finish web payment navigation of view $view with product $product and error $error")

    }
}