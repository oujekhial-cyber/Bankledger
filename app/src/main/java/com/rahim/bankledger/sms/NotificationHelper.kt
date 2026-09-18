package com.rahim.bankledger.sms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rahim.bankledger.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID = "bank_tx_channel"

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تراکنش‌های بانکی",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "وقتی پیامک واریز یا برداشت شناسایی می‌شود اعلان می‌دهد"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun showTransactionNotification(context: Context, parsed: BankSmsParser.ParsedSms, txId: Long) {
        ensureChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            txId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (parsed.type == "income") "واریز شناسایی شد" else "برداشت شناسایی شد"
        val amountToman = parsed.amountRial / 10
        val body = "برای ثبت اینکه بابت چی بود ضربه بزن — ${"%,d".format(amountToman)} تومان"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(txId.toInt(), notification)
        } catch (e: SecurityException) {
            // Notification permission not granted yet; the transaction is still saved
            // as pending and will show up next time the app is opened.
        }
    }
}
