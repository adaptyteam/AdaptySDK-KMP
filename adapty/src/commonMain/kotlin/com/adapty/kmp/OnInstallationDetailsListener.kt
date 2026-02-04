package com.adapty.kmp

import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyInstallationDetails

/**
 * A listener interface for receiving callbacks about the
 * current installation’s details and status.
 *
 * This listener is triggered when the Adapty SDK successfully retrieves
 * installation information or encounters an error.
 */
public interface OnInstallationDetailsListener {

    /**
     * Called when installation details are successfully retrieved.
     *
     * @param details the [AdaptyInstallationDetails] describing the installation state.
     */
    public fun onInstallationDetailsSuccess(details: AdaptyInstallationDetails)

    /**
     * Called when retrieving installation details fails.
     *
     * @param error the [AdaptyError] describing the reason for failure.
     */
    public fun onInstallationDetailsFailure(error: AdaptyError)
}
