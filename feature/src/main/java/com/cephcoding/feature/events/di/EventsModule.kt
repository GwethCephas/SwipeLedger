package com.cephcoding.feature.events.di

import androidx.room.Room
import com.cephcoding.core.data.database.events.EventsDatabase
import com.cephcoding.core.data.repository.EventRepositoryImpl
import com.cephcoding.core.domain.repository.EventRepository
import com.cephcoding.feature.events.alarm.AlarmScheduler
import com.cephcoding.feature.events.notification.EventNotificationHelper
import com.cephcoding.feature.events.presentation.EventsViewModel
import com.cephcoding.feature.events.worker.EventFireWorker
import com.cephcoding.feature.events.worker.RescheduleAlarmsWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val eventsModule = module {

    single {
        Room.databaseBuilder(androidContext(), EventsDatabase::class.java, "events_db").build()
    }

    single { get<EventsDatabase>().eventDao() }

    single<EventRepository> { EventRepositoryImpl(eventDao = get(), eventsDatabase = get()) }

    single { AlarmScheduler(androidContext()) }

    single { EventNotificationHelper(androidContext()) }

    viewModel { EventsViewModel(get(), get(), get()) }

    worker { EventFireWorker(get(), get(), get(), get(), get()) }

    worker { RescheduleAlarmsWorker(get(), get(), get(), get()) }
}
