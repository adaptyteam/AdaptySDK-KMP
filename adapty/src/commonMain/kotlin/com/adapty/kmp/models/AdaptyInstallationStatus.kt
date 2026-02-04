package com.adapty.kmp.models

import kotlinx.datetime.LocalDateTime

/**
 * Represents the status of Adapty installation tracking.
 */
public sealed interface AdaptyInstallationStatus

/**
 * The installation status has not yet been determined.
 */
public data object AdaptyInstallationStatusNotDetermined : AdaptyInstallationStatus

/**
 * Installation details are not available for this device or configuration.
 */
public data object AdaptyInstallationStatusNotAvailable : AdaptyInstallationStatus

/**
 * The installation status has been successfully determined.
 *
 * @property details detailed information about the installation.
 */
public data class AdaptyInstallationStatusDetermined(val details: AdaptyInstallationDetails) :
    AdaptyInstallationStatus

/**
 * Contains detailed information about the current app installation.
 *
 * This data can be used for analytics, user acquisition tracking,
 * and subscription event correlation.
 *
 * @property installId unique identifier for the installation, if available.
 * @property installTime date and time when the app was first installed.
 * @property appLaunchCount number of times the app has been launched.
 * @property payload optional structured data related to installation events.
 */
public data class AdaptyInstallationDetails(
    public val installId: String?,
    public val installTime: LocalDateTime,
    public val appLaunchCount: Int,
    public val payload: Payload?,
) {

    /**
     * Represents additional structured data for an installation.
     *
     * @property jsonString the raw JSON representation of the data.
     * @property dataMap a parsed map representation of the payload for easier access.
     */
    public data class Payload(
        public val jsonString: String,
        public val dataMap: Map<String, Any>,
    )
}
