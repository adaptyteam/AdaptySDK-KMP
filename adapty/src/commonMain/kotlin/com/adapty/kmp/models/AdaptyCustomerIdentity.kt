package com.adapty.kmp.models

/**
 * Represents a customer's identity used to associate in-app purchases
 * with their corresponding transactions on both iOS and Android platforms.
 *
 * @property iosAppAccountToken The UUID generated to associate a customer’s in-app purchase
 * with its resulting App Store transaction (used for iOS).
 * See [Apple documentation](https://developer.apple.com/documentation/appstoreserverapi/appaccounttoken) for more details.
 *
 * @property androidObfuscatedAccountId The obfuscated account identifier used on Android.
 * See [Android documentation](https://developer.android.com/google/play/billing/developer-payload#attribute) for more details.
 *
 * @constructor Creates an [AdaptyCustomerIdentity] with the provided [iosAppAccountToken] and [androidObfuscatedAccountId].
 */
public class AdaptyCustomerIdentity(
    public val iosAppAccountToken: String?,
    public val androidObfuscatedAccountId: String?
) {
    internal companion object {
        fun createIfNotEmpty(
            iosAppAccountToken: String?,
            androidObfuscatedAccountId: String?
        ): AdaptyCustomerIdentity? {
            return when {
                iosAppAccountToken.isNullOrEmpty() && androidObfuscatedAccountId.isNullOrEmpty() -> null
                else -> AdaptyCustomerIdentity(
                    iosAppAccountToken = iosAppAccountToken,
                    androidObfuscatedAccountId = androidObfuscatedAccountId
                )
            }

        }
    }
}
