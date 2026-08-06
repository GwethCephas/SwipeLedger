package com.cephcoding.core.di

import androidx.room.Room
import com.cephcoding.core.data.database.Migrations
import com.cephcoding.core.data.database.TransactionDatabase
import com.cephcoding.core.data.repository.CurrencyPreferenceRepositoryImpl
import com.cephcoding.core.data.repository.LocalDataRepositoryImpl
import com.cephcoding.core.data.repository.TransactionRepositoryImpl
import com.cephcoding.core.domain.repository.CurrencyPreferenceRepository
import com.cephcoding.core.domain.repository.LocalDataRepository
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
            .addMigrations(Migrations.MIGRATION_1_2)
            .build()
    }

    single { get<TransactionDatabase>().transactionsDao() }

    single<TransactionRepository> { TransactionRepositoryImpl(transactionsDao = get()) }

    single<CurrencyPreferenceRepository> { CurrencyPreferenceRepositoryImpl(androidContext()) }

    single<LocalDataRepository> { LocalDataRepositoryImpl(androidContext(), get(), get()) }

}