package com.cephcoding.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cephcoding.core.ui.theme.CoralDestructive
import com.cephcoding.core.ui.theme.DarkCharcoal
import com.cephcoding.core.ui.theme.DarkTealPrimary
import com.cephcoding.core.ui.theme.ObsidianBg
import com.cephcoding.core.ui.theme.TextHighEmphasis
import com.cephcoding.core.ui.theme.TextMediumEmphasis

@Composable
fun DataManagementSection(
    isBusy: Boolean,
    onBackupClick: () -> Unit,
    onExportCsvClick: () -> Unit,
    onClearDataClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Local Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextHighEmphasis
            )
            Text(
                text = "Everything stays on this device. Back up or export your transaction ledger, or clear it entirely.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMediumEmphasis
            )

            Button(
                onClick = onBackupClick,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkTealPrimary)
            ) {
                Text("Backup Database", color = ObsidianBg)
            }

            OutlinedButton(
                onClick = onExportCsvClick,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTealPrimary)
            ) {
                Text("Export to CSV")
            }

            TextButton(
                onClick = onClearDataClick,
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All Data", color = CoralDestructive)
            }
        }
    }
}
