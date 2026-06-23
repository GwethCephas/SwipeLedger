package com.cephcoding.swipeledger

import android.app.Application
import com.cephcoding.core.di.coreModule
import com.cephcoding.feature.sms.di.smsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SwipeLedgerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SwipeLedgerApplication)
            modules(
                smsModule,
                coreModule
            )
        }
    }
}