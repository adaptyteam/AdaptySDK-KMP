package com.adapty.kmp.errors

import com.adapty.errors.AdaptyErrorCode as AdaptyErrorCodeAndroid

internal fun AdaptyErrorCodeAndroid.asAdaptyErrorCode(): AdaptyErrorCode {
    return when (this) {
        AdaptyErrorCodeAndroid.UNKNOWN -> AdaptyErrorCode.UNKNOWN
        AdaptyErrorCodeAndroid.ITEM_UNAVAILABLE -> AdaptyErrorCode.ITEM_UNAVAILABLE
        AdaptyErrorCodeAndroid.ADAPTY_NOT_INITIALIZED -> AdaptyErrorCode.ADAPTY_NOT_INITIALIZED
        AdaptyErrorCodeAndroid.PRODUCT_NOT_FOUND -> AdaptyErrorCode.PRODUCT_NOT_FOUND
        AdaptyErrorCodeAndroid.CURRENT_SUBSCRIPTION_TO_UPDATE_NOT_FOUND_IN_HISTORY -> AdaptyErrorCode.CURRENT_SUBSCRIPTION_TO_UPDATE_NOT_FOUND_IN_HISTORY
        AdaptyErrorCodeAndroid.BILLING_SERVICE_TIMEOUT -> AdaptyErrorCode.BILLING_SERVICE_TIMEOUT
        AdaptyErrorCodeAndroid.FEATURE_NOT_SUPPORTED -> AdaptyErrorCode.FEATURE_NOT_SUPPORTED
        AdaptyErrorCodeAndroid.BILLING_SERVICE_DISCONNECTED -> AdaptyErrorCode.BILLING_SERVICE_DISCONNECTED
        AdaptyErrorCodeAndroid.BILLING_SERVICE_UNAVAILABLE -> AdaptyErrorCode.BILLING_SERVICE_UNAVAILABLE
        AdaptyErrorCodeAndroid.BILLING_UNAVAILABLE -> AdaptyErrorCode.BILLING_UNAVAILABLE
        AdaptyErrorCodeAndroid.DEVELOPER_ERROR -> AdaptyErrorCode.DEVELOPER_ERROR
        AdaptyErrorCodeAndroid.BILLING_ERROR -> AdaptyErrorCode.BILLING_ERROR
        AdaptyErrorCodeAndroid.ITEM_ALREADY_OWNED -> AdaptyErrorCode.ITEM_ALREADY_OWNED
        AdaptyErrorCodeAndroid.ITEM_NOT_OWNED -> AdaptyErrorCode.ITEM_NOT_OWNED
        AdaptyErrorCodeAndroid.BILLING_NETWORK_ERROR -> AdaptyErrorCode.BILLING_NETWORK_ERROR
        AdaptyErrorCodeAndroid.NO_PRODUCT_IDS_FOUND -> AdaptyErrorCode.NO_PRODUCT_IDS_FOUND
        AdaptyErrorCodeAndroid.NO_PURCHASES_TO_RESTORE -> AdaptyErrorCode.NO_PURCHASES_TO_RESTORE
        AdaptyErrorCodeAndroid.AUTHENTICATION_ERROR -> AdaptyErrorCode.AUTHENTICATION_ERROR
        AdaptyErrorCodeAndroid.BAD_REQUEST -> AdaptyErrorCode.BAD_REQUEST
        AdaptyErrorCodeAndroid.SERVER_ERROR -> AdaptyErrorCode.SERVER_ERROR
        AdaptyErrorCodeAndroid.REQUEST_FAILED -> AdaptyErrorCode.REQUEST_FAILED
        AdaptyErrorCodeAndroid.DECODING_FAILED -> AdaptyErrorCode.DECODING_FAILED
        AdaptyErrorCodeAndroid.ANALYTICS_DISABLED -> AdaptyErrorCode.ANALYTICS_DISABLED
        AdaptyErrorCodeAndroid.WRONG_PARAMETER -> AdaptyErrorCode.WRONG_PARAMETER
        com.adapty.errors.AdaptyErrorCode.PROFILE_WAS_CHANGED -> AdaptyErrorCode.PROFILE_WAS_CHANGED
    }
}