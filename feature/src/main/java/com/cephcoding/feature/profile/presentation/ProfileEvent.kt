package com.cephcoding.feature.profile.presentation

sealed interface ProfileEvent {
    data class Message(val text: String) : ProfileEvent
}
