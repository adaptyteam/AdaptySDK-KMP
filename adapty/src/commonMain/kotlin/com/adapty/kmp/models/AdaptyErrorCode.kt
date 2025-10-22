package com.adapty.kmp.models

/**
 * Enumerates all possible error codes returned by the Adapty SDK.
 *
 * Each [AdaptyError] includes a corresponding [AdaptyErrorCode] that helps identify
 * the source or type of failure, such as network issues, StoreKit problems,
 * or internal SDK errors.
 *
 * These values may come from:
 * - System StoreKit errors (for iOS and macOS)
 * - Platform or SDK-level issues
 * - Adapty backend errors
 *
 * You can use this to implement more granular error handling and user feedback.
 *
 * Example:
 * ```
 * when (error.code) {
 *     AdaptyErrorCode.PAYMENT_CANCELLED -> showMessage("Purchase cancelled.")
 *     AdaptyErrorCode.ITEM_UNAVAILABLE -> showMessage("This item is not available.")
 *     else -> logError(error)
 * }
 * ```
 *
 * @property value The raw integer value of the error, corresponding to platform or backend codes.
 *
 * @see AdaptyError
 */
public enum class AdaptyErrorCode(internal val value: Int) {

    //////////////////////////////
    /// System StoreKit codes. ///
    //////////////////////////////
    UNKNOWN(0),
    CLIENT_INVALID(1),
    PAYMENT_CANCELLED(2),
    PAYMENT_INVALID(3),
    PAYMENT_NOT_ALLOWED(4),
    ITEM_UNAVAILABLE(5),
    CLOUD_SERVICE_PERMISSION_DENIED(6),
    CLOUD_SERVICE_NETWORK_CONNECTION_FAILED(7),
    CLOUD_SERVICE_REVOKED(8),
    PRIVACY_ACKNOWLEDGEMENT_REQUIRED(9),
    UNAUTHORIZED_REQUEST_DATA(10),
    INVALID_OFFER_IDENTIFIER(11),
    INVALID_SIGNATURE(12),
    MISSING_OFFER_PARAMS(13),
    INVALID_OFFER_PRICE(14),

    /////////////////////////////
    /// Custom Android codes. ///
    /////////////////////////////
    ADAPTY_NOT_INITIALIZED(20),
    PRODUCT_NOT_FOUND(22),
    INVALID_JSON(23),
    CURRENT_SUBSCRIPTION_TO_UPDATE_NOT_FOUND_IN_HISTORY(24),
    PENDING_PURCHASE(25),
    BILLING_SERVICE_TIMEOUT(97),
    FEATURE_NOT_SUPPORTED(98),
    BILLING_SERVICE_DISCONNECTED(99),
    BILLING_SERVICE_UNAVAILABLE(102),
    BILLING_UNAVAILABLE(103),
    DEVELOPER_ERROR(105),
    BILLING_ERROR(106),
    ITEM_ALREADY_OWNED(107),
    ITEM_NOT_OWNED(108),

    //////////////////////////////
    /// Custom StoreKit codes. ///
    //////////////////////////////
    NO_PRODUCT_IDS_FOUND(1000),
    PRODUCT_REQUEST_FAILED(1002),
    CANT_MAKE_PAYMENTS(1003),
    NO_PURCHASES_TO_RESTORE(1004),
    CANT_READ_RECEIPT(1005),
    PRODUCT_PURCHASE_FAILED(1006),
    REFRESH_RECEIPT_FAILED(1010),
    RECEIVE_RESTORED_TRANSACTIONS_FAILED(1011),

    /////////////////////////////
    /// Custom network codes. ///
    /////////////////////////////

    AUTHENTICATION_ERROR(2002), // You need to be authenticated to perform requests.
    BAD_REQUEST(2003),
    SERVER_ERROR(2004), // Response code is 429 or 500s.
    NETWORK_FAILED(2005), // Network request failed
    DECODING_FAILED(2006),
    ENCODING_FAILED(2009),
    ANALYTICS_DISABLED(3000),
    WRONG_PARAMETER(3001),
    ACTIVATE_ONCE_ERROR(3005),
    PROFILE_WAS_CHANGED(3006),
    UNSUPPORTED_DATA(3007),
    FETCH_TIMEOUT_ERROR(3101),
    OPERATION_INTERRUPTED(9000),

    /////////////////////////////
    /////// Plugin codes. ///////
    /////////////////////////////

    EMPTY_RESULT(10001),
    INTERNAL_PLUGIN_ERROR(10002);
}
