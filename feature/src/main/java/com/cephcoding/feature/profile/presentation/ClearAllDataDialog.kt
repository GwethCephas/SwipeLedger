package com.cephcoding.feature.profile.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cephcoding.core.ui.theme.CoralDestructive

@Composable
fun ClearAllDataDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear All Transaction Data?") },
        text = { Text("This action is permanent and cannot be undone. Are you sure?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Clear All", color = CoralDestructive)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
