package com.cephcoding.swipeledger

import android.app.Application
import androidx.work.Configuration
import com.cephcoding.core.di.coreModule
import com.cephcoding.feature.dashboard.di.dashboardModule
import com.cephcoding.feature.sms.di.smsModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.android.ext.android.get

class SwipeLedgerApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("sqlcipher")
        startKoin {
            androidContext(this@SwipeLedgerApplication)
            workManagerFactory()
            modules(
                smsModule,
                dashboardModule,
                coreModule
            )
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(get())
            .build()
}