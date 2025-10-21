package com.adapty.kmp

import com.adapty.kmp.models.AdaptyProfile

/**
 * A listener interface for receiving updates about the user's [AdaptyProfile].
 *
 * This is triggered whenever the subscription status changes
 * or when Adapty provides cached data on app startup.
 */
public fun interface OnProfileUpdatedListener {
    /**
     * Called when a new [AdaptyProfile] is available.
     *
     * @param profile the updated user profile containing subscription status and related data.
     */
    public fun onProfileReceived(profile: AdaptyProfile)
}
