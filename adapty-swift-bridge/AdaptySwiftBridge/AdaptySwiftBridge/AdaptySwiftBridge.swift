//
//  AdaptySwiftBridge.swift
//  AdaptySwiftBridge
//
//  Created by Mirzamehdi on 14/04/2025.
//

import Foundation
import AdaptyPlugin

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
            let handler = KMPAdaptySwiftEventHandler(onEvent: onEvent)
            AdaptyPlugin.reqister(eventHandler: handler)
        }
        
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
