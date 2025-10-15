package com.adapty.exampleapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adapty.exampleapp.screens.GeneralInfoScreen
import com.adapty.exampleapp.screens.LogsScreen
import com.adapty.exampleapp.screens.OnBoardingScreen
import com.adapty.exampleapp.screens.OnboardingNativeViewScreen
import com.adapty.exampleapp.screens.PaywallsScreen
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.models.AdaptyError
import kmpadapty.example.composeapp.generated.resources.Res
import kmpadapty.example.composeapp.generated.resources.ic_home
import kmpadapty.example.composeapp.generated.resources.ic_info
import kmpadapty.example.composeapp.generated.resources.ic_shopping_cart
import kmpadapty.example.composeapp.generated.resources.ic_star
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {

        val coroutineScope = rememberCoroutineScope()
        val localUriHandler = LocalUriHandler.current
        val appViewModel: AppViewModel = viewModel { AppViewModel() }
        val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
        val error: ErrorDialogState? = ErrorDialogState.from(error = uiState.error)

        LaunchedEffect(Unit) {
            AdaptyUI.setPaywallsEventsObserver(
                AdaptyUIPaywallsEventsObserverImpl(
                    uiCoroutineScope = coroutineScope,
                    uriHandler = localUriHandler
                )
            )
            AdaptyUI.setOnboardingsEventsObserver(
                AdaptyUIOnboardingsEventsObserverImpl(
                    uiCoroutineScope = coroutineScope
                )
            )
        }


        var selectedTab by rememberSaveable { mutableStateOf(0) }
        val tabs = listOf("General", "Paywalls", "Onboardings", "Logs")
        val icons = listOf(
            Res.drawable.ic_home,
            Res.drawable.ic_shopping_cart,
            Res.drawable.ic_star,
            Res.drawable.ic_info
        )

        Scaffold(
            modifier = Modifier.fillMaxWidth().safeDrawingPadding(),
            topBar = { TopAppBar(title = { Text("Welcome to Adapty KMP!") }) },
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, label ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(icons[index]),
                                    contentDescription = label
                                )
                            },
                            label = { Text(label) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> GeneralInfoScreen(
                    viewModel = appViewModel,
                    modifier = Modifier.fillMaxSize().padding(padding)
                )

                1 -> PaywallsScreen(
                    viewModel = appViewModel,
                    modifier = Modifier.padding(padding)
                )

                2 -> OnBoardingScreen(
                    viewModel = appViewModel,
                    modifier = Modifier.padding(padding)
                )

                3 -> LogsScreen(
                    modifier = Modifier.padding(padding)
                )
            }
            ErrorDialog(state = error, onDismiss = appViewModel::onErrorDialogDismissed)
        }
        uiState.nativeOnboardingView?.let { onboarding ->
            OnboardingNativeViewScreen(
                modifier = Modifier.fillMaxSize().zIndex(2f),
                showToastEvents = uiState.showOnboardingToastEvents,
                onboarding = onboarding,
                onNavigateBack = {
                    appViewModel.onUiEvent(AppUiEvent.OnCloseNativeOnboardingView)
                }
            )
        }
    }
}


data class ErrorDialogState(
    val title: String,
    val message: String,
    val details: String? = null
) {
    companion object {
        fun from(error: Throwable?): ErrorDialogState? {
            return when (error) {
                is AdaptyError -> {
                    ErrorDialogState(
                        title = "Error Code: ${error.code}",
                        message = error.message,
                        details = error.detail
                    )
                }

                null -> null
                else -> {
                    ErrorDialogState(
                        title = "Error",
                        message = error.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorDialog(
    state: ErrorDialogState?,
    onDismiss: () -> Unit
) {
    if (state == null) return

    LaunchedEffect(state) {
        AppLogger.e("$state")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = state.title)
        },
        text = {
            Column {
                Text(text = state.message)
                if (!state.details.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(text = state.details)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

