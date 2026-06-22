package com.cephcoding.swipeledger

import android.app.Application
import com.cephcoding.feature.parser.di.parserModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SwipeLedgerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SwipeLedgerApplication)
            modules(
                parserModule
            )
        }
    }
}