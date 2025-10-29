@file:OptIn(AdaptyKMPInternal::class)

package com.adapty.kmp.internal

import com.adapty.kmp.AdaptyUIContract
import com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
import com.adapty.kmp.AdaptyUIPaywallsEventsObserver
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.asAdaptyResult
import com.adapty.kmp.internal.plugin.awaitExecute
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateOnboardingViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUICreatePaywallViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDismissViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIPresentViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIShowDialogRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyCustomAssetRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyOnboardingRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPaywallRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPurchaseParametersRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyUIIOSPresentationStyleRequest
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventDidFailWithErrorResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventDidFinishLoadingResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnAnalyticsActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnCloseActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnCustomActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnPaywallActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnStateUpdatedActionResponse
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
import com.adapty.kmp.internal.plugin.response.AdaptyUIOnboardingViewResponse
import com.adapty.kmp.internal.plugin.response.AdaptyUIPaywallViewResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyError
import com.adapty.kmp.internal.plugin.response.asAdaptyOnboardingEvent
import com.adapty.kmp.internal.plugin.response.asAdaptyOnboardingsStateUpdatedParams
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallProduct
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.plugin.response.asAdaptyPurchaseResult
import com.adapty.kmp.internal.plugin.response.asAdaptyUIAction
import com.adapty.kmp.internal.plugin.response.asAdaptyUIDialogActionType
import com.adapty.kmp.internal.plugin.response.asAdaptyUIOnboardingMeta
import com.adapty.kmp.internal.plugin.response.asAdaptyUIOnboardingView
import com.adapty.kmp.internal.plugin.response.asAdaptyUIView
import com.adapty.kmp.internal.utils.asAdaptyValidDateTimeFormat
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPaywallView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

internal class AdaptyUIImpl(
    private val adaptyPlugin: AdaptyPlugin,
    private val appMainScope: CoroutineScope = MainScope(),
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    private val mainDispatcher: CoroutineContext = Dispatchers.Main,
) : AdaptyUIContract {

    private var paywallsEventObserver: AdaptyUIPaywallsEventsObserver? = null
    private var onboardingsEventObserver: AdaptyUIOnboardingsEventsObserver? = null
    private var nativeOnboardingViewsEventObserver: MutableMap<String, AdaptyUIOnboardingsEventsObserver> =
        mutableMapOf()
    private var nativePaywallViewsEventObserver: MutableMap<String, AdaptyUIPaywallsEventsObserver> =
        mutableMapOf()
    private var eventsListenerJob: Job? = null


    init {
        listerForEvents()
    }

    private fun listerForEvents() {
        eventsListenerJob?.cancel()
        eventsListenerJob = appMainScope.launch {
            AdaptyPluginEventHandler.viewEventFlow
                .catch { logger.log("AdaptyUIImpl, onNewEventReceived, error: $it") }
                .collect { (event, dataJsonString) ->
                    onNewEventReceived(event, dataJsonString)
                }
        }
    }

    override fun registerOnboardingEventsListener(
        observer: AdaptyUIOnboardingsEventsObserver,
        viewId: String
    ) {
        nativeOnboardingViewsEventObserver[viewId] = observer
    }

    override fun unregisterOnboardingEventsListener(viewId: String) {
        nativeOnboardingViewsEventObserver.remove(viewId)
    }

    override fun registerPaywallEventsListener(
        observer: AdaptyUIPaywallsEventsObserver,
        viewId: String
    ) {
        nativePaywallViewsEventObserver[viewId] = observer
    }

    override fun unregisterPaywallEventsListener(viewId: String) {
        nativePaywallViewsEventObserver.remove(viewId)
    }

    override fun setPaywallsEventsObserver(observer: AdaptyUIPaywallsEventsObserver) {
        this.paywallsEventObserver = observer
    }


    override fun setOnboardingsEventsObserver(observer: AdaptyUIOnboardingsEventsObserver) {
        this.onboardingsEventObserver = observer
    }

    override suspend fun createPaywallView(
        paywall: AdaptyPaywall,
        loadTimeout: Duration?,
        preloadProducts: Boolean,
        customTags: Map<String, String>?,
        customTimers: Map<String, LocalDateTime>?,
        customAssets: Map<String, AdaptyCustomAsset>?,
        productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?
    ): AdaptyResult<AdaptyUIPaywallView> {
        return adaptyPlugin.awaitExecute<AdaptyUICreatePaywallViewRequest, AdaptyUIPaywallViewResponse>(
            method = AdaptyPluginMethod.CREATE_PAYWALL_VIEW,
            request = AdaptyUICreatePaywallViewRequest(
                paywall = paywall.asAdaptyPaywallRequest(),
                loadTimeOutInSeconds = loadTimeout?.inWholeSeconds,
                preloadProducts = preloadProducts,
                customTags = customTags,
                customTimers = customTimers?.asAdaptyValidDateTimeFormat(),
                productPurchaseParameters = productPurchaseParams?.map { (key, value) ->
                    key.adaptyProductId to value.asAdaptyPurchaseParametersRequest()
                }?.toMap(),
                customAssets = customAssets?.map { (key, value) ->
                    value.asAdaptyCustomAssetRequest(key)
                }
            )
        ).asAdaptyResult { it.asAdaptyUIView() }
    }

    override suspend fun presentPaywallView(
        view: AdaptyUIPaywallView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle
    ): AdaptyResult<Unit> {
        return adaptyPlugin.awaitExecute<AdaptyUIPresentViewRequest, Boolean>(
            method = AdaptyPluginMethod.PRESENT_PAYWALL_VIEW,
            request = AdaptyUIPresentViewRequest(
                id = view.id,
                iosPresentationStyle = iosPresentationStyle.asAdaptyUIIOSPresentationStyleRequest()
            )
        ).asAdaptyResult { }
    }

    override suspend fun dismissPaywallView(view: AdaptyUIPaywallView): AdaptyResult<Unit> {
        return adaptyPlugin.awaitExecute<AdaptyUIDismissViewRequest, Unit>(
            method = AdaptyPluginMethod.DISMISS_PAYWALL_VIEW,
            request = AdaptyUIDismissViewRequest(id = view.id),
        ).asAdaptyResult { }
    }

    override suspend fun showDialog(
        viewId: String,
        title: String,
        content: String,
        primaryActionTitle: String,
        secondaryActionTitle: String?
    ): AdaptyResult<AdaptyUIDialogActionType> {

        val dialog = AdaptyUIDialogRequest(
            title = title,
            content = content,
            defaultActionTitle = primaryActionTitle,
            secondaryActionTitle = secondaryActionTitle
        )

        return adaptyPlugin.awaitExecute<AdaptyUIShowDialogRequest, AdaptyUIDialogActionTypeResponse>(
            method = AdaptyPluginMethod.SHOW_DIALOG,
            request = AdaptyUIShowDialogRequest(
                id = viewId,
                configuration = dialog
            )
        ).asAdaptyResult { it.asAdaptyUIDialogActionType() }
    }

    override suspend fun createOnboardingView(onboarding: AdaptyOnboarding): AdaptyResult<AdaptyUIOnboardingView> {
        return adaptyPlugin.awaitExecute<AdaptyUICreateOnboardingViewRequest, AdaptyUIOnboardingViewResponse>(
            method = AdaptyPluginMethod.CREATE_ONBOARDING_VIEW,
            request = AdaptyUICreateOnboardingViewRequest(
                onboarding = onboarding.asAdaptyOnboardingRequest()
            )
        ).asAdaptyResult { it.asAdaptyUIOnboardingView() }
    }

    override suspend fun presentOnboardingView(
        view: AdaptyUIOnboardingView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle
    ): AdaptyResult<Unit> {
        return adaptyPlugin.awaitExecute<AdaptyUIPresentViewRequest, Boolean>(
            method = AdaptyPluginMethod.PRESENT_ONBOARDING_VIEW,
            request = AdaptyUIPresentViewRequest(
                id = view.id,
                iosPresentationStyle = iosPresentationStyle.asAdaptyUIIOSPresentationStyleRequest()
            )
        ).asAdaptyResult { }
    }

    override suspend fun dismissOnboardingView(view: AdaptyUIOnboardingView): AdaptyResult<Unit> {
        return adaptyPlugin.awaitExecute<AdaptyUIDismissViewRequest, Unit>(
            method = AdaptyPluginMethod.DISMISS_ONBOARDING_VIEW,
            request = AdaptyUIDismissViewRequest(id = view.id)
        ).asAdaptyResult { }
    }

    private suspend fun onNewEventReceived(
        event: AdaptyPluginEvent,
        dataJsonString: String
    ) {
        when (event) {
            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidUserActionResponse> {
                    paywallsEventObserver?.paywallViewDidPerformAction(
                        view = it.view.asAdaptyUIView(),
                        action = it.action.asAdaptyUIAction()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_APPEAR -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidAppearOrDisappearResponse> {
                    paywallsEventObserver?.paywallViewDidAppear(view = it.view.asAdaptyUIView())
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_DISAPPEAR -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidAppearOrDisappearResponse> {
                    paywallsEventObserver?.paywallViewDidDisappear(view = it.view.asAdaptyUIView())
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_PERFORM_SYSTEM_BACK_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyUIPaywallViewResponse> {
                    paywallsEventObserver?.paywallViewDidPerformAction(
                        view = it.asAdaptyUIView(),
                        action = AdaptyUIAction.AndroidSystemBackAction
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_SELECT_PRODUCT -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidSelectProductResponse> {
                    paywallsEventObserver?.paywallViewDidSelectProduct(
                        view = it.view.asAdaptyUIView(),
                        productId = it.productId
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_START_PURCHASE -> {
                dataJsonString.decodeJsonSafely<com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventWillPurchaseResponse> {
                    paywallsEventObserver?.paywallViewDidStartPurchase(
                        view = it.view.asAdaptyUIView(),
                        product = it.product.asAdaptyPaywallProduct()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidPurchaseResponse> {
                    paywallsEventObserver?.paywallViewDidFinishPurchase(
                        view = it.view.asAdaptyUIView(),
                        product = it.product.asAdaptyPaywallProduct(),
                        purchaseResult = it.purchasedResult.asAdaptyPurchaseResult()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailPurchaseResponse> {
                    paywallsEventObserver?.paywallViewDidFailPurchase(
                        view = it.view.asAdaptyUIView(),
                        product = it.product.asAdaptyPaywallProduct(),
                        error = it.error.asAdaptyError()
                    )
                }

            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_START_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventWillRestorePurchaseResponse> {
                    paywallsEventObserver?.paywallViewDidStartRestore(
                        view = it.view.asAdaptyUIView()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_RESTORE -> {
                dataJsonString.decodeJsonSafely<com.adapty.kmp.internal.plugin.response.AdaptyPaywallViewEventDidRestorePurchaseResponse> {
                    paywallsEventObserver?.paywallViewDidFinishRestore(
                        view = it.view.asAdaptyUIView(),
                        profile = it.profile.asAdaptyProfile()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailRestorePurchaseResponse> {
                    paywallsEventObserver?.paywallViewDidFailRestore(
                        view = it.view.asAdaptyUIView(),
                        error = it.error.asAdaptyError()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_RENDERING -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailRenderingResponse> {
                    paywallsEventObserver?.paywallViewDidFailRendering(
                        view = it.view.asAdaptyUIView(),
                        error = it.error.asAdaptyError()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FAIL_LOADING_PRODUCTS -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFailLoadingProductsResponse> {
                    paywallsEventObserver?.paywallViewDidFailLoadingProducts(
                        view = it.view.asAdaptyUIView(),
                        error = it.error.asAdaptyError()
                    )
                }
            }

            AdaptyPluginEvent.PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION -> {
                dataJsonString.decodeJsonSafely<AdaptyPaywallViewEventDidFinishWebPaymentNavigationResponse> {
                    paywallsEventObserver?.paywallViewDidFinishWebPaymentNavigation(
                        view = it.view.asAdaptyUIView(),
                        product = it.product?.asAdaptyPaywallProduct(),
                        error = it.error?.asAdaptyError()
                    )
                }
            }

            //Onboarding events
            AdaptyPluginEvent.ONBOARDING_DID_FINISH_LOADING -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventDidFinishLoadingResponse> {
                    onboardingsEventObserver?.onboardingViewDidFinishLoading(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                    )
                    nativeOnboardingViewsEventObserver[it.view.id]?.onboardingViewDidFinishLoading(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                    )
                }
            }

            AdaptyPluginEvent.ONBOARDING_DID_FAIL_WITH_ERROR -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventDidFailWithErrorResponse> {
                    onboardingsEventObserver?.onboardingViewDidFailWithError(
                        view = it.view.asAdaptyUIOnboardingView(),
                        error = it.error.asAdaptyError(),
                    )
                    nativeOnboardingViewsEventObserver[it.view.id]?.onboardingViewDidFailWithError(
                        view = it.view.asAdaptyUIOnboardingView(),
                        error = it.error.asAdaptyError(),
                    )
                }
            }

            AdaptyPluginEvent.ONBOARDING_ON_ANALYTICS_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventOnAnalyticsActionResponse> {
                    onboardingsEventObserver?.onboardingViewOnAnalyticsEvent(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        event = it.event.asAdaptyOnboardingEvent()
                    )
                    nativeOnboardingViewsEventObserver[it.view.id]?.onboardingViewOnAnalyticsEvent(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        event = it.event.asAdaptyOnboardingEvent()
                    )
                }
            }

            AdaptyPluginEvent.ONBOARDING_ON_CLOSE_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventOnCloseActionResponse> {
                    onboardingsEventObserver?.onboardingViewOnCloseAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        actionId = it.actionId
                    )
                    nativeOnboardingViewsEventObserver[it.view.id]?.onboardingViewOnCloseAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        actionId = it.actionId
                    )
                }
            }

            AdaptyPluginEvent.ONBOARDING_ON_CUSTOM_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventOnCustomActionResponse> {
                    onboardingsEventObserver?.onboardingViewOnCustomAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        actionId = it.actionId
                    )
                    nativeOnboardingViewsEventObserver[it.view.id]?.onboardingViewOnCustomAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        actionId = it.actionId
                    )

                }
            }

            AdaptyPluginEvent.ONBOARDING_ON_PAYWALL_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventOnPaywallActionResponse> {
                    onboardingsEventObserver?.onboardingViewOnPaywallAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        actionId = it.actionId
                    )
                    nativeOnboardingViewsEventObserver[it.view.id]?.onboardingViewOnPaywallAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        actionId = it.actionId
                    )
                }
            }

            AdaptyPluginEvent.ONBOARDING_ON_STATE_UPDATED_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventOnStateUpdatedActionResponse> {
                    onboardingsEventObserver?.onboardingViewOnStateUpdatedAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        params = it.action.asAdaptyOnboardingsStateUpdatedParams(),
                        elementId = it.action.elementId
                    )
                    nativeOnboardingViewsEventObserver[it.view.id]?.onboardingViewOnStateUpdatedAction(
                        view = it.view.asAdaptyUIOnboardingView(),
                        meta = it.meta.asAdaptyUIOnboardingMeta(),
                        params = it.action.asAdaptyOnboardingsStateUpdatedParams(),
                        elementId = it.action.elementId
                    )
                }
            }

            else -> Unit
        }
    }


    private suspend inline fun <reified Response> String?.decodeJsonSafely(crossinline onResult: (Response) -> Unit) =
        withContext(defaultDispatcher) {
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


