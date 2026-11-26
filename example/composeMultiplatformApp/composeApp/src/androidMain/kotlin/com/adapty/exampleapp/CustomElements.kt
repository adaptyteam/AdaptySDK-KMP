package com.adapty.exampleapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adapty.ui.AdaptyCustomElementContent
import com.adapty.ui.AdaptyCustomElementsResolver

/**
 * Draws the custom elements of flows with Compose. Registered in [AndroidApp] with the native
 * `com.adapty.ui.AdaptyUI.setCustomElementsResolver`. Try it with the `rt.custom.elements` placement.
 *
 * The resolver matches the Type and ID set for the custom element in the Adapty Flow Builder.
 * Returning `null` shows the fallback set for that element in the builder.
 */
class ExampleCustomElementsResolver : AdaptyCustomElementsResolver {

    override fun resolve(type: String, id: String): AdaptyCustomElementContent? = when (type) {
        "review_card" -> AdaptyCustomElementContent.compose { context ->
            // The element can carry strings and a JSON payload from the builder:
            // read them through the context and fall back to defaults when they're not set.
            ReviewCard(
                text = context.annotatedString("text")
                    ?: AnnotatedString("Renewed twice already. Support answered as quickly as promised."),
                author = context.text("author")?.toString() ?: "Marta S.",
                rating = (context.payloadMap?.get("rating") as? Number)?.toInt() ?: 5,
            )
        }

        else -> null
    }
}

@Composable
private fun ReviewCard(text: AnnotatedString, author: String, rating: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row {
            repeat(5) { index ->
                Text(
                    text = if (index < rating) "★" else "☆",
                    color = Color(0xFFFF9500),
                    fontSize = 20.sp,
                )
            }
        }
        Text(text = text, fontSize = 16.sp, color = Color(0xFF1C1C1E))
        Text(
            text = "— $author",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6C6C70),
        )
    }
}
