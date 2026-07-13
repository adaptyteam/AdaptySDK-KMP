package com.adapty.kmp.models

/**
 * A permission a flow may request from the user (cross_platform 4.0.0).
 *
 * The known cross-platform set is enumerated below. Unknown, platform-specific (e.g. Android-only
 * `phone` / `sms`), or future identifiers map to [UNKNOWN].
 */
public enum class AdaptyUIPermission {
    PUSH,
    CAMERA,
    MICROPHONE,
    LOCATION_WHEN_USE,
    LOCATION_ALWAYS,
    LOCATION_FULL_ACCURACY,
    PHOTOS,
    CONTACTS,
    TRACKING,
    CALENDAR,
    BLUETOOTH,
    MOTION,
    REMINDERS,
    SPEECH,
    MEDIA_LIBRARY,
    LOCAL_NETWORK,
    FOCUS_STATUS,
    HOMEKIT,
    HEALTH,
    SIRI,
    MUSIC,

    /** An unknown, platform-specific, or future permission identifier. */
    UNKNOWN
}
