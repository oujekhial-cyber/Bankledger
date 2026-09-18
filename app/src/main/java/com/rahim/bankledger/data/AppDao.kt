package com.rahim.bankledger.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert
    suspend fun insertAccount(account: Account): Long

    @Delete
    suspend fun deleteAccount(account: Account)

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    suspend fun getAccountsOnce(): List<Account>

    @Insert
    suspend fun insertTransaction(tx: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(tx: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(tx: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categorized = 0 ORDER BY timestamp ASC")
    fun getPendingTransactions(): Flow<List<TransactionEntity>>
}
