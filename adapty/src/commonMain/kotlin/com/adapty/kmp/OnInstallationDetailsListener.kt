package com.adapty.kmp

import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyInstallationDetails

public interface OnInstallationDetailsListener {
    public fun onInstallationDetailsSuccess(details: AdaptyInstallationDetails)
    public fun onInstallationDetailsFailure(error: AdaptyError)
}