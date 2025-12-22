//
//  AdaptySwiftBridge.swift
//  AdaptySwiftBridge
//
//  Created by Mirzamehdi on 14/04/2025.
//

import Foundation
import AdaptyPlugin
import AdaptyUI
import Adapty
import SwiftUI
import UIKit

@objc class AdaptySwiftBridge: NSObject {
    
    @objc public static func execute(
            method: String,
            withJson jsonString: String,
            completion: @escaping (String) -> Void
        ) {
            AdaptyPlugin.execute(method: method, withJsonString: jsonString) { resultData in completion(resultData.asAdaptyJsonString)
            }
        }
    
    @objc public static func initialize(onEvent: @escaping (String, String) -> Void) {
        
        Task{ @MainActor in
            
            let assetResolver: (String) -> URL? = { assetId in
                let nsStr = assetId as NSString
                let filename = nsStr.deletingPathExtension
                let ext = nsStr.pathExtension.isEmpty ? nil : nsStr.pathExtension

                return Bundle.main.url(forResource: filename, withExtension: ext)
            }
            
            if #available(iOS 15.0, *) {
                AdaptyPlugin.register(createPaywallView: assetResolver)
            }

            AdaptyPlugin.register(setFallbackRequests: assetResolver)

            let handler = KMPAdaptySwiftEventHandler(onEvent: onEvent)
            AdaptyPlugin.register(eventHandler: handler)
            
        }
        
    }
    
    
    
    @objc public static func createNativeOnboardingView(
        jsonString: String,
        id: String,
        onEvent: @escaping (String, String) -> Void
    ) -> UIViewController {
        let containerView = UIViewController()
        let handler = KMPAdaptySwiftEventHandler(onEvent: onEvent)

        if #available(iOS 15.0, macOS 12.0, tvOS 15.0, watchOS 8.0, visionOS 1.0, *){
            Task { @MainActor in
                do {
                    // Build configuration from onboarding object
                    let configuration = try await AdaptyPlugin.getOnboardingViewConfiguration(withJson: jsonString)
                    
                    // Create the actual onboarding view
                    let uiView = AdaptyOnboardingPlatformViewWrapper(
                        viewId: id,
                        eventHandler: handler,
                        configuration: configuration
                    )
                    
                    containerView.view.addSubview(uiView)
                    
                    // Auto-layout to fill parent
                    uiView.translatesAutoresizingMaskIntoConstraints = false
                    NSLayoutConstraint.activate([
                        uiView.leadingAnchor.constraint(equalTo: containerView.view.leadingAnchor),
                        uiView.trailingAnchor.constraint(equalTo: containerView.view.trailingAnchor),
                        uiView.topAnchor.constraint(equalTo: containerView.view.topAnchor),
                        uiView.bottomAnchor.constraint(equalTo: containerView.view.bottomAnchor),
                    ])
                } catch {
                    print("Failed to get onboarding configuration: \(error)")
                }
            }
        } else {
            print("Onboarding Native UI requires iOS 15.0 or later.")
        }

        return containerView
    }

    @objc public static func createNativePaywallView(
        jsonString: String,
        id: String,
        onEvent: @escaping (String, String) -> Void
    ) -> UIViewController {
        let containerView = UIViewController()
        let handler = KMPAdaptySwiftEventHandler(onEvent: onEvent)

        if #available(iOS 15.0, macOS 12.0, tvOS 15.0, watchOS 8.0, visionOS 1.0, *){
            Task { @MainActor in
               
                do {
                    
                    let configuration = try await AdaptyPlugin.getPaywallViewConfiguration(withJson: jsonString)
                    
                    // Create the actual paywall view
                    let uiView = AdaptyPaywallPlatformViewWrapper(
                        viewId: id,
                        eventHandler: handler,
                        configuration: configuration,
                        parentVC: containerView
                    )

                    containerView.view.addSubview(uiView)

                    // Auto-layout to fill parent
                    uiView.translatesAutoresizingMaskIntoConstraints = false
                    NSLayoutConstraint.activate([
                        uiView.leadingAnchor.constraint(equalTo: containerView.view.leadingAnchor),
                        uiView.trailingAnchor.constraint(equalTo: containerView.view.trailingAnchor),
                        uiView.topAnchor.constraint(equalTo: containerView.view.topAnchor),
                        uiView.bottomAnchor.constraint(equalTo: containerView.view.bottomAnchor),
                    ])
                } catch {
                    print("Failed to get paywall configuration: \(error)")
                }
            }
        } else {
            print("Paywall Native UI requires iOS 15.0 or later.")
        }

        return containerView
    }

    
    
}

final class KMPAdaptySwiftEventHandler: EventHandler {
    
    private let onEvent: (String, String) -> Void
    
    init(onEvent: @escaping (String, String) -> Void) {
        self.onEvent = onEvent
    }
    
    func handle(event: AdaptyPluginEvent) {
        do {
            let eventName = event.id
            let eventData = try event.asAdaptyJsonData.asAdaptyJsonString

            onEvent(eventName, eventData)
       } catch {
            print("⚠️ Failed to handle AdaptyPluginEvent: \(error.localizedDescription)")
       }
    
    }
    
    
}
