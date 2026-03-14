import SwiftUI
import Shared

/// Wraps the native Adapty onboarding UIViewController in SwiftUI.
///
/// Uses `AdaptyUI.shared.createNativeOnboardingView(onboarding:observer:)` from
/// the core `adapty` KMP module to create a native onboarding view, then wraps
/// its `viewController` property with `UIViewControllerRepresentable`.
struct OnboardingView: View {
    let onboarding: AdaptyOnboarding
    @Binding var isPresented: Bool

    var body: some View {
        VStack(spacing: 0) {
            // App-owned header — shows this is an embedded native view, not a fullscreen takeover
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Embedded Onboarding")
                        .font(.headline)
                    Text("Native platform view below")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                Spacer()
                Button("Close") { isPresented = false }
            }
            .padding()
            .background(Color(.systemBackground))

            Divider()

            // The native Adapty onboarding rendered as an embedded platform view
            OnboardingViewControllerRepresentable(
                onboarding: onboarding,
                onDismiss: { isPresented = false }
            )
        }
    }
}

private struct OnboardingViewControllerRepresentable: UIViewControllerRepresentable {
    let onboarding: AdaptyOnboarding
    let onDismiss: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        let observer = OnboardingObserver(onDismiss: onDismiss)
        context.coordinator.observer = observer

        let nativeView = AdaptyUI.shared.createNativeOnboardingView(
            onboarding: onboarding,
            observer: observer,
            externalUrlsPresentation: .inAppBrowser
        )
        context.coordinator.nativeView = nativeView
        return nativeView.viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    static func dismantleUIViewController(_ uiViewController: UIViewController, coordinator: Coordinator) {
        coordinator.nativeView?.dispose()
    }

    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    class Coordinator {
        var nativeView: AdaptyNativeOnboardingView?
        var observer: OnboardingObserver?
    }
}

/// Subclasses the Kotlin `AdaptyUIOnboardingsEventsObserverAdapter` which implements
/// `AdaptyUIOnboardingsEventsObserver` with all defaults (including `mainUiScope`).
/// Only the callbacks we care about are overridden here.
private class OnboardingObserver: AdaptyUIOnboardingsEventsObserverAdapter {
    let onDismiss: () -> Void

    init(onDismiss: @escaping () -> Void) {
        self.onDismiss = onDismiss
        super.init()
    }

    override func onboardingViewOnCloseAction(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        actionId: String
    ) {
        print("[NativeUI] Onboarding close: \(actionId)")
        DispatchQueue.main.async { self.onDismiss() }
    }

    override func onboardingViewDidFailWithError(
        view: AdaptyUIOnboardingView,
        error: AdaptyError
    ) {
        print("[NativeUI] Onboarding error: \(error.message)")
    }

    override func onboardingViewOnAnalyticsEvent(
        view: AdaptyUIOnboardingView,
        meta: AdaptyUIOnboardingMeta,
        event: any AdaptyOnboardingsAnalyticsEvent
    ) {
        print("[NativeUI] Onboarding analytics: \(event)")
    }
}
