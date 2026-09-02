package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIPermission

/**
 * Maps a wire permission identifier (`AdaptyUI.Permission`, cross_platform 4.0.0) onto the public
 * [AdaptyUIPermission]. Every id is carried through verbatim — unknown, platform-specific and
 * future values included.
 */
internal fun String.asAdaptyUIPermission(): AdaptyUIPermission = AdaptyUIPermission(this)
