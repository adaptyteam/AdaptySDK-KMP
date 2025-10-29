package com.adapty.kmp.models

import kotlinx.datetime.LocalDateTime


public sealed interface AdaptyInstallationStatus

public data object AdaptyInstallationStatusNotDetermined : AdaptyInstallationStatus
public data object AdaptyInstallationStatusNotAvailable : AdaptyInstallationStatus
public data class AdaptyInstallationStatusDetermined(public val details: AdaptyInstallationDetails) : AdaptyInstallationStatus


public data class AdaptyInstallationDetails(
    public val installId: String?,
    public val installTime: LocalDateTime,
    public val appLaunchCount: Int,
    public val payload: Payload?,
) {
    public data class Payload(
        public val jsonString: String,
        public val dataMap: Map<String, Any>,
    )
}