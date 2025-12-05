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

/**
 * Observes lifecycle events and user interactions within an [AdaptyUIPaywallView].
 *
 * Implement this interface to handle paywall events such as appearing, disappearing,
 * user actions, product selection, purchases, restores, rendering errors, and more.
 *
 * The default implementation provides convenient behavior such as:
 * - Automatically dismissing the paywall when the close button or system back button is pressed.
 * - Automatically dismissing the paywall after a successful purchase.
 *
 * You can override individual methods to customize paywall behavior in your app.
 *
 * Example:
 * ```
 * class MyPaywallObserver : AdaptyUIPaywallsEventsObserver {
 *     override fun paywallViewDidFinishPurchase(
 *         view: AdaptyUIPaywallView,
 *         product: AdaptyPaywallProduct,
 *         purchaseResult: AdaptyPurchaseResult
 *     ) {
 *         // Custom logic after purchase
 *     }
 * }
 * ```
 */
public interface AdaptyUIPaywallsEventsObserver {

    /**
     * The main [CoroutineScope] used for performing UI-related operations,
     * such as dismissing the paywall view.
     */
    public val mainUiScope: CoroutineScope
        get() = MainScope()

    /**
     * Called when a user performs an action on the paywall view.
     *
     * By default:
     * - The view is dismissed if the action is [AdaptyUIAction.CloseAction]
     *   or [AdaptyUIAction.AndroidSystemBackAction].
     * - [AdaptyUIAction.OpenUrlAction] opens the provided URL.
     *
     * Override this method to handle custom actions.
     *
     * @param view the [AdaptyUIPaywallView] where the event occurred.
     * @param action the [AdaptyUIAction] performed by the user.
     */
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

    /**
     * Called when the paywall view has been presented.
     *
     * @param view the [AdaptyUIPaywallView] that appeared.
     */
    public fun paywallViewDidAppear(view: AdaptyUIPaywallView) {}

    /**
     * Called when the paywall view has been dismissed.
     *
     * @param view the [AdaptyUIPaywallView] that disappeared.
     */
    public fun paywallViewDidDisappear(view: AdaptyUIPaywallView) {}

    /**
     * Called when a product is selected for purchase, either by the user or automatically.
     *
     * @param view the [AdaptyUIPaywallView] where the selection occurred.
     * @param productId the identifier of the selected product.
     */
    public fun paywallViewDidSelectProduct(view: AdaptyUIPaywallView, productId: String) {}

    /**
     * Called when the user initiates the purchase process.
     *
     * @param view the [AdaptyUIPaywallView] where the purchase started.
     * @param product the [AdaptyPaywallProduct] being purchased.
     */
    public fun paywallViewDidStartPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct
    ) {
    }

    /**
     * Called when a purchase is successfully completed or canceled.
     *
     * By default, if the result is not [AdaptyPurchaseResult.UserCanceled],
     * the paywall view will be dismissed.
     *
     * @param view the [AdaptyUIPaywallView] where the purchase occurred.
     * @param product the [AdaptyPaywallProduct] that was purchased.
     * @param purchaseResult the [AdaptyPurchaseResult] describing the outcome.
     */
    public fun paywallViewDidFinishPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct,
        purchaseResult: AdaptyPurchaseResult
    ) {
        if (purchaseResult !is AdaptyPurchaseResult.UserCanceled) {
            mainUiScope.launch { view.dismiss() }
        }
    }

    /**
     * Called when a purchase attempt fails.
     *
     * @param view the [AdaptyUIPaywallView] where the error occurred.
     * @param product the [AdaptyPaywallProduct] involved in the failed purchase.
     * @param error the [AdaptyError] representing the failure reason.
     */
    public fun paywallViewDidFailPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {

    }

    /**
     * Called when the user initiates a restore process.
     *
     * @param view the [AdaptyUIPaywallView] where the restore began.
     */
    public fun paywallViewDidStartRestore(view: AdaptyUIPaywallView) {}

    /**
     * Called when a restore operation completes successfully.
     *
     * Check the provided [AdaptyProfile] to confirm if the user now has access.
     * You can dismiss the paywall view if the profile indicates active access.
     *
     * @param view the [AdaptyUIPaywallView] where the restore occurred.
     * @param profile the updated [AdaptyProfile] of the user.
     */
    public fun paywallViewDidFinishRestore(view: AdaptyUIPaywallView, profile: AdaptyProfile)

    /**
     * Called when a restore operation fails.
     *
     * @param view the [AdaptyUIPaywallView] where the error occurred.
     * @param error the [AdaptyError] describing the failure.
     */
    public fun paywallViewDidFailRestore(view: AdaptyUIPaywallView, error: AdaptyError) {}

    /**
     * Called when an error occurs while rendering the paywall UI.
     *
     * @param view the [AdaptyUIPaywallView] where rendering failed.
     * @param error the [AdaptyError] representing the issue.
     */
    public fun paywallViewDidFailRendering(view: AdaptyUIPaywallView, error: AdaptyError)

    /**
     * Called when an error occurs while loading products for the paywall.
     *
     * @param view the [AdaptyUIPaywallView] where loading failed.
     * @param error the [AdaptyError] representing the issue.
     */
    public fun paywallViewDidFailLoadingProducts(view: AdaptyUIPaywallView, error: AdaptyError) {}


    /**
     * Called when web payment navigation completes.
     *
     * @param view the [AdaptyUIPaywallView] where navigation occurred.
     * @param product the [AdaptyPaywallProduct] related to the payment, if available.
     * @param error the [AdaptyError] describing the issue, or `null` if navigation was successful.
     */
    public fun paywallViewDidFinishWebPaymentNavigation(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct? = null,
        error: AdaptyError? = null
    ) {
    }

}
