package com.cephcoding.feature.profile.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cephcoding.core.R
import com.cephcoding.core.ui.theme.BrightCyanAccent
import com.cephcoding.core.ui.theme.CoralDestructive
import com.cephcoding.core.ui.theme.DarkCharcoal
import com.cephcoding.core.ui.theme.DarkTealPrimary
import com.cephcoding.core.ui.theme.ObsidianBg
import com.cephcoding.core.ui.theme.SlateGray
import com.cephcoding.core.ui.theme.SteelBlue
import com.cephcoding.core.ui.theme.TextHighEmphasis
import com.cephcoding.core.ui.theme.TextMediumEmphasis
import com.cephcoding.core.ui.theme.TextMuted
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showClearDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    
    var smsParsingEnabled by remember { mutableStateOf(false) }
    var isRestrictionCardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.Message -> Toast.makeText(context, event.text, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri -> uri?.let { viewModel.onBackupRequested(it) } }

    val csvExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> uri?.let { viewModel.onCsvExportRequested(it) } }

    val timestamp = remember { SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        when (val state = uiState) {
            ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrightCyanAccent)
                }
            }

            is ProfileUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 40.dp,
                        bottom = 40.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    item {
                        ProfileHeader()
                    }

                    item {
                        SectionLabel("LOCAL DATABASE CONTROL")
                        DataManagementCard(
                            isBusy = state.isBusy,
                            onBackupClick = { backupLauncher.launch("SwipeLedger_Backup_$timestamp.db") },
                            onExportCsvClick = { csvExportLauncher.launch("SwipeLedger_Transactions_$timestamp.csv") },
                            onClearDataClick = { showClearDialog = true }
                        )
                    }

                    item {
                        SectionLabel("APP SETTINGS")
                        AppSettingsCard(
                            selectedCurrencyCode = state.selectedCurrency.code,
                            onCurrencyClick = { showCurrencyDialog = true },
                            smsParsingEnabled = smsParsingEnabled,
                            onSmsParsingToggle = { enabled ->
                                smsParsingEnabled = enabled
                                if (enabled) isRestrictionCardVisible = true
                            }
                        )
                    }

                    if (smsParsingEnabled && isRestrictionCardVisible) {
                        item {
                            BackgroundRestrictionCard(onDismiss = { isRestrictionCardVisible = false })
                        }
                    }
                }
            }
        }
    }

    val successState = uiState as? ProfileUiState.Success
    if (showCurrencyDialog && successState != null) {
        CurrencySelectorDialog(
            selectedCurrency = successState.selectedCurrency,
            onCurrencySelected = { viewModel.onCurrencySelected(it) },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    if (showClearDialog) {
        ClearAllDataDialog(
            onConfirm = {
                viewModel.onClearAllDataConfirmed()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkCharcoal)
                .border(1.dp, SteelBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null,
                tint = BrightCyanAccent,
                modifier = Modifier.size(40.dp)
            )

            // Lock badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(ObsidianBg)
                    .border(1.dp, SteelBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    tint = DarkTealPrimary,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "My Ledger",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextHighEmphasis
        )

        Text(
            text = "Local User - No Cloud Account",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Encryption Badge
        Surface(
            color = DarkTealPrimary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                DarkTealPrimary.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BrightCyanAccent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Local Database Encrypted",
                    color = TextMediumEmphasis,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted,
        modifier = Modifier.padding(bottom = 8.dp),
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DataManagementCard(
    isBusy: Boolean,
    onBackupClick: () -> Unit,
    onExportCsvClick: () -> Unit,
    onClearDataClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SlateGray.copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SteelBlue.copy(alpha = 0.3f))
    ) {
        Column {
            ProfileListItem(
                icon = R.drawable.ic_backup,
                title = "Backup Database to Device",
                subtitle = "Save encrypted local snapshot",
                onClick = onBackupClick,
                enabled = !isBusy
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = SteelBlue.copy(alpha = 0.2f)
            )
            ProfileListItem(
                icon = R.drawable.ic_export,
                title = "Export to CSV",
                subtitle = "All transactions - current period",
                onClick = onExportCsvClick,
                enabled = !isBusy
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = SteelBlue.copy(alpha = 0.2f)
            )
            ProfileListItem(
                icon = R.drawable.ic_delete,
                title = "Clear All Local Data",
                subtitle = "Danger Zone - Irreversible action",
                onClick = onClearDataClick,
                enabled = !isBusy,
                titleColor = CoralDestructive,
                iconColor = CoralDestructive
            )
        }
    }
}

@Composable
fun AppSettingsCard(
    selectedCurrencyCode: String,
    onCurrencyClick: () -> Unit,
    smsParsingEnabled: Boolean,
    onSmsParsingToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SlateGray.copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SteelBlue.copy(alpha = 0.3f))
    ) {
        Column {
            ProfileListItem(
                icon = R.drawable.ic_currency,
                title = "Default Currency",
                subtitle = "Kenyan Shilling",
                trailing = {
                    Text(
                        text = selectedCurrencyCode,
                        color = DarkTealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                onClick = onCurrencyClick
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = SteelBlue.copy(alpha = 0.2f)
            )
            ProfileListItem(
                icon = R.drawable.ic_notification,
                title = "SMS Parsing",
                subtitle = "Auto-detect M-Pesa messages",
                trailing = {
                    Switch(
                        checked = smsParsingEnabled,
                        onCheckedChange = onSmsParsingToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ObsidianBg,
                            checkedTrackColor = DarkTealPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkCharcoal
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun ProfileListItem(
    icon: Int,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    titleColor: Color = TextHighEmphasis,
    iconColor: Color = DarkTealPrimary,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DarkCharcoal),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                painter = painterResource(R.drawable.ic_right_arrow),
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
