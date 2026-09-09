package com.adapty.kmp.models

/**
 * A permission a flow may request from the user (cross_platform 4.0.0).
 *
 * The known cross-platform identifiers are exposed as constants below, but any [value] is valid:
 * platform-specific ids (e.g. the Android-only `phone` / `sms`) and identifiers added by future
 * dashboard releases arrive unchanged rather than collapsing into an "unknown" constant. Compare
 * against the constants, and fall back to [value] for anything else:
 *
 * ```
 * when (request.permission) {
 *     AdaptyUIPermission.PUSH -> requestNotificationPermission()
 *     AdaptyUIPermission.CAMERA -> requestCameraPermission()
 *     else -> requestByRawId(request.permission.value)
 * }
 * ```
 *
 * @property value the raw permission identifier as sent by the native SDK.
 */
public class AdaptyUIPermission(public val value: String) {

    public companion object {
        public val PUSH: AdaptyUIPermission = AdaptyUIPermission("push")
        public val CAMERA: AdaptyUIPermission = AdaptyUIPermission("camera")
        public val MICROPHONE: AdaptyUIPermission = AdaptyUIPermission("microphone")
        public val LOCATION_WHEN_USE: AdaptyUIPermission = AdaptyUIPermission("location_when_use")
        public val LOCATION_ALWAYS: AdaptyUIPermission = AdaptyUIPermission("location_always")
        public val LOCATION_FULL_ACCURACY: AdaptyUIPermission =
            AdaptyUIPermission("location_full_accuracy")
        public val PHOTOS: AdaptyUIPermission = AdaptyUIPermission("photos")
        public val CONTACTS: AdaptyUIPermission = AdaptyUIPermission("contacts")
        public val TRACKING: AdaptyUIPermission = AdaptyUIPermission("tracking")
        public val CALENDAR: AdaptyUIPermission = AdaptyUIPermission("calendar")
        public val BLUETOOTH: AdaptyUIPermission = AdaptyUIPermission("bluetooth")
        public val MOTION: AdaptyUIPermission = AdaptyUIPermission("motion")
        public val REMINDERS: AdaptyUIPermission = AdaptyUIPermission("reminders")
        public val SPEECH: AdaptyUIPermission = AdaptyUIPermission("speech")
        public val MEDIA_LIBRARY: AdaptyUIPermission = AdaptyUIPermission("media_library")
        public val LOCAL_NETWORK: AdaptyUIPermission = AdaptyUIPermission("local_network")
        public val FOCUS_STATUS: AdaptyUIPermission = AdaptyUIPermission("focus_status")
        public val HOMEKIT: AdaptyUIPermission = AdaptyUIPermission("homekit")
        public val HEALTH: AdaptyUIPermission = AdaptyUIPermission("health")
        public val SIRI: AdaptyUIPermission = AdaptyUIPermission("siri")
        public val MUSIC: AdaptyUIPermission = AdaptyUIPermission("music")
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is AdaptyUIPermission && other.value == value)

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "AdaptyUIPermission($value)"
}
