package com.adapty.kmp.listeners

import com.adapty.kmp.models.AdaptyProfile

public fun interface OnProfileUpdatedListener {
    public fun onProfileReceived(profile: AdaptyProfile)
}