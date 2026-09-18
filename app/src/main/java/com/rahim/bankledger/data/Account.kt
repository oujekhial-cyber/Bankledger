package com.rahim.bankledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bankName: String,
    val last4: String,
    val nickname: String,
    val openingBalanceRial: Long
)
