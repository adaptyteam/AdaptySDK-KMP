package com.adapty.kmp

import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIPaywallView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

public interface AdaptyUIPaywallsEventsObserver {
    public val mainUiScope: CoroutineScope
        get() = MainScope()

    public fun paywallViewDidPerformAction(
        view: AdaptyUIPaywallView,
        action: AdaptyUIAction
    ) {
        when (action) {
            is AdaptyUIAction.CloseAction, is AdaptyUIAction.AndroidSystemBackAction -> {
                mainUiScope.launch { view.dismiss() }
            }

            is AdaptyUIAction.OpenUrlAction -> openUrl(action.url)
            is AdaptyUIAction.CustomAction -> {}
        }
    }

    public fun paywallViewDidAppear(view: AdaptyUIPaywallView) {}
    public fun paywallViewDidDisappear(view: AdaptyUIPaywallView) {}

    public fun paywallViewDidSelectProduct(view: AdaptyUIPaywallView, productId: String) {}

    public fun paywallViewDidStartPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct
    ) {
    }

    public fun paywallViewDidFinishPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct,
        purchaseResult: AdaptyPurchaseResult
    ) {
        if (purchaseResult !is AdaptyPurchaseResult.UserCanceled) {
            mainUiScope.launch { view.dismiss() }
        }
    }

    public fun paywallViewDidFailPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {

    }

    public fun paywallViewDidStartRestore(view: AdaptyUIPaywallView) {}

    public fun paywallViewDidFinishRestore(view: AdaptyUIPaywallView, profile: AdaptyProfile)

    public fun paywallViewDidFailRestore(view: AdaptyUIPaywallView, error: AdaptyError) {}

    public fun paywallViewDidFailRendering(view: AdaptyUIPaywallView, error: AdaptyError)

    public fun paywallViewDidFailLoadingProducts(view: AdaptyUIPaywallView, error: AdaptyError) {}


    public fun paywallViewDidFinishWebPaymentNavigation(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct? = null,
        error: AdaptyError? = null
    ) {
    }

}
