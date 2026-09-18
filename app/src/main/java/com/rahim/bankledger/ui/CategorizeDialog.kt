package com.rahim.bankledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahim.bankledger.data.Account
import com.rahim.bankledger.data.TransactionEntity
import com.rahim.bankledger.ui.theme.Emerald500
import com.rahim.bankledger.ui.theme.HeroGradient
import com.rahim.bankledger.ui.theme.Rose500
import com.rahim.bankledger.ui.theme.rememberLedgerColors

@Composable
fun CategorizeDialog(
    tx: TransactionEntity,
    accounts: List<Account>,
    onSave: (note: String, category: String, accountId: Long, amountRial: Long, type: String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = rememberLedgerColors()
    var type by remember(tx.id) { mutableStateOf(tx.type) }
    var amountText by remember(tx.id) { mutableStateOf(if (tx.amountRial > 0) tx.amountRial.toString() else "") }
    var accountId by remember(tx.id) { mutableStateOf(if (tx.accountId != 0L) tx.accountId else accounts.firstOrNull()?.id ?: 0L) }
    var note by remember(tx.id) { mutableStateOf(tx.note) }
    var category by remember(tx.id) { mutableStateOf(tx.category) }
    var noteError by remember(tx.id) { mutableStateOf(false) }
    var amountError by remember(tx.id) { mutableStateOf(false) }
    var accountMenuExpanded by remember(tx.id) { mutableStateOf(false) }

    val cats = if (type == "income") INCOME_CATEGORIES else EXPENSE_CATEGORIES

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = { Text("این تراکنش برای چی بود؟", color = colors.text, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                val rawSmsText = tx.rawSms
                if (!rawSmsText.isNullOrBlank()) {
                    Text(
                        rawSmsText,
                        color = colors.textSoft,
                        fontSize = 11.sp,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surface2)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("expense" to "برداشت از حساب", "income" to "واریز به حساب").forEach { (value, label) ->
                        val selected = type == value
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) (if (value == "income") Emerald500 else Rose500) else colors.surface2)
                                .clickable { type = value; category = "" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (selected) Color.White else colors.textSoft, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("مبلغ (ریال)", color = colors.textSoft, fontSize = 12.sp)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) { amountText = it; amountError = false } },
                    isError = amountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface2,
                        unfocusedContainerColor = colors.surface2
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (amountError) Text("مبلغ را وارد کن", color = Rose500, fontSize = 12.sp)

                if (accounts.size > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("حساب", color = colors.textSoft, fontSize = 12.sp)
                    val accName = accounts.firstOrNull { it.id == accountId }?.nickname ?: ""
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surface2)
                                .clickable { accountMenuExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(accName, color = colors.text)
                            Text("˅", color = colors.textSoft)
                        }
                        DropdownMenu(expanded = accountMenuExpanded, onDismissRequest = { accountMenuExpanded = false }) {
                            accounts.forEach { a ->
                                DropdownMenuItem(
                                    text = { Text(a.nickname) },
                                    onClick = { accountId = a.id; accountMenuExpanded = false }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(if (type == "income") "چی بود که واریز شد؟" else "چی خریدی؟", color = colors.textSoft, fontSize = 12.sp)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it; noteError = false },
                    placeholder = { Text(if (type == "income") "مثلاً پیش‌پرداخت مشتری" else "مثلاً بنزین") },
                    isError = noteError,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface2,
                        unfocusedContainerColor = colors.surface2
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (noteError) Text("بنویس چی بود", color = Rose500, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cats) { c ->
                        val selected = category == c.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) c.gradient else SolidColor(colors.surface2))
                                .clickable { category = c.name }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("${c.emoji} ${c.name}", color = if (selected) Color.White else colors.textSoft, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(HeroGradient)
                        .clickable {
                            val amt = amountText.toLongOrNull() ?: 0L
                            when {
                                note.isBlank() -> noteError = true
                                amt <= 0 -> amountError = true
                                else -> onSave(note, category, accountId, amt, type)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("ثبت در دفتر", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
