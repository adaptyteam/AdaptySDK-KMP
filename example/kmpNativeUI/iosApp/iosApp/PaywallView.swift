import SwiftUI
import Shared

/// Wraps the native Adapty flow UIViewController in SwiftUI.
///
/// Uses `AdaptyUI.shared.createNativeFlowView(flow:observer:)` from the
/// core `adapty` KMP module to create a native flow view, then wraps its
/// `viewController` property with `UIViewControllerRepresentable`.
struct PaywallView: View {
    let flow: AdaptyFlow
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
                flow: flow,
                onDismiss: { isPresented = false }
            )
        }
    }
}

private struct PaywallViewControllerRepresentable: UIViewControllerRepresentable {
    let flow: AdaptyFlow
    let onDismiss: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        let observer = PaywallObserver(onDismiss: onDismiss)
        context.coordinator.observer = observer

        let nativeView = AdaptyUI.shared.createNativeFlowView(
            flow: flow,
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
        var nativeView: AdaptyNativeFlowView?
        var observer: PaywallObserver?
    }
}

/// Subclasses the Kotlin `AdaptyUIFlowsEventsObserverAdapter` which implements
/// `AdaptyUIFlowsEventsObserver` with all defaults (including `mainUiScope`).
/// Only the callbacks we care about are overridden here.
private class PaywallObserver: AdaptyUIFlowsEventsObserverAdapter {
    let onDismiss: () -> Void

    init(onDismiss: @escaping () -> Void) {
        self.onDismiss = onDismiss
        super.init()
    }

    override func flowViewDidPerformAction(view: AdaptyUIFlowView, action: any AdaptyUIAction) {
        print("[NativeUI] Flow action: \(action)")
        if action is AdaptyUIActionCloseAction {
            DispatchQueue.main.async { self.onDismiss() }
        }
    }

    override func flowViewDidFinishPurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        purchaseResult: any AdaptyPurchaseResult
    ) {
        print("[NativeUI] Purchase finished: \(purchaseResult)")
        if !(purchaseResult is AdaptyPurchaseResultUserCanceled) {
            DispatchQueue.main.async { self.onDismiss() }
        }
    }

    override func flowViewDidFailPurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        error: AdaptyError
    ) {
        print("[NativeUI] Purchase failed: \(error.message)")
    }

    override func flowViewDidReceiveError(view: AdaptyUIFlowView, error: AdaptyError) {
        print("[NativeUI] Rendering failed: \(error.message)")
    }
}
