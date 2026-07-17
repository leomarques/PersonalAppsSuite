package com.personalapps.suite.shared.databaseutils

interface TransactionProvider {
    suspend fun <T> runInTransaction(block: suspend () -> T): T
}
