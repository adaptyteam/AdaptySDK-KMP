package com.adapty.kmp.internal.plugin.constants

internal enum class AdaptyPluginEvent(val eventName: String) {
    FLOW_VIEW_DID_PERFORM_ACTION("flow_view_did_perform_action"),
    FLOW_VIEW_DID_APPEAR("flow_view_did_appear"),
    FLOW_VIEW_DID_DISAPPEAR("flow_view_did_disappear"),
    FLOW_VIEW_DID_SELECT_PRODUCT("flow_view_did_select_product"),
    FLOW_VIEW_DID_START_PURCHASE("flow_view_did_start_purchase"),
    FLOW_VIEW_DID_FINISH_PURCHASE("flow_view_did_finish_purchase"),
    FLOW_VIEW_DID_FAIL_PURCHASE("flow_view_did_fail_purchase"),
    FLOW_VIEW_DID_START_RESTORE("flow_view_did_start_restore"),
    FLOW_VIEW_DID_FINISH_RESTORE("flow_view_did_finish_restore"),
    FLOW_VIEW_DID_FAIL_RESTORE("flow_view_did_fail_restore"),
    FLOW_VIEW_DID_RECEIVE_ERROR("flow_view_did_receive_error"),
    FLOW_VIEW_DID_FAIL_LOADING_PRODUCTS("flow_view_did_fail_loading_products"),
    FLOW_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION("flow_view_did_finish_web_payment_navigation"),
    FLOW_VIEW_DID_ASK_PERMISSION("flow_view_did_ask_permission"),
    FLOW_VIEW_DID_REQUEST_APP_REVIEW("flow_view_did_request_app_review"),
    FLOW_VIEW_OBSERVER_DID_INITIATE_PURCHASE("flow_view_observer_did_initiate_purchase"),
    FLOW_VIEW_OBSERVER_DID_INITIATE_RESTORE("flow_view_observer_did_initiate_restore"),
    FLOW_VIEW_DID_RECEIVE_ANALYTIC_EVENT("flow_view_did_receive_analytic_event"),

    DID_LOAD_LATEST_PROFILE("did_load_latest_profile"),

    // Onboarding events
    ONBOARDING_DID_FINISH_LOADING("onboarding_did_finish_loading"),
    ONBOARDING_DID_FAIL_WITH_ERROR("onboarding_did_fail_with_error"),
    ONBOARDING_ON_ANALYTICS_ACTION("onboarding_on_analytics_action"),
    ONBOARDING_ON_CLOSE_ACTION("onboarding_on_close_action"),
    ONBOARDING_ON_CUSTOM_ACTION("onboarding_on_custom_action"),
    ONBOARDING_ON_PAYWALL_ACTION("onboarding_on_paywall_action"),
    ONBOARDING_ON_STATE_UPDATED_ACTION("onboarding_on_state_updated_action"),

    ON_INSTALLATION_DETAILS_SUCCESS("on_installation_details_success"),
    ON_INSTALLATION_DETAILS_FAIL("on_installation_details_fail")
}