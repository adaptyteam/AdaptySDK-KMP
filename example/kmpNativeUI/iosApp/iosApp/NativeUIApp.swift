import SwiftUI
import Shared

@main
struct NativeUIApp: App {
    init() {
        // Initialize Adapty SDK on app launch.
        // Uses API key from BuildConfig in the shared module.
        AdaptyManager.shared.initialize()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
