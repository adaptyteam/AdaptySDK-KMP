import AdaptyUI
import SwiftUI
import ComposeApp


class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        
        AppInitializer.shared.initialize()

        // Flows draw their custom elements with this resolver (see CustomElements.swift).
        // AdaptyUI here is the native iOS module (Swift package AdaptySDK-iOS), not the KMP one.
        AdaptyUI.setCustomElementsResolver(ExampleCustomElements())
        
        return true
    }
    
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
