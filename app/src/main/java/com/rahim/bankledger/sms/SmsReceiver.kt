package com.rahim.bankledger.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.rahim.bankledger.data.AppDatabase
import com.rahim.bankledger.data.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return
        val fullBody = messages.joinToString(separator = "") { it.messageBody ?: "" }

        val parsed = BankSmsParser.parse(fullBody) ?: return

        // Do the DB work off the main thread, using goAsync() so the receiver
        // isn't torn down before the coroutine finishes.
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context.applicationContext).appDao()
                val accounts = dao.getAccountsOnce()
                if (accounts.isEmpty()) return@launch

                val matched = accounts.firstOrNull { it.last4.isNotBlank() && it.last4 == parsed.cardHint }
                    ?: accounts.firstOrNull { it.bankName == parsed.bankHint }
                    ?: accounts.first()

                val txId = dao.insertTransaction(
                    TransactionEntity(
                        accountId = matched.id,
                        type = parsed.type,
                        amountRial = parsed.amountRial,
                        note = "",
                        category = "",
                        rawSms = fullBody,
                        timestamp = System.currentTimeMillis(),
                        categorized = false
                    )
                )

                NotificationHelper.showTransactionNotification(context.applicationContext, parsed, txId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
