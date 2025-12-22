package com.adapty.exampleapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adapty.exampleapp.AppUiEvent
import com.adapty.exampleapp.AppUiState
import com.adapty.exampleapp.AppViewModel
import com.adapty.exampleapp.Platform
import com.adapty.exampleapp.components.ListActionTile
import com.adapty.exampleapp.components.ListSection
import com.adapty.exampleapp.components.ListTextFieldTile
import com.adapty.exampleapp.components.ListTextTile
import com.adapty.exampleapp.components.ListToggleTile
import com.adapty.exampleapp.getPlatform
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyWebPresentation

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentOnboardingId by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OnboardingListScreen(
            modifier = Modifier.weight(1f),
            uiState = uiState,
            onUiEvent = viewModel::onUiEvent
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add and save Onboarding") },
                text = {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = currentOnboardingId,
                            onValueChange = { currentOnboardingId = it },
                            placeholder = { Text("Enter Placement Id") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (currentOnboardingId.isNotBlank()) {
                            viewModel.onUiEvent(
                                AppUiEvent.OnNewOnboardingIdAdded(
                                    currentOnboardingId
                                )
                            )
                        }
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Button(
            onClick = { showDialog = true }, modifier =
                Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        ) {
            Text("Add Onboarding")
        }
    }

}

@Composable
private fun OnboardingListScreen(
    modifier: Modifier = Modifier,
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit
) {
    LazyColumn(modifier = modifier) {

        item {
            ListSection(headerText = "Settings") {
                ListToggleTile(
                    title = "Show Toast Events",
                    value = uiState.showOnboardingToastEvents,
                    onChanged = { onUiEvent(AppUiEvent.OnToggleOnboardingShowToastEvents) }
                )
                ListTextFieldTile(
                    value = uiState.onboardingLocale ?: "",
                    onChanged = { onUiEvent(AppUiEvent.OnChangeOnboardingLocale(it)) },
                    placeholder = "Enter onboarding locale (e.g., en, es, fr)"
                )
            }
        }

        items(uiState.savedOnboardingIds.toList()) { id ->

            val onboarding = uiState.savedOnboardings[id]
            ListSection(headerText = "Onboarding Id: $id") {
                when {
                    onboarding == null -> {
                        ListTextTile(
                            title = "Status",
                            subtitle = "Error",
                            subtitleColor = Color.Red
                        )
                    }

                    else -> {
                        ListTextTile(
                            title = "Status",
                            subtitle = "OK",
                            subtitleColor = Color(0xFF32CD32)
                        )
                        ListTextTile(
                            title = "Id",
                            subtitle = onboarding.id
                        )
                        ListTextTile(
                            title = "Name",
                            subtitle = onboarding.name,
                        )
                        ListTextTile(
                            title = "Variation Id",
                            subtitle = onboarding.variationId,
                        )

                        // Present action
                        ListActionTile(
                            title = "Present Full Screen",
                            showProgress = uiState.isLoadingOnboard,
                            onClick = {
                                onUiEvent(
                                    AppUiEvent.OnClickPresentOnboarding(
                                        onboarding = onboarding,
                                        presentationStyle = AdaptyUIIOSPresentationStyle.FULLSCREEN,
                                        externalUrlsPresentation = AdaptyWebPresentation.IN_APP_BROWSER
                                    )
                                )
                            }
                        )

                        if (getPlatform() == Platform.Ios) {
                            ListActionTile(
                                title = "Present Page Sheet",
                                showProgress = uiState.isLoadingOnboard,
                                onClick = {
                                    onUiEvent(
                                        AppUiEvent.OnClickPresentOnboarding(
                                            onboarding = onboarding,
                                            presentationStyle = AdaptyUIIOSPresentationStyle.PAGESHEET,
                                            externalUrlsPresentation = AdaptyWebPresentation.IN_APP_BROWSER
                                        )
                                    )
                                }
                            )
                        }

                        // Present Native View action
                        ListActionTile(
                            title = "Present Native View",
                            showProgress = uiState.isLoadingOnboard,
                            onClick = {
                                onUiEvent(
                                    AppUiEvent.OnClickPresentOnboardingNativeView(onboarding)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}