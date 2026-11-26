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

        if #available(iOS 16.0, macOS 13.0, tvOS 16.0, watchOS 9.0, visionOS 1.0, *){
            Task { @MainActor in
               
                do {
                    
                    let configuration = try await AdaptyPlugin.getFlowViewConfiguration(withJson: jsonString)

                    // Create the actual flow view
                    let uiView = AdaptyFlowPlatformViewWrapper(
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
                    // The view stays empty: report the error createFlowView would fail with
                    print("Failed to get flow configuration: \(error)")
                    handler.handle(event: FlowViewErrorEvent(
                        view: FlowViewIdentity(viewId: id, flowJson: jsonString),
                        error: FlowViewError(error)
                    ))
                }
            }
        } else {
            print("Adapty flows require iOS 16.0 or newer")
            Task { @MainActor in
                handler.handle(event: FlowViewErrorEvent(
                    view: FlowViewIdentity(viewId: id, flowJson: jsonString),
                    error: .ui(.platformNotSupported)
                ))
            }
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

/// `flow_view_did_receive_error` of an embedded flow view that never showed its flow:
/// below iOS 16, or when the flow fails to load.
private struct FlowViewErrorEvent: AdaptyPluginEvent {
    let id = "flow_view_did_receive_error"
    let view: FlowViewIdentity
    let error: FlowViewError

    enum CodingKeys: String, CodingKey {
        case id
        case view
        case error
    }
}

/// Error of an embedded flow view, encoded like the bridge's own errors
/// (`adapty_code`, `message`, `detail`) with the code createFlowView fails with.
private enum FlowViewError: Encodable {
    case adapty(AdaptyError)
    case ui(AdaptyUIError)
    case plugin(AdaptyPluginError)
    case other(code: Int, message: String, detail: String)

    init(_ error: Error) {
        switch error {
        case let error as AdaptyError:
            self = .adapty(error)
        case let error as AdaptyUIError:
            self = .ui(error)
        case let error as AdaptyPluginError:
            self = .plugin(error)
        case let error as DecodingError:
            let message = "Request params of method:adapty_ui_create_flow_view is invalid"
            self = .other(
                code: AdaptyError.ErrorCode.decodingFailed.rawValue,
                message: "\(message): \(error.localizedDescription)",
                detail: "AdaptyPluginError.decodingFailed: \(message) \(String(describing: error))"
            )
        default:
            self = .other(
                code: AdaptyError.ErrorCode.unknown.rawValue,
                message: "Unknown: \(error.localizedDescription)",
                detail: "AdaptyPluginError.unknown: \(String(describing: error))"
            )
        }
    }

    private enum CodingKeys: String, CodingKey {
        case code = "adapty_code"
        case message
        case detail
    }

    func encode(to encoder: Encoder) throws {
        switch self {
        case let .adapty(error):
            try error.encode(to: encoder)
        case let .ui(error):
            try error.encode(to: encoder)
        case let .plugin(error):
            try error.encode(to: encoder)
        case let .other(code, message, detail):
            var container = encoder.container(keyedBy: CodingKeys.self)
            try container.encode(code, forKey: .code)
            try container.encode(message, forKey: .message)
            try container.encode(detail, forKey: .detail)
        }
    }
}

/// `AdaptyUI.FlowView` of an embedded view that was never built: the view id and
/// the placement and variation of the flow from the create request.
private struct FlowViewIdentity: Encodable {
    let id: String
    let placementId: String
    let variationId: String

    enum CodingKeys: String, CodingKey {
        case id
        case placementId = "placement_id"
        case variationId = "variation_id"
    }

    init(viewId: String, flowJson: String) {
        let params = flowJson.data(using: .utf8)
            .flatMap { try? JSONSerialization.jsonObject(with: $0) as? [String: Any] }
        let flow = params?["flow"] as? [String: Any]
        let placement = flow?["placement"] as? [String: Any]

        id = viewId
        placementId = placement?["developer_id"] as? String ?? ""
        variationId = flow?["variation_id"] as? String ?? ""
    }
}
