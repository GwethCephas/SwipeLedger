package com.cephcoding.feature.events.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cephcoding.core.R
import com.cephcoding.core.domain.model.LedgerEvent
import com.cephcoding.core.domain.model.NotificationTiming
import com.cephcoding.core.domain.model.RecurrenceInterval
import com.cephcoding.core.domain.repository.EventRepository
import com.cephcoding.feature.events.alarm.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class EventsViewModel(
    private val repository: EventRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val uiState: StateFlow<EventsUiState> = combine(
        repository.getUpcomingEvents(),
        repository.getPastEvents()
    ) { upcoming, past ->
        EventsUiState.Success(
            upcomingEvents = upcoming.map { it.toDisplayModel(isUpcoming = true) },
            pastEvents = past.map { it.toDisplayModel(isUpcoming = false) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventsUiState.Loading
    )

    fun addEvent(
        title: String,
        amount: Double,
        dueDateTimestamp: Long,
        recurrenceInterval: RecurrenceInterval,
        notificationTiming: NotificationTiming
    ) {
        viewModelScope.launch {
            val event = LedgerEvent(
                id = UUID.randomUUID().toString(),
                title = title,
                amount = amount,
                currency = "Ksh",
                nextDueDateTimestamp = dueDateTimestamp,
                recurrenceInterval = recurrenceInterval,
                isAutoAdded = false,
                notificationTiming = notificationTiming
            )
            repository.insertEvent(event)
            alarmScheduler.schedule(event)
        }
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            alarmScheduler.cancel(id)
            repository.deleteEvent(id)
        }
    }
}

private val amountFormat = NumberFormat.getNumberInstance(Locale.US)
private val pastDateFormat = SimpleDateFormat("MMM d", Locale.US)

private fun LedgerEvent.toDisplayModel(isUpcoming: Boolean): RecurringEvent {
    return RecurringEvent(
        id = id,
        title = title,
        amount = amountFormat.format(amount),
        status = formatStatus(nextDueDateTimestamp, isUpcoming),
        isUpcoming = isUpcoming,
        iconRes = R.drawable.ic_events,
        label = formatLabel(recurrenceInterval, isUpcoming)
    )
}

private fun formatStatus(nextDueDateTimestamp: Long, isUpcoming: Boolean): String {
    if (!isUpcoming) return pastDateFormat.format(Date(nextDueDateTimestamp))

    val diffDays = ((nextDueDateTimestamp - System.currentTimeMillis()) / (24 * 60 * 60 * 1000L)).toInt()
    return when {
        diffDays <= 0 -> "Due today"
        diffDays == 1 -> "Due tomorrow"
        else -> "Due in $diffDays days"
    }
}

private fun formatLabel(recurrenceInterval: RecurrenceInterval, isUpcoming: Boolean): String {
    if (!isUpcoming) return "Auto-added"
    return if (recurrenceInterval == RecurrenceInterval.NONE) "one-time" else "recurring"
}
