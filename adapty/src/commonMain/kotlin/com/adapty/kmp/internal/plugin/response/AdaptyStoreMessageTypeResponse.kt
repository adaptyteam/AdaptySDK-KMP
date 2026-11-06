package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyStoreMessageType

/**
 * Maps a wire store message type (`AdaptyStoreMessageType`, cross_platform 4.3.1) onto the public
 * [AdaptyStoreMessageType]. Every value is carried through verbatim — known reasons as well as
 * opaque `storekit_<rawValue>` ones and anything a future native SDK adds.
 */
internal fun String.asAdaptyStoreMessageType(): AdaptyStoreMessageType = AdaptyStoreMessageType(this)
