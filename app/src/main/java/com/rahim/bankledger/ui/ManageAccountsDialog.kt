package com.rahim.bankledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahim.bankledger.data.Account
import com.rahim.bankledger.ui.theme.Rose500
import com.rahim.bankledger.ui.theme.rememberLedgerColors

@Composable
fun ManageAccountsDialog(
    accounts: List<Account>,
    onAdd: (String, String, String, Long) -> Unit,
    onRemove: (Account) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = rememberLedgerColors()
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = { Text("حساب‌ها و کارت‌ها", color = colors.text, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                accounts.forEach { a ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surface2)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${a.nickname} · ${a.bankName}", color = colors.text, fontSize = 13.sp)
                        Text(
                            "🗑",
                            fontSize = 14.sp,
                            color = Rose500,
                            modifier = Modifier.clickable { onRemove(a) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (accounts.isNotEmpty()) Spacer(modifier = Modifier.height(8.dp))
                AccountForm(onSubmit = onAdd)
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
