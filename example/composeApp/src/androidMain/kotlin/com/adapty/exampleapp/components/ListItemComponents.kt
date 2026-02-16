package com.adapty.exampleapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.adapty.exampleapp.AppLogger
import com.adapty.kmp.Adapty
import com.adapty.kmp.models.AdaptyPaywallProduct
import com.adapty.kmp.models.exceptionOrNull
import com.adapty.kmp.models.getOrNull
import kotlinx.coroutines.launch


@Composable
fun ListSection(
    headerText: String? = null,
    footerText: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (!headerText.isNullOrBlank()) {
            Text(
                text = headerText.uppercase(),
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
            )
        }

        Surface(
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }

        if (!footerText.isNullOrBlank()) {
            Text(
                text = footerText,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
        }
    }
}


@Composable
fun ListProductTile(
    product: AdaptyPaywallProduct,
    onClick: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray.copy(alpha = 0.1f))
            .clickable { onClick?.invoke() }
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.localizedTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(
                    text = "Offer: ",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                val offer = product.subscription?.offer
                Text(
                    text = offer?.phases?.joinToString { it.paymentMode.toString() } ?: "No offer",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            var webUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(product) {
                val webPaywallUrlResult = Adapty.createWebPaywallUrl(product = product)

                webPaywallUrlResult.exceptionOrNull()?.let {
                    AppLogger.e("createWebPaywallUrl error: $it")
                }
                webUrl = webPaywallUrlResult.getOrNull()
            }

            Row {
                Text(
                    text = "Web URL: ",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = webUrl ?: "null",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        Adapty.openWebPaywall(product = product)
                    }
                }
            ) {
                Text("Open Web Paywall (with product)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = product.price.localizedString ?: "null",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
fun ListTextTile(
    title: String,
    subtitle: String? = null,
    subtitleColor: Color = Color.Gray
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = subtitle ?: "",
            color = subtitleColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ListTextFieldTile(
    value: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    placeholderColor: Color = Color.Gray,
    onChanged: ((String) -> Unit)? = null,
    onSubmitted: (() -> Unit)? = null,
) {

    TextField(
        value = value,
        onValueChange = {
            onChanged?.invoke(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        placeholder = {
            if (placeholder != null) {
                Text(
                    text = placeholder,
                    color = placeholderColor
                )
            }
        },

        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = {
                onSubmitted?.invoke()
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onChanged?.invoke("") }) {
                    Icon(painterResource(com.adapty.exampleapp.R.drawable.ic_close), contentDescription = "Clear")
                }
            }
        }
    )
}


@Composable
fun ListActionTile(
    title: String,
    titleColor: Color? = null,
    subtitle: String? = null,
    showProgress: Boolean = false,
    isActive: Boolean = true,
    onClick: () -> Unit
) {
    val titleLengthLimit = 30
    val truncatedTitle = if (title.length > titleLengthLimit) {
        title.take(titleLengthLimit) + "…"
    } else title

    val buttonColors = ButtonDefaults.textButtonColors(
        contentColor = titleColor ?: MaterialTheme.colorScheme.primary
    )

    TextButton(
        onClick = { if (isActive) onClick() },
        enabled = isActive,
        colors = buttonColors,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = truncatedTitle,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor ?: LocalContentColor.current
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            if (showProgress) {
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
fun ListToggleTile(
    title: String,
    value: Boolean,
    onChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        Switch(
            checked = value,
            onCheckedChange = { onChanged(it) }
        )
    }
}

