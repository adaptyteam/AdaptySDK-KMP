import SwiftUI
import ComposeApp
import Adapty


class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        
        
        //Below is not needed, just added to make sure Adapty native ios sdk codes are avaialable
        let configurationBuilder = AdaptyConfiguration
              .builder(withAPIKey: "YOUR_PUBLIC_SDK_KEY") // Get from
              .with(logLevel: .verbose)
        
        AppInitializer.shared.initialize()
        
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




