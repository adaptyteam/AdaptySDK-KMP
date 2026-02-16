import UIKit
import SwiftUI
import ComposeApp

struct ContentView: View {

    @State private var inputText: String = ""

    var body: some View {
        VStack(spacing: 24) {

            TextField("Enter text", text: $inputText)
                .textFieldStyle(.roundedBorder)

            Button("Present As Component") {
                
            }

            Button("Present Full Screen") {
                AdaptyUiHandler.shared.showFullScreenPaywallView(placementId: inputText)
            }
            
            Button("Present As Page Sheet") {
                AdaptyUiHandler.shared.showModalSheetPaywallView(placementId: inputText)
            }


        }
        .padding()
    }

}



