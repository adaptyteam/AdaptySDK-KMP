package com.adapty.exampleapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adapty.exampleapp.AppLogger
import com.adapty.exampleapp.AppUiEvent
import com.adapty.exampleapp.AppUiState
import com.adapty.exampleapp.AppViewModel
import com.adapty.exampleapp.Constants
import com.adapty.exampleapp.components.ListActionTile
import com.adapty.exampleapp.components.ListProductTile
import com.adapty.exampleapp.components.ListSection
import com.adapty.exampleapp.components.ListTextFieldTile
import com.adapty.exampleapp.components.ListTextTile
import com.adapty.kmp.Adapty
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.models.AdaptyIosRefundPreference
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import kotlinx.coroutines.launch


@Composable
fun GeneralInfoScreen(
    viewModel: AppViewModel = viewModel(),
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        uiState.adaptyProfile?.let {
            GeneralInfoScreenContent(uiState = uiState, onUiEvent = viewModel::onUiEvent)
        }
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
    }

}


@Composable
private fun GeneralInfoScreenContent(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState()).padding(bottom = 16.dp)) {
        ProfileIdSection(uiState, onUiEvent)
        IdentifyCustomerIdSection(uiState, onUiEvent)
        ProfileInfoSection(uiState, onUiEvent)
        ExampleABPaywallTestSection(uiState, onUiEvent)
        CustomPaywallSection(uiState, onUiEvent)
        OtherActionsSection(uiState, onUiEvent)
        RefundSaverSection(uiState, onUiEvent)
        LogoutSection { onUiEvent(AppUiEvent.OnClickLogout) }
    }
}

@Composable
fun ProfileIdSection(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit
) {
    val profileId = uiState.adaptyProfile?.profileId
    val isActive = profileId != null
    val clipboardManager = LocalClipboardManager.current

    ListSection(
        headerText = "Adapty Profile Id",
        footerText = "👆🏻 Tap to copy"
    ) {
        ListActionTile(
            title = profileId ?: "null",
            isActive = isActive,
            onClick = {
                profileId?.let {
                    clipboardManager.setText(AnnotatedString(profileId))
                }
            }
        )
    }
}


@Composable
fun IdentifyCustomerIdSection(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit
) {
    val customerUserId = uiState.adaptyProfile?.customerUserId
    val enteredCustomerUserId = uiState.userEnteredCustomerUserId

    ListSection(headerText = "Customer User Id") {
        if (!customerUserId.isNullOrEmpty()) {
            ListTextTile(title = "Current", subtitle = customerUserId)
        }

        ListTextFieldTile(
            placeholder = "Enter Customer User Id",
            value = enteredCustomerUserId,
            onChanged = { text ->
                onUiEvent(AppUiEvent.OnCustomerUserIdInputChanged(text))
            },
            onSubmitted = {
                onUiEvent(AppUiEvent.OnClickIdentifyCustomerUserId)

            }
        )

        ListActionTile(
            title = "Identify",
            isActive = enteredCustomerUserId.isNotEmpty(),
            onClick = {
                onUiEvent(AppUiEvent.OnClickIdentifyCustomerUserId)
            }
        )
    }
}


@Composable
fun ProfileInfoSection(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit
) {
    val profile = uiState.adaptyProfile
    val premium = profile?.accessLevels?.get("premium")

    ListSection(headerText = "Profile") {
        ListTextTile(
            title = "Is Test",
            subtitle = if (profile?.isTestUser == true) "true" else "false"
        )

        ListTextTile(
            title = "Premium",
            subtitle = if (premium?.isActive == true) "Active" else "Inactive",
            subtitleColor = if (premium?.isActive == true) Color.Green else Color.Red
        )

        ListTextTile(
            title = "Is Lifetime",
            subtitle = if (premium?.isLifetime == true) "true" else "false"
        )

        premium?.activatedAt?.let {
            ListTextTile(
                title = "Activated At",
                subtitle = premium.activatedAt
            )
        }

        premium?.renewedAt?.let {
            ListTextTile(
                title = "Renewed At",
                subtitle = premium.renewedAt
            )
        }

        premium?.expiresAt?.let {
            ListTextTile(
                title = "Expires At",
                subtitle = premium.expiresAt
            )
        }

        ListTextTile(
            title = "Will Renew",
            subtitle = if (premium?.willRenew == true) "true" else "false"
        )

        ListTextTile(
            title = "Subscriptions: ${profile?.subscriptions?.size ?: 0}"
        )

        ListTextTile(
            title = "NonSubscriptions: ${profile?.nonSubscriptions?.size ?: 0}"
        )

        ListTextTile(
            title = "Custom Attributes",
            subtitle = profile?.customAttributes.toString()
        )

        ListActionTile(
            title = "Update",
            onClick = {
                onUiEvent(AppUiEvent.OnClickReloadProfile)
            },
        )
    }
}


@Composable
private fun RefundSaverSection(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit,
) {
    ListSection(
        headerText = "Refund Saver",
        footerText = null
    ) {
        ListActionTile(
            title = "Update Consent: FALSE",
            isActive = true,
            onClick = {
                onUiEvent(AppUiEvent.OnClickUpdateConsent(false))
            }
        )

        ListActionTile(
            title = "Update Consent: TRUE",
            isActive = true,
            onClick = {
                onUiEvent(AppUiEvent.OnClickUpdateConsent(true))
            }
        )

        ListActionTile(
            title = "Update Preference: NO_PREFERENCE",
            isActive = true,
            onClick = {
                onUiEvent(AppUiEvent.OnClickUpdateRefundPreference(AdaptyIosRefundPreference.NO_PREFERENCE))
            }
        )

        ListActionTile(
            title = "Update Preference: DECLINE",
            isActive = true,
            onClick = {
                onUiEvent(AppUiEvent.OnClickUpdateRefundPreference(AdaptyIosRefundPreference.DECLINE))
            }
        )

        ListActionTile(
            title = "Update Preference: GRANT",
            isActive = true,
            onClick = {
                onUiEvent(AppUiEvent.OnClickUpdateRefundPreference(AdaptyIosRefundPreference.GRANT))
            }
        )

    }
}

@Composable
private fun ExampleABPaywallTestSection(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit,
) {
    ListSection(headerText = "Example A/B Test") {
        if (uiState.examplePaywall == null) {
            ListTextTile(
                title = "Paywall Placement ID: ${Constants.EXAMPLE_PAYWALL_ID}",
                subtitle = "Loading...",
                subtitleColor = Color.Blue
            )
        } else {
            ListTextTile(
                title = "Paywall Placement ID: ${Constants.EXAMPLE_PAYWALL_ID}",
                subtitle = "OK",
                subtitleColor = Color.Green
            )

            PaywallFetchPolicySelector(
                selectedPolicy = uiState.selectedPolicy,
                onPolicySelected = {
                    onUiEvent(AppUiEvent.OnNewFetchPolicySelected(it))
                }
            )

            PaywallContentSection(
                paywall = uiState.examplePaywall,
                products = uiState.examplePaywallProducts,
                onUiEvent = onUiEvent
            )

            ListActionTile(
                title = "Refresh",
                onClick = {
                    onUiEvent(AppUiEvent.OnClickRefreshExamplePaywall)
                }
            )
        }
    }
}


@Composable
private fun LogoutSection(onLogout: () -> Unit) {
    ListSection(
        headerText = null,
        footerText = null
    ) {
        ListActionTile(
            title = "Logout",
            titleColor = Color.Red,
            onClick = onLogout
        )
    }
}


@Composable
private fun CustomPaywallSection(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit,
) {
    ListSection(
        headerText = "Custom Paywall",
        footerText = "Here you can load any paywall by its id and inspect the contents"
    ) {
        PaywallFetchPolicySelector(
            selectedPolicy = uiState.selectedPolicy,
            onPolicySelected = {
                onUiEvent(AppUiEvent.OnNewFetchPolicySelected(it))
            }
        )

        if (uiState.customPaywall == null) {
            ListTextTile(title = "No Paywall Loaded")

            ListTextFieldTile(
                placeholder = "Enter Paywall Locale",
                value = uiState.customPaywallLocale,
                onChanged = {
                    onUiEvent(AppUiEvent.OnClickCustomPaywallLocaleInputChanged(it))
                }
            )

            ListTextFieldTile(
                placeholder = "Enter Paywall Id",
                value = uiState.customPaywallId,
                onChanged = {
                    onUiEvent(AppUiEvent.OnClickCustomPaywallIdInputChanged(it))
                },
                onSubmitted = {
                    onUiEvent(AppUiEvent.OnClickLoadCustomPaywall)
                }
            )

            ListActionTile(
                title = "Load",
                isActive = uiState.customPaywallId.isNotEmpty(),
                onClick = {
                    onUiEvent(AppUiEvent.OnClickLoadCustomPaywall)
                }
            )
        } else {
            ListTextTile(title = "Paywall Id", subtitle = uiState.customPaywall.placementId)

            PaywallContentSection(
                paywall = uiState.customPaywall,
                products = uiState.customPaywallProducts,
                onUiEvent = onUiEvent
            )

            ListActionTile(
                title = "Reload",
                isActive = uiState.customPaywallId.isNotEmpty(),
                onClick = {
                    onUiEvent(AppUiEvent.OnClickLoadCustomPaywall)
                }
            )

            ListActionTile(
                title = "Reset",
                isActive = uiState.customPaywallId.isNotEmpty(),
                onClick = {
                    onUiEvent(AppUiEvent.OnClickResetCustomPaywall)
                }
            )
        }
    }
}

@Composable
private fun OtherActionsSection(
    uiState: AppUiState,
    onUiEvent: (AppUiEvent) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    ListSection(
        headerText = "Other Actions",
        footerText = null
    ) {
        ListActionTile(
            title = "Restore Purchases",
            isActive = !uiState.isLoading,
            onClick = {
                onUiEvent(AppUiEvent.OnClickRestorePurchases)
            }
        )

        ListActionTile(
            title = "Update Profile",
            onClick = {
                onUiEvent(AppUiEvent.OnClickUpdateProfile)
            }
        )

        ListActionTile(
            title = "Set Integration Identifier",
            onClick = {
                onUiEvent(AppUiEvent.OnClickSetIntegrationIdentifier)
            }
        )

        ListActionTile(
            title = "Update Attribution",
            onClick = {
                onUiEvent(AppUiEvent.OnClickUpdateAttribution)
            }
        )

        ListActionTile(
            title = "Send Onboarding Order 1",
            isActive = !uiState.isLoading,
            onClick = {
                onUiEvent(AppUiEvent.OnClickLogShowOnBoarding)
            }
        )

        ListActionTile(
            title = "Present Code Redemption Sheet",
            onClick = {

                coroutineScope.launch {
                    AppLogger.d("Invoking present code redemption sheet")

                    Adapty.presentCodeRedemptionSheet(onError = {
                        AppLogger.e("PaywallContentSection, presentCodeRedemptionSheet, error: $it")
                    })
                }
            }
        )
    }
}

@Composable
private fun PaywallContentSection(
    paywall: AdaptyPaywall,
    products: List<AdaptyPaywallProduct>?,
    onUiEvent: (AppUiEvent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column {
        ListTextTile(title = "Name", subtitle = paywall.name)
        ListTextTile(title = "Variation", subtitle = paywall.variationId)
        ListTextTile(title = "Revision", subtitle = paywall.revision.toString())
        ListTextTile(title = "Locale", subtitle = paywall.remoteConfig?.locale.orEmpty())

        if (products == null) {
            paywall.vendorProductIds.forEach {
                ListTextTile(title = it)
            }
        } else {
            products.forEach { product ->
                ListProductTile(
                    product = product,
                    onClick = { onUiEvent(AppUiEvent.OnClickProduct(product)) }
                )
            }
        }

        ListActionTile(
            title = "Log Show Paywall",
            onClick = {
                onUiEvent(AppUiEvent.OnClickLogShowPaywall(paywall))
            }
        )

        ListActionTile(
            title = "Report Transaction",
            onClick = {
                onUiEvent(
                    AppUiEvent.OnClickReportTransaction(
                        transactionId = Constants.TEST_TRANSACTION_ID,
                        variationId = paywall.variationId
                    )
                )
            }
        )

        if (paywall.hasViewConfiguration) {
            ListActionTile(
                title = "Present View",
                onClick = {
                    coroutineScope.launch {
                        try {
                            AppLogger.d("Invoking createPaywallView, paywall: $paywall")
                            val paywallView = AdaptyUI.createPaywallView(paywall = paywall)
                            paywallView?.present()
                        } catch (e: Exception) {
                            AppLogger.e("PaywallContentSection, createPaywallView, error: $e")
                        }

                    }
                }
            )
        }

        ListActionTile(
            title = "Open Web Paywall",
            onClick = {
                coroutineScope.launch {
                    AppLogger.d("Invoking openWebPaywall, paywall: $paywall")
                    Adapty.openWebPaywall(paywall = paywall, onError = {
                        AppLogger.e("PaywallContentSection, openWebPaywall, error: $it")
                    })
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallFetchPolicySelector(
    selectedPolicy: AppUiState.DemoPaywallFetchPolicy,
    onPolicySelected: (AppUiState.DemoPaywallFetchPolicy) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column {
                AppUiState.DemoPaywallFetchPolicy.entries.forEach { policy ->
                    ListItem(
                        modifier = Modifier.clickable {
                            onPolicySelected(policy)
                            showSheet = false
                        },
                        headlineContent = { Text(policy.title()) }
                    )
                }
                ListItem(
                    modifier = Modifier.clickable { showSheet = false },
                    headlineContent = { Text("Cancel") }
                )
            }
        }
    }

    ListActionTile(
        title = "Fetch Policy",
        subtitle = selectedPolicy.title(),
        onClick = { showSheet = true }
    )
}

