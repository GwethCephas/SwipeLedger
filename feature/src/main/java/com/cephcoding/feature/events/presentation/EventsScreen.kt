package com.cephcoding.feature.events.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.cephcoding.core.ui.theme.TextMuted
import org.koin.androidx.compose.koinViewModel

data class RecurringEvent(
    val id: String,
    val title: String,
    val amount: String,
    val status: String,
    val isUpcoming: Boolean,
    val iconRes: Int,
    val label: String
)

@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    viewModel: EventsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        when (val state = uiState) {
            EventsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkTealPrimary)
                }
            }

            is EventsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        EventHeader()
                    }

                    item {
                        SectionTitle("UPCOMING EVENTS")
                    }

                    items(state.upcomingEvents) { event ->
                        EventCard(event = event)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionTitle("PAST AUTOMATED EVENTS")
                    }

                    items(state.pastEvents) { event ->
                        EventCard(event = event)
                    }
                }
            }
        }

        AddEventButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, dueDateTimestamp, recurrenceInterval, notificationTiming ->
                viewModel.addEvent(title, amount, dueDateTimestamp, recurrenceInterval, notificationTiming)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EventHeader() {
    Column {
        Text(
            text = "Recurring Ledger Events",
            style = MaterialTheme.typography.headlineMedium,
            color = TextHighEmphasis,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Scheduled & auto-logged entries",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun EventCard(event: RecurringEvent) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SlateGray.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SteelBlue.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DarkCharcoal),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = event.iconRes),
                    contentDescription = null,
                    tint = BrightCyanAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    color = TextHighEmphasis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.isUpcoming) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock), // Placeholder for small status icon
                            contentDescription = null,
                            tint = DarkTealPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = event.status,
                        color = if (event.isUpcoming) DarkTealPrimary else TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Ksh ${event.amount}",
                    color = CoralDestructive,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (event.label == "Auto-added") {
                    Surface(
                        color = DarkTealPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = event.label,
                            color = BrightCyanAccent,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = event.label,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddEventButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkTealPrimary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                tint = ObsidianBg
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Recurring Event",
                color = ObsidianBg,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
