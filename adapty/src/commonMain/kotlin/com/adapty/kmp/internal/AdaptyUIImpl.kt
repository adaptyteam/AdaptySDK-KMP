package com.adapty.kmp.internal

import com.adapty.kmp.AdaptyUIContract
import com.adapty.kmp.AdaptyUIObserver
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.asAdaptyResult
import com.adapty.kmp.internal.plugin.awaitExecute
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.execute
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDismissViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIPresentViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIShowDialogRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallRequest
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidAppearOrDisappearResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidFailLoadingProductsResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidFailPurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidFailRenderingResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidFailRestorePurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidFinishWebPaymentNavigationResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidPurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidSelectProductResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidUserActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventWillRestorePurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyUIDialogActionTypeResponse
import com.adapty.kmp.internal.plugin.response.AdaptyUIViewResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyError
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallProduct
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.plugin.response.asAdaptyPurchaseResult
import com.adapty.kmp.internal.plugin.response.asAdaptyUIAction
import com.adapty.kmp.internal.plugin.response.asAdaptyUIDialogActionType
import com.adapty.kmp.internal.plugin.response.asAdaptyUIView
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.JvmName
import kotlin.time.Duration

internal class AdaptyUIImpl(
    private val adaptyPlugin: AdaptyPlugin,
    private val appMainScope: CoroutineScope = MainScope(),
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
    private val mainDispatcher: CoroutineContext = Dispatchers.Main,
) : AdaptyUIContract {

    private var observer: AdaptyUIObserver? = null
        @JvmName("setUIObserverFromJava")
        set(value) {
            appMainScope.launch {
                AdaptyPluginEventHandler.viewEventFlow
                    .catch {
                        logger.log("AdaptyUIImpl, onNewEventReceived, error: $it")
                    }
                    .onEach {
                        val (event, dataJsonString) = it
                        value?.onNewEventReceived(
                            event = event,
                            dataJsonString = dataJsonString
                        )
                    }
                    .collect()
            }
            field = value
        }

    override fun setObserver(observer: AdaptyUIObserver) {
        this.observer = observer
    }

    override suspend fun createPaywallView(
        paywall: AdaptyPaywall,
        loadTimeout: Duration?,
        preloadProducts: Boolean,
        customTags: Map<String, String>?,
        customTimers: Map<String, String>?, //TODO customer timers value set type Duration.
        androidPersonalizedOffers: Map<String, Boolean>?
    ): AdaptyUIView? {

        val result = adaptyPlugin.awaitExecute<AdaptyUICreateViewRequest, AdaptyUIViewResponse>(
            method = AdaptyPluginMethod.CREATE_VIEW,
            request = AdaptyUICreateViewRequest(
                paywall = paywall.asAdaptyPaywallRequest(),
                loadTimeOutInSeconds = loadTimeout?.inWholeSeconds,
                preloadProducts = preloadProducts,
                customTags = customTags,
                customTimers = customTimers,
                androidPersonalizedOffers = androidPersonalizedOffers

            )
        ).asAdaptyResult { it.asAdaptyUIView() }

        return (result as? AdaptyResult.Success)?.value

    }

    override fun presentPaywallView(view: AdaptyUIView) {
        adaptyPlugin.execute<AdaptyUIPresentViewRequest, Boolean>(
            method = AdaptyPluginMethod.PRESENT_VIEW,
            request = AdaptyUIPresentViewRequest(id = view.id),
            onResult = { result ->
                logger.log("AdaptyUIImpl, presentPaywallView, result: $result")
            }
        )
    }

    override fun dismissPaywallView(view: AdaptyUIView) {
        adaptyPlugin.execute<AdaptyUIDismissViewRequest, Unit>(
            method = AdaptyPluginMethod.DISMISS_VIEW,
            request = AdaptyUIDismissViewRequest(id = view.id),
            onResult = { result ->
                logger.log("AdaptyUIImpl, dismissPaywallView, result: $result")
            }
        )
    }

    override suspend fun showDialog(
        view: AdaptyUIView,
        title: String,
        content: String,
        primaryActionTitle: String,
        secondaryActionTitle: String?
    ): AdaptyUIDialogActionType {

        val dialog = AdaptyUIDialogRequest(
            title = title,
            content = content,
            defaultActionTitle = primaryActionTitle,
            secondaryActionTitle = secondaryActionTitle
        )

        val result =
            adaptyPlugin.awaitExecute<AdaptyUIShowDialogRequest, AdaptyUIDialogActionTypeResponse>(
                method = AdaptyPluginMethod.SHOW_DIALOG,
                request = AdaptyUIShowDialogRequest(
                    id = view.id,
                    configuration = dialog
                )
            ).asAdaptyResult { it.asAdaptyUIDialogActionType() }

        return (result as? AdaptyResult.Success)?.value ?: AdaptyUIDialogActionType.PRIMARY
    }


    private suspend fun AdaptyUIObserver?.onNewEventReceived(
        event: AdaptyPluginEvent,
        dataJsonString: String
    ) {
        if (this == null) return
        when (event) {
            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidUserActionResponse> {
                    paywallViewDidPerformAction(
                        view = it.view.asAdaptyUIView(),
                        action = it.action.asAdaptyUIAction()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_APPEAR -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidAppearOrDisappearResponse> {
                    paywallViewDidAppear(view = it.view.asAdaptyUIView())
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_DISAPPEAR -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidAppearOrDisappearResponse> {
                    paywallViewDidDisappear(view = it.view.asAdaptyUIView())
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_SYSTEM_BACK_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyUIViewResponse> {
                    paywallViewDidPerformAction(
                        view = it.asAdaptyUIView(),
                        action = AdaptyUIAction.AndroidSystemBackAction
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_SELECT_PRODUCT -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidSelectProductResponse> {
                    paywallViewDidSelectProduct(
                        view = it.view.asAdaptyUIView(),
                        productId = it.productId
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_START_PURCHASE -> {
                dataJsonString.decodeJsonSafely<com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventWillPurchaseResponse> {
                    paywallViewDidStartPurchase(
                        view = it.view.asAdaptyUIView(),
                        product = it.product.asAdaptyPaywallProduct()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidPurchaseResponse> {
                    paywallViewDidFinishPurchase(
                        view = it.view.asAdaptyUIView(),
                        product = it.product.asAdaptyPaywallProduct(),
                        purchaseResult = it.purchasedResult.asAdaptyPurchaseResult()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailPurchaseResponse> {
                    paywallViewDidFailPurchase(
                        view = it.view.asAdaptyUIView(),
                        product = it.product.asAdaptyPaywallProduct(),
                        error = it.error.asAdaptyError()
                    )
                }

            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_START_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventWillRestorePurchaseResponse> {
                    paywallViewDidStartRestore(
                        view = it.view.asAdaptyUIView()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_RESTORE -> {
                dataJsonString.decodeJsonSafely<com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidRestorePurchaseResponse> {
                    paywallViewDidFinishRestore(
                        view = it.view.asAdaptyUIView(),
                        profile = it.profile.asAdaptyProfile()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailRestorePurchaseResponse> {
                    paywallViewDidFailRestore(
                        view = it.view.asAdaptyUIView(),
                        error = it.error.asAdaptyError()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RENDERING -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailRenderingResponse> {
                    paywallViewDidFailRendering(
                        view = it.view.asAdaptyUIView(),
                        error = it.error.asAdaptyError()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_LOADING_PRODUCTS -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailLoadingProductsResponse> {
                    paywallViewDidFailLoadingProducts(
                        view = it.view.asAdaptyUIView(),
                        error = it.error.asAdaptyError()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFinishWebPaymentNavigationResponse> {
                    paywallViewDidFinishWebPaymentNavigation(
                        view = it.view.asAdaptyUIView(),
                        product = it.product?.asAdaptyPaywallProduct(),
                        error = it.error?.asAdaptyError()
                    )
                }
            }

            else -> Unit
        }
    }

    private suspend inline fun <reified Response> String?.decodeJsonSafely(crossinline onResult: (Response) -> Unit) =
        withContext(ioDispatcher) {
            try {
                if (this@decodeJsonSafely == null) return@withContext
                val response = this@decodeJsonSafely.decodeJsonString<Response>()
                if (response != null) {
                    withContext(mainDispatcher) { onResult(response) }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.log("AdaptyUIImpl, execute, error: $e")
            }
        }
}


