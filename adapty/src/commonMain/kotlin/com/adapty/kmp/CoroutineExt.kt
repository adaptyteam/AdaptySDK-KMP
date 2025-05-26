package com.adapty.kmp

import com.adapty.kmp.models.AdaptyConfig
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyProfileParameters
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitActivate(configuration: AdaptyConfig): Unit =
    awaitErrorIfAny { onError -> Adapty.activate(configuration = configuration, onError = onError) }

@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitIdentify(customerUserId: String): Unit =
    awaitErrorIfAny { onError ->
        Adapty.identify(
            customerUserId = customerUserId,
            onError = onError
        )
    }

@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitUpdateProfile(params: AdaptyProfileParameters): Unit =
    awaitErrorIfAny { onError ->
        Adapty.updateProfile(
            params = params,
            onError = onError
        )
    }

@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitUpdateAttribution(
    attribution: Map<String, Any>,
    source: String,
): Unit =
    awaitErrorIfAny { onError ->
        Adapty.updateAttribution(
            attribution = attribution,
            source = source,
            onError = onError
        )
    }

@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitSetIntegrationIdentifier(key: String, value: String): Unit =
    awaitErrorIfAny { onError ->
        Adapty.setIntegrationIdentifier(
            key = key,
            value = value,
            onError = onError
        )
    }

@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitLogout(): Unit =
    awaitErrorIfAny { onError -> Adapty.logout(onError) }


@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitSetFallbackPaywalls(assetId: String): Unit =
    awaitErrorIfAny { onError -> Adapty.setFallbackPaywalls(assetId = assetId, onError = onError) }


@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitLogShowPaywall(
    paywall: AdaptyPaywall
): Unit =
    awaitErrorIfAny { onError ->
        Adapty.logShowPaywall(
            paywall = paywall,
            onError = onError
        )
    }

@Throws(AdaptyError::class, CancellationException::class)
public suspend fun Adapty.awaitLogShowOnboarding(
    name: String?,
    screenName: String?,
    screenOrder: Int,
): Unit =
    awaitErrorIfAny { onError ->
        Adapty.logShowOnboarding(
            name = name,
            screenName = screenName,
            screenOrder = screenOrder,
            onError = onError
        )
    }

internal suspend fun Adapty.awaitErrorIfAny(block: (onError: (AdaptyError?) -> Unit) -> Unit) {
    return suspendCoroutine { cont ->
        block { error ->
            if (error != null) {
                cont.resumeWithException(error)
            } else {
                cont.resume(Unit)
            }
        }
    }
}