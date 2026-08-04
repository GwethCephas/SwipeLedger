package com.cephcoding.feature.events.presentation

interface EventsUiState {
    object Loading : EventsUiState

    data class Success(
        val upcomingEvents: List<RecurringEvent>,
        val pastEvents: List<RecurringEvent>
    ) : EventsUiState
}
