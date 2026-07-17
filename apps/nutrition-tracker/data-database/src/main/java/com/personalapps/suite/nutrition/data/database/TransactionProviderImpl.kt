package com.personalapps.suite.nutrition.data.database

import androidx.room.withTransaction
import com.personalapps.suite.shared.databaseutils.TransactionProvider

class TransactionProviderImpl(
    private val database: NutritionDatabase
) : TransactionProvider {
    override suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return database.withTransaction {
            block()
        }
    }
}
