import com.adapty.kmp.internal.plugin.response.AdaptyAccessLevelResponse
import com.adapty.kmp.internal.plugin.response.AdaptySubscriptionResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyAccessLevel
import com.adapty.kmp.internal.plugin.response.asAdaptySubscription
import com.adapty.kmp.internal.utils.jsonInstance
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The profile is cross-platform: a Google Play subscription is visible on an iPhone and vice versa,
 * so `offer_id` / `active_promotional_offer_id` must be decoded whatever the device platform is.
 * The decoders are shared common code; this guards against a platform or store check creeping in.
 */
class AdaptyProfileOfferIdStoreTest {

    @Test
    fun `access level keeps offer ids for both stores`() {
        for ((store, offerId, promoId) in storeCases) {
            val accessLevel = jsonInstance.decodeFromJsonElement(
                AdaptyAccessLevelResponse.serializer(),
                accessLevelJson(store, offerId, promoId)
            ).asAdaptyAccessLevel()

            assertEquals(store, accessLevel.store)
            assertEquals(offerId, accessLevel.offerId, "offerId for $store")
            assertEquals(promoId, accessLevel.activePromotionalOfferId, "activePromotionalOfferId for $store")
        }
    }

    @Test
    fun `subscription keeps offer ids for both stores`() {
        for ((store, offerId, promoId) in storeCases) {
            val subscription = jsonInstance.decodeFromJsonElement(
                AdaptySubscriptionResponse.serializer(),
                subscriptionJson(store, offerId, promoId)
            ).asAdaptySubscription()

            assertEquals(store, subscription.store)
            assertEquals(offerId, subscription.offerId, "offerId for $store")
            assertEquals(promoId, subscription.activePromotionalOfferId, "activePromotionalOfferId for $store")
        }
    }

    private val storeCases = listOf(
        Triple("play_store", "intro-pay-up-front-weekly", "promo-weekly-50"),
        Triple("app_store", "com.app.intro.weekly", "com.app.promo.weekly"),
    )

    private fun accessLevelJson(store: String, offerId: String, promoId: String): JsonObject =
        buildJsonObject {
            put("id", "premium")
            put("is_active", true)
            put("vendor_product_id", "weekly.premium.599")
            put("store", store)
            put("activated_at", "2026-08-24T09:34:24.727Z")
            put("is_lifetime", false)
            put("will_renew", true)
            put("is_in_grace_period", false)
            put("is_refund", false)
            put("active_promotional_offer_type", "pay_up_front")
            put("active_promotional_offer_id", promoId)
            put("offer_id", offerId)
        }

    private fun subscriptionJson(store: String, offerId: String, promoId: String): JsonObject =
        buildJsonObject {
            put("store", store)
            put("vendor_product_id", "weekly.premium.599")
            put("vendor_transaction_id", "tx-1")
            put("vendor_original_transaction_id", "tx-0")
            put("is_active", true)
            put("is_lifetime", false)
            put("activated_at", "2026-08-24T09:34:24.727Z")
            put("is_in_grace_period", false)
            put("is_refund", false)
            put("is_sandbox", true)
            put("will_renew", true)
            put("active_promotional_offer_type", "pay_up_front")
            put("active_promotional_offer_id", promoId)
            put("offer_id", offerId)
        }
}
