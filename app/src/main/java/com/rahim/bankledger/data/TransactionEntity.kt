package com.rahim.bankledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val type: String, // "income" or "expense"
    val amountRial: Long,
    val note: String,
    val category: String,
    val rawSms: String?,
    val timestamp: Long,
    val categorized: Boolean = false
)
