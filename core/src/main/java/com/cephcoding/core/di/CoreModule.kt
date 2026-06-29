package com.cephcoding.core.di

import androidx.room.Room
import com.cephcoding.core.data.database.TransactionDatabase
import com.cephcoding.core.data.repository.TransactionRepositoryImpl
import com.cephcoding.core.domain.repository.TransactionRepository
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {

    single {
        val passphrase = "SuperSecretSecurePassphraseKey123".toByteArray()
        val factory = SupportOpenHelperFactory(passphrase)

        Room.databaseBuilder(
            androidContext(),
            TransactionDatabase::class.java,
            "Transaction_db"
        ).openHelperFactory(factory)
            .build()
    }

    single { get<TransactionDatabase>().transactionsDao() }

    single<TransactionRepository> { TransactionRepositoryImpl(transactionsDao = get()) }

}