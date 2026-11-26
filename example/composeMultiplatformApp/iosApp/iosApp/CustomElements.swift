import AdaptyUI
import AdaptyUIBuilder
import SwiftUI

/// Draws the custom elements of flows with SwiftUI. Registered in `iOSApp.swift` with
/// `AdaptyUI.setCustomElementsResolver`. Try it with the `rt.custom.elements` placement.
///
/// The resolver matches the Type and ID set for the custom element in the Adapty Flow Builder.
/// Returning `nil` shows the fallback set for that element in the builder.
@MainActor
struct ExampleCustomElements: AdaptyUICustomElementsResolver {
    func view(forType type: String, id: String, context: AdaptyUICustomElementContext) -> AnyView? {
        switch type {
        case "review_card":
            // The element can carry strings and a JSON payload from the builder:
            // read them through the context and fall back to defaults when they're not set.
            let text = context.attributedString("text")
                ?? AttributedString("Renewed twice already. Support answered as quickly as promised.")
            let author = context.attributedString("author").map { String($0.characters) } ?? "Marta S."
            let rating = context.payloadDictionary?["rating"] as? Int ?? 5

            return AnyView(ReviewCard(text: text, author: author, rating: rating))
        default:
            return nil
        }
    }
}

private struct ReviewCard: View {
    let text: AttributedString
    let author: String
    let rating: Int

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 2) {
                ForEach(0..<5, id: \.self) { index in
                    Image(systemName: index < rating ? "star.fill" : "star")
                        .foregroundColor(.orange)
                }
            }
            Text(text)
                .font(.body)
            Text("— \(author)")
                .font(.subheadline.weight(.semibold))
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
