@file:OptIn(InternalAdaptyApi::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.adapty.kmp.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelStoreOwner
import com.adapty.internal.crossplatform.ui.Dependencies.safeInject
import com.adapty.internal.crossplatform.ui.OnboardingUiManager
import com.adapty.internal.utils.InternalAdaptyApi
import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.plugin.request.asJsonString
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.ui.onboardings.AdaptyOnboardingView

@OptIn(AdaptyKMPInternal::class)
@Composable
internal actual fun AdaptyUIOnboardingPlatformView(
    onboarding: AdaptyOnboarding,
    modifier: Modifier,
) {
    val viewModelStoreOwner = LocalActivity.current as? ViewModelStoreOwner ?: return
    val context = LocalContext.current
    val onboardingUiManager: OnboardingUiManager? by safeInject<OnboardingUiManager>()

    val onboardingView = remember {
        AdaptyOnboardingView(context).apply {
            onboardingUiManager?.setupOnboardingView(
                onboardingView = this,
                viewModelStoreOwner = viewModelStoreOwner,
                args = onboarding.asJsonString(),
                id = onboarding.idForNativePlatformView
            )
        }
    }

    AndroidView(modifier = modifier, factory = { onboardingView })
    DisposableEffect(Unit) {
        onDispose {
            onboardingUiManager?.clearOnboardingView(onboardingView)
        }
    }
}