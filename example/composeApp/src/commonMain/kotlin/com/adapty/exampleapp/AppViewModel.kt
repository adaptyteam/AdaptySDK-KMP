@file:OptIn(ExperimentalTime::class)

package com.adapty.exampleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adapty.kmp.Adapty
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.OnInstallationDetailsListener
import com.adapty.kmp.models.AdaptyCustomAsset
import com.adapty.kmp.models.AdaptyError
import com.adapty.kmp.models.AdaptyInstallationDetails
import com.adapty.kmp.models.AdaptyInstallationStatusDetermined
import com.adapty.kmp.models.AdaptyOnboarding
import com.adapty.kmp.models.AdaptyPaywall
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyProfileParameters
import com.adapty.kmp.models.AdaptyPurchaseResult
import com.adapty.kmp.models.AdaptyUIIOSPresentationStyle
import com.adapty.kmp.models.AdaptyWebPresentation
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class AppViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<AppUiState> =
        MutableStateFlow(AppUiState(isLoading = true))
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update { it.copy(error = throwable) }
    }

    init {
        initialize()
    }

    private fun initialize() = viewModelScope.launch {
        delay(1000) //Adapty Initialization takes some time
        checkIfAdaptyIsActivated()
        Adapty.setOnProfileUpdatedListener { profile ->
            _uiState.update { it.copy(isLoading = false, adaptyProfile = profile) }
        }
        Adapty.setOnInstallationDetailsListener(object : OnInstallationDetailsListener {
            override fun onInstallationDetailsSuccess(details: AdaptyInstallationDetails) {
                AppLogger.d("Installation details event : $details")
                _uiState.update {
                    it.copy(
                        installationStatus = AdaptyInstallationStatusDetermined(details = details)
                    )
                }
            }

            override fun onInstallationDetailsFailure(error: AdaptyError) {
                AppLogger.e("Installation details error: $error")
            }

        })
        reloadProfile()
//        loadExamplePaywallAndProducts()
//        loadSavedPaywalls()
    }

    fun onErrorDialogDismissed() = viewModelScope.launch(exceptionHandler) {
        _uiState.update { it.copy(error = null) }
    }

    fun onUiEvent(event: AppUiEvent) = viewModelScope.launch {
        when (event) {
            is AppUiEvent.OnClickLogout -> onClickLogout()
            is AppUiEvent.OnCustomerUserIdInputChanged -> {
                _uiState.update { it.copy(userEnteredCustomerUserId = event.value) }
            }

            is AppUiEvent.OnNewFetchPolicySelected -> {
                _uiState.update { it.copy(selectedPolicy = event.policy) }
            }

            AppUiEvent.OnClickLoadCustomPaywall -> {
                loadCustomPaywall()
            }

            AppUiEvent.OnClickRestorePurchases -> {
                restorePurchases()
            }

            AppUiEvent.OnClickResetCustomPaywall -> {
                _uiState.update {
                    it.copy(
                        customPaywall = null,
                        customPaywallId = "",
                        customPaywallLocale = "",
                        customPaywallProducts = emptyList()
                    )
                }
            }

            is AppUiEvent.OnClickCustomPaywallIdInputChanged -> {
                _uiState.update { it.copy(customPaywallId = event.value) }
            }

            is AppUiEvent.OnClickCustomPaywallLocaleInputChanged -> {
                _uiState.update { it.copy(customPaywallLocale = event.value) }
            }

            AppUiEvent.OnClickRefreshExamplePaywall -> {
                loadExamplePaywallAndProducts()
            }

            AppUiEvent.OnClickIdentifyCustomerUserId -> {
                val customerUserId = uiState.value.userEnteredCustomerUserId
                if (customerUserId.isNotBlank()) {
                    Adapty.identify(customerUserId.trim())
                }
            }

            AppUiEvent.OnClickReloadProfile -> {
                reloadProfile()
            }

            AppUiEvent.OnClickUpdateProfile -> {
                updateProfile()
            }

            AppUiEvent.OnClickSetIntegrationIdentifier -> {
                _uiState.update { it.copy(isLoading = true) }
                Adapty.setIntegrationIdentifier(
                    key = "test_integration_identifier",
                    value = "test_value"
                )
                _uiState.update { it.copy(isLoading = false) }
            }

            is AppUiEvent.OnClickUpdateAttribution -> {
                _uiState.update { it.copy(isLoading = true) }
                Adapty.updateAttribution(
                    attribution = mapOf("test_key" to "test_value"),
                    source = "custom"
                )
                _uiState.update { it.copy(isLoading = false) }
            }

            is AppUiEvent.OnClickLogShowPaywall -> {
                AppLogger.d("Invoking Log Show Paywall: ${event.paywall}")
                Adapty.logShowPaywall(paywall = event.paywall).onError { error ->
                    AppLogger.d("Log Show Paywall Error: $error")
                }
            }

            is AppUiEvent.OnClickProduct -> purchaseProduct(event.product)
            is AppUiEvent.OnClickReportTransaction -> reportTransaction(
                variationId = event.variationId,
                transactionId = event.transactionId
            )

            is AppUiEvent.OnClickUpdateConsent -> {
                Adapty.updateCollectingRefundDataConsent(event.consent).onError { error ->
                    _uiState.update { it.copy(error = error) }
                }
            }

            is AppUiEvent.OnClickUpdateRefundPreference -> {
                Adapty.updateRefundPreference(event.refundPreference)
                    .onError { error ->
                        _uiState.update { it.copy(error = error) }
                    }
            }

            is AppUiEvent.OnNewPaywallIdAdded -> {
                _uiState.update { currentState ->
                    currentState.copy(savedPaywallIds = setOf(event.paywallId) + currentState.savedPaywallIds)
                }
                loadSavedPaywalls()
            }

            is AppUiEvent.OnClickPresentPaywallView -> {
                presentPaywallView(paywall = event.paywall)
            }

            is AppUiEvent.CreateAndPresentPaywallView -> createAndPresentPaywallView(
                paywall = event.paywall,
                loadProducts = event.loadProducts,
                iosPresentationStyle = event.iosPresentationStyle
            )

            is AppUiEvent.OnNewOnboardingIdAdded -> {
                _uiState.update { currentState ->
                    currentState.copy(savedOnboardingIds = setOf(event.onboardingId) + currentState.savedOnboardingIds)
                }
                loadSavedOnBoardings()
            }

            is AppUiEvent.OnChangeOnboardingLocale -> {
                _uiState.update { currentState ->
                    currentState.copy(onboardingLocale = event.locale)
                }
            }

            is AppUiEvent.OnClickPresentOnboarding -> createAndPresentOnboarding(
                onboarding = event.onboarding,
                presentationStyle = event.presentationStyle,
                externalUrlsPresentation = event.externalUrlsPresentation
            )

            is AppUiEvent.OnClickPresentOnboardingNativeView -> showOnboardingNativeView(event.onboarding)
            is AppUiEvent.OnClickPresentPaywallNativeView -> showNativePaywallView(event.paywall)
            AppUiEvent.OnToggleOnboardingShowToastEvents -> {
                _uiState.update { currentState ->
                    currentState.copy(showOnboardingToastEvents = !currentState.showOnboardingToastEvents)
                }
            }

            AppUiEvent.OnClickUpdateInstallationDetails -> {
                getCurrentInstallationStatus()
            }

            AppUiEvent.OnCloseNativeOnboardingView -> {
                _uiState.update { it.copy(nativeOnboardingView = null) }
            }

            AppUiEvent.OnCloseNativePaywallView -> {
                _uiState.update { it.copy(nativePaywallView = null) }
            }

        }
    }

    private fun loadSavedPaywalls() = viewModelScope.launch {
        _uiState.value.savedPaywallIds.forEach { id ->
            loadPaywallData(id)
        }
    }

    private fun loadSavedOnBoardings() = viewModelScope.launch {
        _uiState.value.savedOnboardingIds.forEach { id ->
            loadOnboardingData(id)
        }
    }

    private fun loadPaywallData(id: String) = viewModelScope.launch {

        Adapty.getPaywall(placementId = id)
            .onSuccess {
                _uiState.update { currentUiState ->
                    val updatedPaywalls = currentUiState.savedPaywalls.toMutableMap()
                    updatedPaywalls[id] = it
                    currentUiState.copy(savedPaywalls = updatedPaywalls)
                }
            }
            .onError { error ->
                AppLogger.e("Error loading paywall: $error")
                _uiState.update { it.copy(error = error) }
            }

    }

    private fun loadOnboardingData(id: String) = viewModelScope.launch {
        Adapty.getOnboarding(
            placementId = id,
            locale = _uiState.value.onboardingLocale.takeIf { !it.isNullOrBlank() }
        ).onSuccess {
            _uiState.update { currentUiState ->
                val updatedOnboardings = currentUiState.savedOnboardings.toMutableMap()
                updatedOnboardings[id] = it
                currentUiState.copy(savedOnboardings = updatedOnboardings)
            }
        }.onError { error ->
            AppLogger.e("Error loading onboarding: $error")
            _uiState.update { it.copy(error = error) }
        }

    }

    private fun presentPaywallView(paywall: AdaptyPaywall) =
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            AdaptyUI.createPaywallView(paywall = paywall)
                .onSuccess { view -> view.present() }
                .onError { error -> _uiState.update { it.copy(error = error) } }
            _uiState.update { it.copy(isLoading = false) }
        }


    private fun createAndPresentPaywallView(
        paywall: AdaptyPaywall,
        loadProducts: Boolean,
        iosPresentationStyle: AdaptyUIIOSPresentationStyle
    ) =
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }


            val customAssets: Map<String, AdaptyCustomAsset> = mapOf(
//                "hero_image" to AdaptyCustomAsset.localImageResource(
//                    path = localImageResourcePath
//                ),
//                "hero_image" to AdaptyCustomAsset.localImageData(
//                    data = imageByteData
//                ),
//                "hero_video" to AdaptyCustomAsset.localVideoFile(
//                    path = localVideoResourcePath
//                ),
//                "custom_color_orange" to AdaptyCustomAsset.color(
//                    colorHex = "#FFFFA500"
//                ),
//                "custom_bright_gradient" to AdaptyCustomAsset.linearGradient(
//                    colors = listOf(""),
//                )
            )


            AdaptyUI.createPaywallView(
                paywall = paywall,
                preloadProducts = loadProducts,
                customTags = mapOf(
                    "CUSTOM_TAG_NAME" to "Walter White",
                    "CUSTOM_TAG_PHONE" to "+1 234 567890",
                    "CUSTOM_TAG_CITY" to "Albuquerque",
                    "CUSTOM_TAG_EMAIL" to "walter@white.com",
                ),
                customTimers = mapOf(
                    "CUSTOM_TIMER_24H" to Clock.System.now().plus(86400.seconds)
                        .toLocalDateTime(TimeZone.UTC),
                    "CUSTOM_TIMER_10H" to Clock.System.now().plus(36000.seconds)
                        .toLocalDateTime(TimeZone.UTC),
                    "CUSTOM_TIMER_1H" to Clock.System.now().plus(3600.seconds)
                        .toLocalDateTime(TimeZone.UTC),
                    "CUSTOM_TIMER_10M" to Clock.System.now().plus(600.seconds)
                        .toLocalDateTime(TimeZone.UTC),
                    "CUSTOM_TIMER_1M" to Clock.System.now().plus(60.seconds)
                        .toLocalDateTime(TimeZone.UTC),
                    "CUSTOM_TIMER_10S" to Clock.System.now().plus(10.seconds)
                        .toLocalDateTime(TimeZone.UTC),
                    "CUSTOM_TIMER_5S" to Clock.System.now().plus(5.seconds)
                        .toLocalDateTime(TimeZone.UTC)
                ),
                customAssets = customAssets
            ).onSuccess { view ->
                view.present(iosPresentationStyle = iosPresentationStyle)
            }.onError { error ->
                _uiState.update { it.copy(error = error) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }


    private fun checkIfAdaptyIsActivated() = viewModelScope.launch {
        val isActivated = Adapty.isActivated()
        AppLogger.d("Adapty is activated: $isActivated")
    }


    private fun restorePurchases() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        Adapty.restorePurchases()
            .onSuccess { adaptyProfile ->
                AppLogger.d("Restore purchase result: $adaptyProfile")
                _uiState.update { it.copy(isLoading = false, adaptyProfile = adaptyProfile) }
            }.onError { error ->
                AppLogger.e("Restore purchase error: $error")
                _uiState.update { it.copy(error = error, isLoading = false) }
            }

    }

    private fun reportTransaction(variationId: String, transactionId: String) =
        viewModelScope.launch {
            AppLogger.d("Invoking report transaction with variationID: $variationId and transactionID: $transactionId")
            Adapty.reportTransaction(
                transactionId = transactionId,
                variationId = variationId
            )
                .onSuccess {
                    AppLogger.d("Transaction reported: $it")
                }.onError { error ->
                    AppLogger.e("Transaction report error: $error")
                    _uiState.update { it.copy(error = error) }
                }
        }

    private fun purchaseProduct(paywallProduct: AdaptyPaywallProduct) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        AppLogger.d("Purchasing product: $paywallProduct")
        Adapty.makePurchase(paywallProduct).onSuccess { purchaseResult ->
            AppLogger.d("Purchase result: $purchaseResult")
            when (purchaseResult) {
                is AdaptyPurchaseResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        adaptyProfile = purchaseResult.profile
                    )
                }

                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }.onError { error ->
            AppLogger.e("Purchase error: $error")
            _uiState.update { it.copy(isLoading = false, error = error) }
        }

    }

    private fun onClickLogout() = viewModelScope.launch(exceptionHandler) {
        _uiState.update { it.copy(isLoading = true) }
        Adapty.logout()

        _uiState.update {
            it.copy(
                isLoading = false,
                adaptyProfile = null,
                examplePaywall = null,
                examplePaywallProducts = emptyList()
            )
        }
        reloadProfile()
        loadExamplePaywallAndProducts()
    }

    private fun updateProfile() = viewModelScope.launch(exceptionHandler) {
        AppLogger.d("Updating profile...")
        _uiState.update { it.copy(isLoading = true) }


        val profileParameters = AdaptyProfileParameters.Builder()
            .withFirstName("John")
            .withLastName("Doe")
            .withBirthday(AdaptyProfile.Date(1990, 1, 1))
            .withGender(AdaptyProfile.Gender.FEMALE)
            .withEmail("example@adapty.io")
            .withCustomAttribute("custom_attribute_key_string", "custom_attribute_value")
            .withCustomAttribute("test_double_key", 12.0)
            .build()

        Adapty.updateProfile(params = profileParameters)
            .onSuccess {
                _uiState.update { it.copy(isLoading = false) }
            }
            .onError { error ->
                AppLogger.e("Profile update error: $error")
                _uiState.update { it.copy(error = error, isLoading = false) }
            }
    }

    private fun reloadProfile() = viewModelScope.launch(exceptionHandler) {
        AppLogger.d("Fetching profile...")
        _uiState.update { it.copy(isLoading = true) }
        Adapty.getProfile()
            .onSuccess { adaptyProfile ->
                AppLogger.d("Profile is updated: $adaptyProfile")
                _uiState.update { it.copy(adaptyProfile = adaptyProfile, isLoading = false) }
            }.onError { adaptyError ->
                _uiState.update { it.copy(error = adaptyError, isLoading = false) }
            }
    }

    private fun getCurrentInstallationStatus() = viewModelScope.launch(exceptionHandler) {
        AppLogger.d("Getting current installation status...")
        Adapty.getCurrentInstallationStatus()
            .onSuccess { installationStatus ->
                AppLogger.d("Current Installation status is : $installationStatus")
                _uiState.update { it.copy(installationStatus = installationStatus) }
            }.onError { adaptyError ->
                AppLogger.e("Error getting current installation status: $adaptyError")
            }
    }

    private fun loadExamplePaywallAndProducts() = viewModelScope.launch(exceptionHandler) {
        AppLogger.d("Fetching example paywall...")
        _uiState.update { it.copy(isLoading = true) }
        var examplePaywall: AdaptyPaywall? = null
        Adapty.getPaywall(
            placementId = Constants.EXAMPLE_PAYWALL_ID,
            locale = "fr",
            fetchPolicy = _uiState.value.selectedPolicy.toAdaptyPaywallFetchPolicy()

        ).onSuccess { adaptyPaywall ->
            AppLogger.d("Example paywall is loaded: $adaptyPaywall")
            examplePaywall = adaptyPaywall
            _uiState.update { it.copy(examplePaywall = adaptyPaywall, isLoading = false) }
        }.onError { adaptyError ->
            _uiState.update { it.copy(error = adaptyError, isLoading = false) }
        }

        examplePaywall?.let {
            AppLogger.d("Fetching example paywall products...")
            _uiState.update { currentUiState -> currentUiState.copy(isLoading = true) }
            Adapty.getPaywallProducts(paywall = it)
                .onSuccess { products ->
                    AppLogger.d("Example paywall products are loaded: $products")
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            examplePaywallProducts = products,
                            isLoading = false
                        )
                    }
                }.onError { adaptyError ->
                    _uiState.update { it.copy(error = adaptyError, isLoading = false) }
                }
        }
    }

    private fun loadCustomPaywall() = viewModelScope.launch(exceptionHandler) {
        val customPaywallId = _uiState.value.customPaywallId
        if (customPaywallId.isEmpty()) return@launch

        AppLogger.d("Fetching custom paywall...")
        _uiState.update { it.copy(isLoading = true) }
        var paywall: AdaptyPaywall? = null
        Adapty.getPaywall(
            placementId = customPaywallId,
            locale = _uiState.value.customPaywallLocale,
            fetchPolicy = _uiState.value.selectedPolicy.toAdaptyPaywallFetchPolicy()

        ).onSuccess { adaptyPaywall ->
            AppLogger.d("Custom paywall is loaded: $adaptyPaywall")
            paywall = adaptyPaywall
            _uiState.update { it.copy(customPaywall = adaptyPaywall, isLoading = false) }
        }.onError { adaptyError ->
            _uiState.update { it.copy(error = adaptyError, isLoading = false) }
        }

        paywall?.let {
            AppLogger.d("Fetching custom paywall products...")
            _uiState.update { currentUiState -> currentUiState.copy(isLoading = true) }
            Adapty.getPaywallProducts(paywall = it)
                .onSuccess { products ->
                    AppLogger.d("Custom paywall products are loaded: $products")
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            customPaywallProducts = products,
                            isLoading = false
                        )
                    }
                }.onError { adaptyError ->
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            error = adaptyError,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun createAndPresentOnboarding(
        onboarding: AdaptyOnboarding,
        presentationStyle: AdaptyUIIOSPresentationStyle,
        externalUrlsPresentation: AdaptyWebPresentation
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isLoadingOnboard = true) }
        AdaptyUI
            .createOnboardingView(onboarding = onboarding, externalUrlsPresentation = externalUrlsPresentation)
            .onSuccess { view -> view.present(iosPresentationStyle = presentationStyle) }
            .onError { adaptyError ->
                _uiState.update { currentState -> currentState.copy(error = adaptyError) }
            }

        _uiState.update { it.copy(isLoadingOnboard = false) }
    }

    private fun showOnboardingNativeView(onboarding: AdaptyOnboarding) = viewModelScope.launch {
        _uiState.update { it.copy(nativeOnboardingView = onboarding) }
    }

    private fun showNativePaywallView(paywall: AdaptyPaywall) = viewModelScope.launch {
        _uiState.update { it.copy(nativePaywallView = paywall) }
    }


}