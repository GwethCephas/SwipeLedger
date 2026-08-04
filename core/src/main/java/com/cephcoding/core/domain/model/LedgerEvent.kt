package com.cephcoding.core.domain.model

data class LedgerEvent(
    val id: String,
    val title: String,
    val amount: Double,
    val currency: String,
    val nextDueDateTimestamp: Long,
    val recurrenceInterval: RecurrenceInterval,
    val isAutoAdded: Boolean,
    val notificationTiming: NotificationTiming
)

enum class RecurrenceInterval {
    NONE, WEEKLY, MONTHLY, YEARLY
}

enum class NotificationTiming(val offsetMillis: Long) {
    NONE(0L),
    ONE_HOUR_BEFORE(-3_600_000L),
    DAY_BEFORE(-86_400_000L)
}
