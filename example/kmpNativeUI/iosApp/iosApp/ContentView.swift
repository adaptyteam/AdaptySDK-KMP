import SwiftUI
import Shared

struct ContentView: View {
    @State private var placementId = ""
    @State private var isLoading = false
    @State private var showPaywall = false
    @State private var showOnboarding = false
    @State private var paywall: AdaptyPaywall? = nil
    @State private var onboarding: AdaptyOnboarding? = nil
    @State private var errorMessage: String? = nil

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Text("KMP Native UI Example")
                    .font(.title2)
                    .bold()

                Text("Uses adapty module only (no adapty-ui)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                TextField("Placement ID", text: $placementId)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)

                if isLoading {
                    ProgressView()
                } else {
                    Button("Show Native Paywall") {
                        loadPaywall()
                    }
                    .buttonStyle(.borderedProminent)
                    .disabled(placementId.isEmpty)

                    Button("Show Native Onboarding") {
                        loadOnboarding()
                    }
                    .buttonStyle(.borderedProminent)
                    .disabled(placementId.isEmpty)
                }

                if let errorMessage = errorMessage {
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .font(.caption)
                        .padding(.horizontal)
                }

                Spacer()
            }
            .padding(.top, 40)
            .navigationBarTitleDisplayMode(.inline)
        }
        .fullScreenCover(isPresented: $showPaywall) {
            if let paywall = paywall {
                PaywallView(paywall: paywall, isPresented: $showPaywall)
            }
        }
        .fullScreenCover(isPresented: $showOnboarding) {
            if let onboarding = onboarding {
                OnboardingView(onboarding: onboarding, isPresented: $showOnboarding)
            }
        }
    }

    private func loadPaywall() {
        guard !placementId.isEmpty else { return }
        isLoading = true
        errorMessage = nil

        Task {
            let result = try await AdaptyManager.shared.getPaywall(placementId: placementId)
            await MainActor.run {
                isLoading = false
                if let successResult = result as? AdaptyResultSuccess<AdaptyPaywall> {
                    self.paywall = successResult.value
                    self.showPaywall = true
                } else if let errorResult = result as? AdaptyResultError {
                    self.errorMessage = errorResult.error.message
                }
            }
        }
    }

    private func loadOnboarding() {
        guard !placementId.isEmpty else { return }
        isLoading = true
        errorMessage = nil

        Task {
            let result = try await AdaptyManager.shared.getOnboarding(placementId: placementId)
            await MainActor.run {
                isLoading = false
                if let successResult = result as? AdaptyResultSuccess<AdaptyOnboarding> {
                    self.onboarding = successResult.value
                    self.showOnboarding = true
                } else if let errorResult = result as? AdaptyResultError {
                    self.errorMessage = errorResult.error.message
                }
            }
        }
    }
}
