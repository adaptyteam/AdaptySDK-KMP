package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.internal.plugin.request.AdaptyCustomAttributesRequestResponse
import com.adapty.kmp.internal.plugin.request.toTypedMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyProfileResponse(
    @SerialName("profile_id")
    val profileId: String,

    @SerialName("customer_user_id")
    val customerUserId: String? = null,

    @SerialName("segment_hash")
    val segmentHash: String,

    @SerialName("custom_attributes")
    val customAttributes: AdaptyCustomAttributesRequestResponse? = null,

    @SerialName("paid_access_levels")
    val paidAccessLevels: Map<String, AdaptyAccessLevelResponse>? = null,

    @SerialName("subscriptions")
    val subscriptions: Map<String, AdaptySubscriptionResponse>? = emptyMap(),

    @SerialName("non_subscriptions")
    val nonSubscriptions: Map<String, List<AdaptyNonSubscriptionResponse>>? = emptyMap(),

    @SerialName("timestamp")
    val timestamp: Long,

    @SerialName("is_test_user")
    val isTestUser: Boolean
)

internal fun AdaptyProfileResponse.asAdaptyProfile(): AdaptyProfile {
    return AdaptyProfile(
        profileId = profileId,
        segmentId = segmentHash,
        customerUserId = customerUserId,
        accessLevels = paidAccessLevels?.entries?.associate { entry -> entry.key to entry.value.asAdaptyAccessLevel() }
            ?: emptyMap(),
        subscriptions = subscriptions?.entries?.associate { entry -> entry.key to entry.value.asAdaptySubscription() }
            ?: emptyMap(),
        nonSubscriptions = nonSubscriptions?.entries?.associate { entry -> entry.key to entry.value.map { it.asAdaptyNonSubscription() } }
            ?: emptyMap(),
        customAttributes = customAttributes.toTypedMap(),
        isTestUser = isTestUser
    )
}