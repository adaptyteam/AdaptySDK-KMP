package com.adapty.kmp

import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIView

public interface AdaptyUIObserver {

    public fun paywallViewDidPerformAction(
        view: AdaptyUIView,
        action: AdaptyUIAction
    ) {
        when (action) {
            is AdaptyUIAction.CloseAction, is AdaptyUIAction.AndroidSystemBackAction -> view.dismiss()
            is AdaptyUIAction.OpenUrlAction -> openUrl(action.url)
            is AdaptyUIAction.CustomAction -> {}
        }
    }

    public fun paywallViewDidAppear(view: AdaptyUIView) {}
    public fun paywallViewDidDisappear(view: AdaptyUIView) {}

    public fun paywallViewDidSelectProduct(view: AdaptyUIView, productId: String) {}

    public fun paywallViewDidStartPurchase(view: AdaptyUIView, product: AdaptyPaywallProduct) {}

    public fun paywallViewDidFinishPurchase(
        view: AdaptyUIView,
        product: AdaptyPaywallProduct,
        purchaseResult: AdaptyPurchaseResult
    ) {
        if (purchaseResult !is AdaptyPurchaseResult.UserCanceled)
            view.dismiss()
    }

    public fun paywallViewDidFailPurchase(
        view: AdaptyUIView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {

    }

    public fun paywallViewDidStartRestore(view: AdaptyUIView) {}

    public fun paywallViewDidFinishRestore(view: AdaptyUIView, profile: AdaptyProfile)

    public fun paywallViewDidFailRestore(view: AdaptyUIView, error: AdaptyError) {}

    public fun paywallViewDidFailRendering(view: AdaptyUIView, error: AdaptyError)

    public fun paywallViewDidFailLoadingProducts(view: AdaptyUIView, error: AdaptyError) {}


    public fun paywallViewDidFinishWebPaymentNavigation(
        view: AdaptyUIView,
        product: AdaptyPaywallProduct? = null,
        error: AdaptyError? = null
    ) {
    }

}
