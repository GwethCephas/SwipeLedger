package com.cephcoding.feature.profile.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cephcoding.core.R
import com.cephcoding.core.common.util.AutoStartPermissionManager
import com.cephcoding.core.ui.theme.CoralDestructive
import com.cephcoding.core.ui.theme.ObsidianBg
import com.cephcoding.core.ui.theme.TextHighEmphasis
import com.cephcoding.core.ui.theme.TextMediumEmphasis
import com.cephcoding.core.ui.theme.TextMuted

@Composable
fun BackgroundRestrictionCard(
    context: Context = LocalContext.current,
    onDismiss: () -> Unit
) {
    if (AutoStartPermissionManager.isRestrictedDevice()) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CoralDestructive.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Auto-Start Tracking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextHighEmphasis
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock), // User will replace with close icon
                            contentDescription = "Dismiss",
                            tint = TextMuted
                        )
                    }
                }
                Text(
                    text = "Your device limits offline background features. To automatically track incoming SMS transactions, please enable Auto-Start for SwipeLedger.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMediumEmphasis
                )
                Button(
                    onClick = {
                        val success = AutoStartPermissionManager.openAutostartSettings(context)
                        if (!success) {
                            // Fallback to general settings if custom intent fails
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CoralDestructive
                    ),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Go to Settings", color = ObsidianBg)
                }
            }
        }
    }
}
