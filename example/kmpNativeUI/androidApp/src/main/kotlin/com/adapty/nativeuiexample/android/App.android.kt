package com.adapty.nativeuiexample.android

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.adapty.kmp.Adapty
import com.adapty.kmp.AdaptyNativeOnboardingView
import com.adapty.kmp.AdaptyNativePaywallView
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.AdaptyUIOnboardingsEventsObserver
import com.adapty.kmp.AdaptyUIPaywallsEventsObserver
import com.adapty.kmp.createNativeOnboardingView
import com.adapty.kmp.createNativePaywallView
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyOnboardingsAnalyticsEvent
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIAction
import com.adapty.kmp.models.AdaptyUIOnboardingMeta
import com.adapty.kmp.models.AdaptyUIOnboardingView
import com.adapty.kmp.models.AdaptyUIPaywallView
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import com.adapty.nativeuiexample.AdaptyManager
import com.adapty.nativeuiexample.AppLogger
import kotlinx.coroutines.launch


class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AdaptyManager.initialize()
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NativeUIExampleApp()
                }
            }
        }
    }
}

@Composable
private fun NativeUIExampleApp() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isLoading by remember { mutableStateOf(false) }
    var placementId by remember { mutableStateOf("") }
    var nativePaywallView by remember { mutableStateOf<AdaptyNativePaywallView?>(null) }
    var nativeOnboardingView by remember { mutableStateOf<AdaptyNativeOnboardingView?>(null) }



    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            nativePaywallView != null -> {
                // Show native paywall view embedded below a header
                NativePaywallScreen(
                    modifier = Modifier.padding(padding),
                    nativeView = nativePaywallView!!,
                    onDismiss = {
                        nativePaywallView?.dispose()
                        nativePaywallView = null
                    }
                )
            }

            nativeOnboardingView != null -> {
                // Show native onboarding view embedded below a header
                NativeOnboardingScreen(
                    modifier = Modifier.padding(padding),
                    nativeView = nativeOnboardingView!!,
                    onDismiss = {
                        nativeOnboardingView?.dispose()
                        nativeOnboardingView = null
                    }
                )
            }

            else -> {
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "KMP Native UI Example",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Uses adapty module only (no adapty-ui)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = placementId,
                        onValueChange = { placementId = it },
                        label = { Text("Placement ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Button(
                            onClick = {
                                if (placementId.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Enter a placement ID",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                isLoading = true
                                scope.launch {
                                    Adapty.getPaywall(placementId)
                                        .onSuccess { paywall ->
                                            AppLogger.d("Paywall loaded: ${paywall.name}")
                                            val activity = context as ComponentActivity
                                            val view = AdaptyUI.createNativePaywallView(
                                                context = context,
                                                viewModelStoreOwner = activity,
                                                paywall = paywall,
                                                observer = object : AdaptyUIPaywallsEventsObserver {
                                                    override fun paywallViewDidPerformAction(
                                                        view: AdaptyUIPaywallView,
                                                        action: AdaptyUIAction
                                                    ) {
                                                        AppLogger.d("Paywall action: $action")
                                                        when (action) {
                                                            is AdaptyUIAction.CloseAction,
                                                            is AdaptyUIAction.AndroidSystemBackAction -> {
                                                                nativePaywallView?.dispose()
                                                                nativePaywallView = null
                                                            }

                                                            else -> {}
                                                        }
                                                    }

                                                    override fun paywallViewDidFinishPurchase(
                                                        view: AdaptyUIPaywallView,
                                                        product: AdaptyPaywallProduct,
                                                        purchaseResult: AdaptyPurchaseResult
                                                    ) {
                                                        AppLogger.d("Purchase finished: $purchaseResult")
                                                        if (purchaseResult !is AdaptyPurchaseResult.UserCanceled) {
                                                            nativePaywallView?.dispose()
                                                            nativePaywallView = null
                                                        }
                                                    }

                                                    override fun paywallViewDidFailPurchase(
                                                        view: AdaptyUIPaywallView,
                                                        product: AdaptyPaywallProduct,
                                                        error: AdaptyError
                                                    ) {
                                                        AppLogger.e("Purchase failed: $error")
                                                    }

                                                    override fun paywallViewDidFailRendering(
                                                        view: AdaptyUIPaywallView,
                                                        error: AdaptyError
                                                    ) {
                                                        AppLogger.e("Rendering failed: $error")
                                                    }

                                                    override fun paywallViewDidFinishRestore(
                                                        view: AdaptyUIPaywallView,
                                                        profile: AdaptyProfile
                                                    ) {
                                                        AppLogger.d("Restore finished")
                                                    }
                                                }
                                            )
                                            nativePaywallView = view
                                        }
                                        .onError { error ->
                                            AppLogger.e("Failed to load paywall: $error")
                                            snackbarHostState.showSnackbar("Error: ${error.message}")
                                        }
                                    isLoading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show Native Paywall")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (placementId.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Enter a placement ID",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                isLoading = true
                                scope.launch {
                                    Adapty.getOnboarding(placementId)
                                        .onSuccess { onboarding ->
                                            AppLogger.d("Onboarding loaded: ${onboarding.name}")
                                            val activity = context as ComponentActivity
                                            val view = AdaptyUI.createNativeOnboardingView(
                                                context = context,
                                                viewModelStoreOwner = activity,
                                                onboarding = onboarding,
                                                observer = object :
                                                    AdaptyUIOnboardingsEventsObserver {
                                                    override fun onboardingViewDidFinishLoading(
                                                        view: AdaptyUIOnboardingView,
                                                        meta: AdaptyUIOnboardingMeta
                                                    ) {
                                                        AppLogger.d("Onboarding loaded")
                                                    }

                                                    override fun onboardingViewOnCloseAction(
                                                        view: AdaptyUIOnboardingView,
                                                        meta: AdaptyUIOnboardingMeta,
                                                        actionId: String
                                                    ) {
                                                        AppLogger.d("Onboarding close: $actionId")
                                                        nativeOnboardingView?.dispose()
                                                        nativeOnboardingView = null
                                                    }

                                                    override fun onboardingViewOnCustomAction(
                                                        view: AdaptyUIOnboardingView,
                                                        meta: AdaptyUIOnboardingMeta,
                                                        actionId: String
                                                    ) {
                                                        AppLogger.d(

                                                            "Onboarding custom action: $actionId"
                                                        )
                                                    }

                                                    override fun onboardingViewOnAnalyticsEvent(
                                                        view: AdaptyUIOnboardingView,
                                                        meta: AdaptyUIOnboardingMeta,
                                                        event: AdaptyOnboardingsAnalyticsEvent
                                                    ) {
                                                        AppLogger.d("Onboarding analytics: $event")
                                                    }

                                                    override fun onboardingViewDidFailWithError(
                                                        view: AdaptyUIOnboardingView,
                                                        error: AdaptyError
                                                    ) {
                                                        AppLogger.e("Onboarding error: $error")
                                                    }
                                                }
                                            )
                                            nativeOnboardingView = view
                                        }
                                        .onError { error ->
                                            AppLogger.e("Failed to load onboarding: $error")
                                            snackbarHostState.showSnackbar("Error: ${error.message}")
                                        }
                                    isLoading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show Native Onboarding")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NativePaywallScreen(
    modifier: Modifier = Modifier,
    nativeView: AdaptyNativePaywallView,
    onDismiss: () -> Unit
) {
    DisposableEffect(nativeView) {
        onDispose { onDismiss() }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // App-owned header — shows this is an embedded native view, not a fullscreen takeover
        EmbeddedViewHeader(title = "Embedded Paywall", onClose = onDismiss)

        // The native Adapty paywall rendered as an embedded platform view
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { nativeView.view as View },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun NativeOnboardingScreen(
    modifier: Modifier = Modifier,
    nativeView: AdaptyNativeOnboardingView,
    onDismiss: () -> Unit
) {
    DisposableEffect(nativeView) {
        onDispose { onDismiss() }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // App-owned header — shows this is an embedded native view, not a fullscreen takeover
        EmbeddedViewHeader(title = "Embedded Onboarding", onClose = onDismiss)

        // The native Adapty onboarding rendered as an embedded platform view
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { nativeView.view as View },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun EmbeddedViewHeader(title: String, onClose: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Native platform view below",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onClose) {
                Text("Close")
            }
        }
        HorizontalDivider()
    }
}

@Preview
@Composable
private fun AppPreview() {
    NativeUIExampleApp()
}

