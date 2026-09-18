package com.rahim.bankledger.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rahim.bankledger.data.Account
import com.rahim.bankledger.data.AppDatabase
import com.rahim.bankledger.data.TransactionEntity
import com.rahim.bankledger.sms.BankSmsParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).appDao()

    val accounts = dao.getAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions = dao.getTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTransactions = dao.getPendingTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAccount(bank: String, last4: String, nickname: String, openingRial: Long) {
        viewModelScope.launch {
            dao.insertAccount(
                Account(
                    bankName = bank,
                    last4 = last4,
                    nickname = nickname.ifBlank { bank },
                    openingBalanceRial = openingRial
                )
            )
        }
    }

    fun removeAccount(account: Account) {
        viewModelScope.launch {
            dao.deleteAccount(account)
        }
    }

    fun categorize(tx: TransactionEntity, note: String, category: String, accountId: Long, amountRial: Long, type: String) {
        viewModelScope.launch {
            dao.updateTransaction(
                tx.copy(
                    note = note,
                    category = category,
                    accountId = accountId,
                    amountRial = amountRial,
                    type = type,
                    categorized = true
                )
            )
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            dao.deleteTransaction(tx)
        }
    }

    /** Lets the person test the parser in-app by pasting a sample SMS, the same way the receiver would handle a real one. */
    fun testParseSms(text: String) {
        val parsed = BankSmsParser.parse(text) ?: return
        viewModelScope.launch {
            val accs = dao.getAccountsOnce()
            val matched = accs.firstOrNull { it.last4.isNotBlank() && it.last4 == parsed.cardHint }
                ?: accs.firstOrNull { it.bankName == parsed.bankHint }
                ?: accs.firstOrNull()
                ?: return@launch

            dao.insertTransaction(
                TransactionEntity(
                    accountId = matched.id,
                    type = parsed.type,
                    amountRial = parsed.amountRial,
                    note = "",
                    category = "",
                    rawSms = text,
                    timestamp = System.currentTimeMillis(),
                    categorized = false
                )
            )
        }
    }

    fun balanceFor(accountId: Long, accounts: List<Account>, transactions: List<TransactionEntity>): Long {
        val acc = accounts.firstOrNull { it.id == accountId } ?: return 0
        val delta = transactions.filter { it.accountId == accountId }
            .sumOf { if (it.type == "income") it.amountRial else -it.amountRial }
        return acc.openingBalanceRial + delta
    }
}
