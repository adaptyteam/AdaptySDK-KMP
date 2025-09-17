package com.adapty.kmp

import com.adapty.kmp.models.AdaptyProfile

public fun interface OnProfileUpdatedListener {
    public fun onProfileReceived(profile: AdaptyProfile)
}