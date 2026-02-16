package com.adapty.exampleapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.adapty.exampleapp.AppLogger

@Composable
fun LogsScreen(modifier: Modifier = Modifier) {
    val logs by remember { mutableStateOf(AppLogger.logs) }
    val clipboardManager = LocalClipboardManager.current
    LazyColumn(modifier = modifier.fillMaxSize()) {
        //Copy Logs Button
        item {
            Button(modifier = Modifier.fillMaxWidth().padding(20.dp), onClick = {
                //Copy to clipboard
                clipboardManager.setText(AnnotatedString(logs.joinToString("/n")))
            }) {
                Text(("Copy Logs"))
            }
        }

        logs.forEach {
            item() {
                Text(it,modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp))
            }
        }
    }

}