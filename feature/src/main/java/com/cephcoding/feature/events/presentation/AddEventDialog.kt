package com.cephcoding.feature.events.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cephcoding.core.domain.model.NotificationTiming
import com.cephcoding.core.domain.model.RecurrenceInterval
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        amount: Double,
        dueDateTimestamp: Long,
        recurrenceInterval: RecurrenceInterval,
        notificationTiming: NotificationTiming
    ) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    var dueDateTimestamp by remember {
        mutableStateOf(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis)
    }
    var recurrenceInterval by remember { mutableStateOf(RecurrenceInterval.MONTHLY) }
    var notificationTiming by remember { mutableStateOf(NotificationTiming.DAY_BEFORE) }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.US) }
    val amount = amountText.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Recurring Event") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() || it == '.' } },
                    label = { Text("Amount (Ksh)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {
                    val calendar = Calendar.getInstance().apply { timeInMillis = dueDateTimestamp }
                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            calendar.set(year, month, day)
                            dueDateTimestamp = calendar.timeInMillis
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text("Due date: ${dateFormat.format(Date(dueDateTimestamp))}")
                }
                EnumDropdown(
                    label = "Repeats",
                    options = RecurrenceInterval.entries,
                    selected = recurrenceInterval,
                    onSelected = { recurrenceInterval = it },
                    display = { it.label() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                EnumDropdown(
                    label = "Remind me",
                    options = NotificationTiming.entries,
                    selected = notificationTiming,
                    onSelected = { notificationTiming = it },
                    display = { it.label() }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && amount != null && amount > 0) {
                        onConfirm(title.trim(), amount, dueDateTimestamp, recurrenceInterval, notificationTiming)
                    }
                },
                enabled = title.isNotBlank() && amount != null && amount > 0
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun <T> EnumDropdown(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    display: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            Surface(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = display(selected))
                    Text(text = "▾")
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(display(option)) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun RecurrenceInterval.label(): String = when (this) {
    RecurrenceInterval.NONE -> "One-time"
    RecurrenceInterval.WEEKLY -> "Weekly"
    RecurrenceInterval.MONTHLY -> "Monthly"
    RecurrenceInterval.YEARLY -> "Yearly"
}

private fun NotificationTiming.label(): String = when (this) {
    NotificationTiming.NONE -> "No reminder"
    NotificationTiming.ONE_HOUR_BEFORE -> "1 hour before"
    NotificationTiming.DAY_BEFORE -> "1 day before"
}
