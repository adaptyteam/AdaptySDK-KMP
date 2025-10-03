package com.adapty.kmp.internal.plugin.constants

internal enum class AdaptyPluginEvent(val eventName: String) {
    PAYWALL_VIEW_DID_PERFORM_ACTION("paywall_view_did_perform_action"),
    PAYWALL_VIEW_DID_APPEAR("paywall_view_did_appear"),
    PAYWALL_VIEW_DID_DISAPPEAR("paywall_view_did_disappear"),
    PAYWALL_VIEW_DID_PERFORM_SYSTEM_BACK_ACTION("paywall_view_did_perform_system_back_action"),
    PAYWALL_VIEW_DID_SELECT_PRODUCT("paywall_view_did_select_product"),
    PAYWALL_VIEW_DID_START_PURCHASE("paywall_view_did_start_purchase"),
    PAYWALL_VIEW_DID_FINISH_PURCHASE("paywall_view_did_finish_purchase"),
    PAYWALL_VIEW_DID_FAIL_PURCHASE("paywall_view_did_fail_purchase"),
    PAYWALL_VIEW_DID_START_RESTORE("paywall_view_did_start_restore"),
    PAYWALL_VIEW_DID_FINISH_RESTORE("paywall_view_did_finish_restore"),
    PAYWALL_VIEW_DID_FAIL_RESTORE("paywall_view_did_fail_restore"),
    PAYWALL_VIEW_DID_FAIL_RENDERING("paywall_view_did_fail_rendering"),
    PAYWALL_VIEW_DID_FAIL_LOADING_PRODUCTS("paywall_view_did_fail_loading_products"),
    PAYWALL_VIEW_DID_FINISH_WEB_PAYMENT_NAVIGATION("paywall_view_did_finish_web_payment_navigation"),
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