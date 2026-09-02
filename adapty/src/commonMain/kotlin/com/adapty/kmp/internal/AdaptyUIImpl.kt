@file:OptIn(AdaptyKMPInternal::class)
@file:Suppress("DEPRECATION") // internal impl of the deprecated onboarding API

package com.adapty.kmp.internal

import com.adapty.kmp.AdaptyUIContract
import com.adapty.kmp.AdaptyUIFlowsEventsObserver
import com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
import com.adapty.kmp.AdaptyUIObserverModeResolver
import com.adapty.kmp.AdaptyUISystemRequestsHandler
import com.adapty.kmp.internal.plugin.AdaptyPlugin
import com.adapty.kmp.internal.plugin.AdaptyPluginEventHandler
import com.adapty.kmp.internal.plugin.asAdaptyResult
import com.adapty.kmp.internal.plugin.awaitExecute
import com.adapty.kmp.internal.plugin.execute
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginMethod
import com.adapty.kmp.internal.plugin.request.AdaptyFlowAnswerPermissionRequest
import com.adapty.kmp.internal.plugin.request.AdaptyObserverEventRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIOpenUrlRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateOnboardingViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUICreateFlowViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDialogRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIDismissViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIPresentViewRequest
import com.adapty.kmp.internal.plugin.request.AdaptyUIShowDialogRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyCustomAssetRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyFlowRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyOnboardingRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyPurchaseParametersRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyUIIOSPresentationStyleRequest
import com.adapty.kmp.internal.plugin.request.asAdaptyWebPresentationRequest
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventDidFailWithErrorResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventDidFinishLoadingResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnAnalyticsActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnCloseActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnCustomActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnPaywallActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnboardingViewEventOnStateUpdatedActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewDidAskPermissionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewDidReceiveAnalyticEventResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewObserverDidInitiatePurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewObserverDidInitiateRestoreResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidAppearOrDisappearResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidFailLoadingProductsResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidFailPurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidReceiveErrorResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidFailRestorePurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidFinishWebPaymentNavigationResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidPurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidSelectProductResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidRestorePurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventWillPurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventDidUserActionResponse
import com.adapty.kmp.internal.plugin.response.AdaptyFlowViewEventWillRestorePurchaseResponse
import com.adapty.kmp.internal.plugin.response.AdaptyUIDialogActionTypeResponse
import com.adapty.kmp.internal.plugin.response.AdaptyUIOnboardingViewResponse
import com.adapty.kmp.internal.plugin.response.AdaptyUIFlowViewResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyError
import com.adapty.kmp.internal.plugin.response.asAdaptyOnboardingEvent
import com.adapty.kmp.internal.plugin.response.asAdaptyOnboardingsStateUpdatedParams
import com.adapty.kmp.internal.plugin.response.asAdaptyPaywallProduct
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.plugin.response.asAdaptyPurchaseResult
import com.adapty.kmp.internal.plugin.response.asAdaptyUIAction
import com.adapty.kmp.internal.plugin.response.asAdaptyUIDialogActionType
import com.adapty.kmp.internal.plugin.response.asAdaptyUIOnboardingMeta
import com.adapty.kmp.internal.plugin.response.asAdaptyUIFlowView
import com.adapty.kmp.internal.plugin.response.asAdaptyUIOnboardingView
import com.adapty.kmp.internal.plugin.response.asAdaptyUIPermission
import com.adapty.kmp.internal.utils.asAdaptyValidDateTimeFormat
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyProductIdentifier
import com.adapty.kmp.models.AdaptyPurchaseParameters
import com.adapty.kmp.models.AdaptyResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIDialogActionType
import com.adapty.kmp.models.AdaptyUIFlowView
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPermissionResult
import com.adapty.kmp.models.AdaptyWebPresentation
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

@Suppress("OVERRIDE_DEPRECATION")
internal class AdaptyUIImpl(
    private val adaptyPlugin: AdaptyPlugin,
    private val appMainScope: CoroutineScope = MainScope(),
    private val defaultDispatcher: CoroutineContext = Dispatchers.Default,
    private val mainDispatcher: CoroutineContext = Dispatchers.Main,
) : AdaptyUIContract {

    // Active default observers so a view "just works" out of the box (close dismisses, url opens,
    // etc.) even when the app sets no listener.
    private var flowsEventObserver: AdaptyUIFlowsEventsObserver =
        object : AdaptyUIFlowsEventsObserver {}
    private var onboardingsEventObserver: AdaptyUIOnboardingsEventsObserver =
        object : AdaptyUIOnboardingsEventsObserver {}
    private var nativeOnboardingViewsEventObserver: MutableMap<String, AdaptyUIOnboardingsEventsObserver> =
        mutableMapOf()
    // No default: permissions can only be requested by the app itself, and an unregistered
    // handler is answered by native at teardown (see AdaptyUISystemRequestsHandler).
    private var systemRequestsHandler: AdaptyUISystemRequestsHandler? = null

    // No default: in observer mode the app owns the purchase, so with no resolver there is nothing
    // to hand the purchase to (see AdaptyUIObserverModeResolver).
    private var observerModeResolver: AdaptyUIObserverModeResolver? = null
    private var nativeFlowViewsEventObserver: MutableMap<String, AdaptyUIFlowsEventsObserver> =
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


    override fun setOnboardingsEventsObserver(observer: AdaptyUIOnboardingsEventsObserver) {
        this.onboardingsEventObserver = observer
    }

    override fun setSystemRequestsHandler(handler: AdaptyUISystemRequestsHandler) {
        this.systemRequestsHandler = handler
    }

    override fun setObserverModeResolver(resolver: AdaptyUIObserverModeResolver) {
        this.observerModeResolver = resolver
    }

    override fun registerFlowEventsListener(
        observer: AdaptyUIFlowsEventsObserver,
        viewId: String
    ) {
        nativeFlowViewsEventObserver[viewId] = observer
    }

    override fun unregisterFlowEventsListener(viewId: String) {
        nativeFlowViewsEventObserver.remove(viewId)
    }

    override fun setFlowsEventsObserver(observer: AdaptyUIFlowsEventsObserver) {
        this.flowsEventObserver = observer
    }

    override fun requestAppReview() {
        adaptyPlugin.execute<Unit, Unit>(
            method = AdaptyPluginMethod.REQUEST_APP_REVIEW,
            request = Unit,
            onResult = {}
        )
    }

    override fun openWebUrl(url: String, openIn: AdaptyWebPresentation) {
        adaptyPlugin.execute<AdaptyUIOpenUrlRequest, Unit>(
            method = AdaptyPluginMethod.OPEN_URL,
            request = AdaptyUIOpenUrlRequest(
                url = url,
                openIn = openIn.asAdaptyWebPresentationRequest()
            ),
            onResult = {}
        )
    }

    override suspend fun createFlowView(
        flow: AdaptyFlow,
        locale: String?,
        loadTimeout: Duration?,
        preloadProducts: Boolean,
        customTags: Map<String, String>?,
        customTimers: Map<String, LocalDateTime>?,
        customAssets: Map<String, AdaptyCustomAsset>?,
        productPurchaseParams: Map<AdaptyProductIdentifier, AdaptyPurchaseParameters>?
    ): AdaptyResult<AdaptyUIFlowView> {
        return adaptyPlugin.awaitExecute<AdaptyUICreateFlowViewRequest, AdaptyUIFlowViewResponse>(
            method = AdaptyPluginMethod.CREATE_FLOW_VIEW,
            request = AdaptyUICreateFlowViewRequest(
                flow = flow.asAdaptyFlowRequest(),
                locale = locale,
                loadTimeOutInSeconds = loadTimeout?.inWholeMilliseconds?.toDouble()?.div(1000.0),
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
        ).asAdaptyResult { it.asAdaptyUIFlowView() }
    }

    override suspend fun presentFlowView(
        view: AdaptyUIFlowView,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle
    ): AdaptyResult<Unit> {
        return adaptyPlugin.awaitExecute<AdaptyUIPresentViewRequest, Boolean>(
            method = AdaptyPluginMethod.PRESENT_FLOW_VIEW,
            request = AdaptyUIPresentViewRequest(
                id = view.id,
                iosPresentationStyle = iosPresentationStyle.asAdaptyUIIOSPresentationStyleRequest()
            )
        ).asAdaptyResult { }
    }

    override suspend fun dismissFlowView(view: AdaptyUIFlowView): AdaptyResult<Unit> {
        return adaptyPlugin.awaitExecute<AdaptyUIDismissViewRequest, Unit>(
            method = AdaptyPluginMethod.DISMISS_FLOW_VIEW,
            request = AdaptyUIDismissViewRequest(id = view.id),
        ).asAdaptyResult { }
    }

    private fun answerPermission(eventId: String, granted: Boolean, detail: String?) {
        adaptyPlugin.execute<AdaptyFlowAnswerPermissionRequest, Unit>(
            method = AdaptyPluginMethod.FLOW_VIEW_DID_ANSWER_PERMISSION,
            request = AdaptyFlowAnswerPermissionRequest(
                eventId = eventId,
                status = if (granted) "granted" else "denied",
                detail = detail
            ),
            onResult = {}
        )
    }

    private fun reportObserverEvent(method: AdaptyPluginMethod, eventId: String) {
        adaptyPlugin.execute<AdaptyObserverEventRequest, Unit>(
            method = method,
            request = AdaptyObserverEventRequest(eventId = eventId),
            onResult = {}
        )
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

    override suspend fun createOnboardingView(
        onboarding: AdaptyOnboarding,
        externalUrlsPresentation: AdaptyWebPresentation
    ): AdaptyResult<AdaptyUIOnboardingView> {
        return adaptyPlugin.awaitExecute<AdaptyUICreateOnboardingViewRequest, AdaptyUIOnboardingViewResponse>(
            method = AdaptyPluginMethod.CREATE_ONBOARDING_VIEW,
            request = AdaptyUICreateOnboardingViewRequest(
                onboarding = onboarding.asAdaptyOnboardingRequest(),
                externalUrlsPresentation = externalUrlsPresentation.asAdaptyWebPresentationRequest()
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

    /** Global flow observer plus any per-view native flow observer registered for [viewId]. */
    private fun flowObservers(viewId: String): List<AdaptyUIFlowsEventsObserver> =
        listOfNotNull(flowsEventObserver, nativeFlowViewsEventObserver[viewId])

    private suspend fun onNewEventReceived(
        event: AdaptyPluginEvent,
        dataJsonString: String
    ) {
        when (event) {
            AdaptyPluginEvent.FLOW_VIEW_DID_PERFORM_ACTION -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidUserActionResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidPerformAction(it.view.asAdaptyUIFlowView(), it.action.asAdaptyUIAction())
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_APPEAR -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidAppearOrDisappearResponse> {
                    flowObservers(it.view.id).forEach { obs -> obs.flowViewDidAppear(it.view.asAdaptyUIFlowView()) }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_DISAPPEAR -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidAppearOrDisappearResponse> {
                    flowObservers(it.view.id).forEach { obs -> obs.flowViewDidDisappear(it.view.asAdaptyUIFlowView()) }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_SELECT_PRODUCT -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidSelectProductResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidSelectProduct(it.view.asAdaptyUIFlowView(), it.productId)
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_START_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventWillPurchaseResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidStartPurchase(it.view.asAdaptyUIFlowView(), it.product.asAdaptyPaywallProduct())
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidPurchaseResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidFinishPurchase(
                            it.view.asAdaptyUIFlowView(),
                            it.product.asAdaptyPaywallProduct(),
                            it.purchasedResult.asAdaptyPurchaseResult()
                        )
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidFailPurchaseResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidFailPurchase(
                            it.view.asAdaptyUIFlowView(),
                            it.product.asAdaptyPaywallProduct(),
                            it.error.asAdaptyError()
                        )
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_START_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventWillRestorePurchaseResponse> {
                    flowObservers(it.view.id).forEach { obs -> obs.flowViewDidStartRestore(it.view.asAdaptyUIFlowView()) }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidRestorePurchaseResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidFinishRestore(it.view.asAdaptyUIFlowView(), it.profile.asAdaptyProfile())
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidFailRestorePurchaseResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidFailRestore(it.view.asAdaptyUIFlowView(), it.error.asAdaptyError())
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_RECEIVE_ERROR -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidReceiveErrorResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidReceiveError(it.view.asAdaptyUIFlowView(), it.error.asAdaptyError())
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_FAIL_LOADING_PRODUCTS -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidFailLoadingProductsResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidFailLoadingProducts(it.view.asAdaptyUIFlowView(), it.error.asAdaptyError())
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidFinishWebPaymentNavigationResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidFinishWebPaymentNavigation(
                            it.view.asAdaptyUIFlowView(),
                            it.product?.asAdaptyPaywallProduct(),
                            it.error?.asAdaptyError()
                        )
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_ASK_PERMISSION -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewDidAskPermissionResponse> {
                    val handler = systemRequestsHandler ?: return@decodeJsonSafely
                    appMainScope.launch {
                        val result: AdaptyUIPermissionResult = handler.handlePermission(
                            view = it.view.asAdaptyUIFlowView(),
                            permission = it.permission.asAdaptyUIPermission(),
                            customArgs = it.customArgs,
                        )
                        answerPermission(it.eventId, result.isGranted, result.detail)
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_REQUEST_APP_REVIEW -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewEventDidAppearOrDisappearResponse> {
                    val handler = systemRequestsHandler
                    if (handler != null) {
                        appMainScope.launch {
                            handler.handleAppReviewRequest(view = it.view.asAdaptyUIFlowView())
                        }
                    } else {
                        // No registered handler -> trigger the native review prompt by default.
                        requestAppReview()
                    }
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_PURCHASE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewObserverDidInitiatePurchaseResponse> {
                    observerModeResolver?.observerModeDidInitiatePurchase(
                        view = it.view.asAdaptyUIFlowView(),
                        product = it.product.asAdaptyPaywallProduct(),
                        onStartPurchase = {
                            reportObserverEvent(
                                AdaptyPluginMethod.OBSERVER_PURCHASE_DID_START,
                                it.eventId
                            )
                        },
                        onFinishPurchase = {
                            reportObserverEvent(
                                AdaptyPluginMethod.OBSERVER_PURCHASE_DID_FINISH,
                                it.eventId
                            )
                        }
                    )
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_OBSERVER_DID_INITIATE_RESTORE -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewObserverDidInitiateRestoreResponse> {
                    observerModeResolver?.observerModeDidInitiateRestore(
                        view = it.view.asAdaptyUIFlowView(),
                        onStartRestore = {
                            reportObserverEvent(
                                AdaptyPluginMethod.OBSERVER_RESTORE_DID_START,
                                it.eventId
                            )
                        },
                        onFinishRestore = {
                            reportObserverEvent(
                                AdaptyPluginMethod.OBSERVER_RESTORE_DID_FINISH,
                                it.eventId
                            )
                        }
                    )
                }
            }

            AdaptyPluginEvent.FLOW_VIEW_DID_RECEIVE_ANALYTIC_EVENT -> {
                dataJsonString.decodeJsonSafely<AdaptyFlowViewDidReceiveAnalyticEventResponse> {
                    flowObservers(it.view.id).forEach { obs ->
                        obs.flowViewDidReceiveAnalyticEvent(
                            view = it.view.asAdaptyUIFlowView(),
                            name = it.name,
                            paramsJsonString = it.params?.toString() ?: "{}"
                        )
                    }
                }
            }

            //Onboarding events
            AdaptyPluginEvent.ONBOARDING_DID_FINISH_LOADING -> {
                dataJsonString.decodeJsonSafely<AdaptyOnboardingViewEventDidFinishLoadingResponse> {
                    onboardingsEventObserver.onboardingViewDidFinishLoading(
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
                    onboardingsEventObserver.onboardingViewDidFailWithError(
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
                    onboardingsEventObserver.onboardingViewOnAnalyticsEvent(
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
                    onboardingsEventObserver.onboardingViewOnCloseAction(
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
                    onboardingsEventObserver.onboardingViewOnCustomAction(
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
                    onboardingsEventObserver.onboardingViewOnPaywallAction(
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
                    onboardingsEventObserver.onboardingViewOnStateUpdatedAction(
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


    private suspend inline fun <reified Response> String?.decodeJsonSafely(
        crossinline onResult: suspend (Response) -> Unit
    ) =
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


