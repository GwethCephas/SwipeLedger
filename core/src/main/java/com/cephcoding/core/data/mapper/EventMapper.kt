package com.cephcoding.core.data.mapper

import com.cephcoding.core.data.database.events.LedgerEventEntity
import com.cephcoding.core.domain.model.LedgerEvent
import com.cephcoding.core.domain.model.NotificationTiming
import com.cephcoding.core.domain.model.RecurrenceInterval

fun LedgerEvent.toEntity(): LedgerEventEntity {
    return LedgerEventEntity(
        id = this.id,
        title = this.title,
        amount = this.amount,
        currency = this.currency,
        nextDueDateTimestamp = this.nextDueDateTimestamp,
        recurrenceInterval = this.recurrenceInterval.name,
        isAutoAdded = this.isAutoAdded,
        notificationTiming = this.notificationTiming.name
    )
}

fun LedgerEventEntity.toLedgerEvent(): LedgerEvent {
    return LedgerEvent(
        id = this.id,
        title = this.title,
        amount = this.amount,
        currency = this.currency,
        nextDueDateTimestamp = this.nextDueDateTimestamp,
        recurrenceInterval = try {
            RecurrenceInterval.valueOf(this.recurrenceInterval)
        } catch (e: IllegalArgumentException) {
            RecurrenceInterval.NONE
        },
        isAutoAdded = this.isAutoAdded,
        notificationTiming = try {
            NotificationTiming.valueOf(this.notificationTiming)
        } catch (e: IllegalArgumentException) {
            NotificationTiming.NONE
        }
    )
}
