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
import com.adapty.exampleapp.components.ListActionTile
import com.adapty.exampleapp.components.ListSection
import com.adapty.exampleapp.components.ListTextTile


@Composable
fun PaywallsScreen(modifier: Modifier, viewModel: AppViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    var currentPaywallId by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        PaywallsListScreen(
            modifier = Modifier.weight(1f),
            uiState = uiState,
            onUiEvent = viewModel::onUiEvent
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add and save Paywall") },
                text = {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = currentPaywallId,
                            onValueChange = { currentPaywallId = it },
                            placeholder = { Text("Enter Placement Id") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (currentPaywallId.isNotBlank()) {
                            viewModel.onUiEvent(AppUiEvent.OnNewPaywallIdAdded(currentPaywallId))
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
            Text("Add Paywall")
        }
    }
}


@Composable
private fun PaywallsListScreen(
    modifier: Modifier = Modifier,
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit
) {
    LazyColumn(modifier = modifier) {

        items(uiState.savedPaywallIds.toList()) { id ->
            val paywall = uiState.savedPaywalls[id]

            ListSection(headerText = "Paywall Id: $id") {
                when {
                    paywall == null -> {
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
                            title = "Variation Id",
                            subtitle = paywall.variationId
                        )
                        ListTextTile(
                            title = "Has View",
                            subtitle = paywall.hasViewConfiguration.toString(),
                            subtitleColor = if (paywall.hasViewConfiguration) Color(0xFF32CD32) else Color.Red
                        )

                        if (paywall.hasViewConfiguration) {
                            ListActionTile(
                                title = "Present",
                                onClick = {
                                    onUiEvent(
                                        AppUiEvent.CreateAndPresentPaywallView(
                                            paywall = paywall,
                                            loadProducts = false
                                        )
                                    )
                                }
                            )
                            ListActionTile(
                                title = "Load Products and Present",
                                onClick = {
                                    onUiEvent(
                                        AppUiEvent.CreateAndPresentPaywallView(
                                            paywall = paywall,
                                            loadProducts = true
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
