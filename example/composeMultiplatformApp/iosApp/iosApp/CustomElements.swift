import AdaptyUI
import AdaptyUIBuilder
import SwiftUI

@MainActor
struct ExampleCustomElements: AdaptyUICustomElementsResolver {
    func view(forType type: String, id: String, context: AdaptyUICustomElementContext) -> AnyView? {
        switch type {
        case "review_card":
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
