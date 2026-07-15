package com.cephcoding.feature.sms.di

import com.cephcoding.feature.sms.parser.RegexParser
import com.cephcoding.feature.sms.TransactionProcessor
import com.cephcoding.feature.sms.ml.LocalClassifier
import com.cephcoding.feature.sms.worker.SmsParseWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val smsModule = module {

    single { RegexParser() }

    single { LocalClassifier() }

    single { TransactionProcessor(get(), get(), get()) }

    worker { SmsParseWorker(get(), get(), get()) }
}