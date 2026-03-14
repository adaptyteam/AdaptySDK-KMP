import SwiftUI
import Shared

/// Wraps the native Adapty paywall UIViewController in SwiftUI.
///
/// Uses `AdaptyUI.shared.createNativePaywallView(paywall:observer:)` from the
/// core `adapty` KMP module to create a native paywall view, then wraps its
/// `viewController` property with `UIViewControllerRepresentable`.
struct PaywallView: View {
    let paywall: AdaptyPaywall
    @Binding var isPresented: Bool

    var body: some View {
        VStack(spacing: 0) {
            // App-owned header — shows this is an embedded native view, not a fullscreen takeover
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Embedded Paywall")
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

            // The native Adapty paywall rendered as an embedded platform view
            PaywallViewControllerRepresentable(
                paywall: paywall,
                onDismiss: { isPresented = false }
            )
        }
    }
}

private struct PaywallViewControllerRepresentable: UIViewControllerRepresentable {
    let paywall: AdaptyPaywall
    let onDismiss: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        let observer = PaywallObserver(onDismiss: onDismiss)
        context.coordinator.observer = observer

        let nativeView = AdaptyUI.shared.createNativePaywallView(
            paywall: paywall,
            observer: observer,
            customTags: nil,
            customTimers: nil,
            customAssets: nil,
            productPurchaseParams: nil
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
        var nativeView: AdaptyNativePaywallView?
        var observer: PaywallObserver?
    }
}

/// Subclasses the Kotlin `AdaptyUIPaywallsEventsObserverAdapter` which implements
/// `AdaptyUIPaywallsEventsObserver` with all defaults (including `mainUiScope`).
/// Only the callbacks we care about are overridden here.
private class PaywallObserver: AdaptyUIPaywallsEventsObserverAdapter {
    let onDismiss: () -> Void

    init(onDismiss: @escaping () -> Void) {
        self.onDismiss = onDismiss
        super.init()
    }

    override func paywallViewDidPerformAction(view: AdaptyUIPaywallView, action: any AdaptyUIAction) {
        print("[NativeUI] Paywall action: \(action)")
        if action is AdaptyUIActionCloseAction {
            DispatchQueue.main.async { self.onDismiss() }
        }
    }

    override func paywallViewDidFinishPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct,
        purchaseResult: any AdaptyPurchaseResult
    ) {
        print("[NativeUI] Purchase finished: \(purchaseResult)")
        if !(purchaseResult is AdaptyPurchaseResultUserCanceled) {
            DispatchQueue.main.async { self.onDismiss() }
        }
    }

    override func paywallViewDidFailPurchase(
        view: AdaptyUIPaywallView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {
        print("[NativeUI] Purchase failed: \(error.message)")
    }

    override func paywallViewDidFailRendering(view: AdaptyUIPaywallView, error: AdaptyError) {
        print("[NativeUI] Rendering failed: \(error.message)")
    }
}
