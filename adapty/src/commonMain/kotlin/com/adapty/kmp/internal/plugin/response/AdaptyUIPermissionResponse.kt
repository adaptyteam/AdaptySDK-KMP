package com.adapty.kmp.internal.plugin.response

import com.adapty.kmp.models.AdaptyUIPermission

/**
 * Maps a wire permission identifier (`AdaptyUI.Permission`, cross_platform 4.0.0) onto the public
 * [AdaptyUIPermission] enum. Unknown / platform-specific / future ids map to [AdaptyUIPermission.UNKNOWN].
 */
internal fun String.asAdaptyUIPermission(): AdaptyUIPermission = when (this) {
    "push" -> AdaptyUIPermission.PUSH
    "camera" -> AdaptyUIPermission.CAMERA
    "microphone" -> AdaptyUIPermission.MICROPHONE
    "location_when_use" -> AdaptyUIPermission.LOCATION_WHEN_USE
    "location_always" -> AdaptyUIPermission.LOCATION_ALWAYS
    "location_full_accuracy" -> AdaptyUIPermission.LOCATION_FULL_ACCURACY
    "photos" -> AdaptyUIPermission.PHOTOS
    "contacts" -> AdaptyUIPermission.CONTACTS
    "tracking" -> AdaptyUIPermission.TRACKING
    "calendar" -> AdaptyUIPermission.CALENDAR
    "bluetooth" -> AdaptyUIPermission.BLUETOOTH
    "motion" -> AdaptyUIPermission.MOTION
    "reminders" -> AdaptyUIPermission.REMINDERS
    "speech" -> AdaptyUIPermission.SPEECH
    "media_library" -> AdaptyUIPermission.MEDIA_LIBRARY
    "local_network" -> AdaptyUIPermission.LOCAL_NETWORK
    "focus_status" -> AdaptyUIPermission.FOCUS_STATUS
    "homekit" -> AdaptyUIPermission.HOMEKIT
    "health" -> AdaptyUIPermission.HEALTH
    "siri" -> AdaptyUIPermission.SIRI
    "music" -> AdaptyUIPermission.MUSIC
    else -> AdaptyUIPermission.UNKNOWN
}
