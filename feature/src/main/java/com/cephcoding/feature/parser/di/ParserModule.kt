package com.cephcoding.feature.parser.di

import com.cephcoding.feature.parser.RegexParser
import com.cephcoding.feature.parser.TransactionProcessor
import org.koin.dsl.module

val parserModule = module {
    single { RegexParser() }
    single { TransactionProcessor(get()) }
}