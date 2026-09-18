package com.rahim.bankledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahim.bankledger.data.Account
import com.rahim.bankledger.data.TransactionEntity
import com.rahim.bankledger.ui.theme.Emerald500
import com.rahim.bankledger.ui.theme.HeroGradient
import com.rahim.bankledger.ui.theme.Rose500
import com.rahim.bankledger.ui.theme.rememberLedgerColors
import com.rahim.bankledger.util.formatAmount

private val SAMPLE_EXPENSE = "برداشت مبلغ 450,000 ریال از کارت ۱۲۳۴ در تاریخ 1405/06/25 ساعت 14:32 موجودی 8,320,000 ریال بانک ملت"
private val SAMPLE_INCOME = "واریز مبلغ 12,000,000 ریال به حساب ۵۶۷۸ در تاریخ 1405/06/25 ساعت 09:15 موجودی 20,320,000 ریال بانک سامان"

@Composable
fun DashboardScreen(
    accounts: List<Account>,
    transactions: List<TransactionEntity>,
    balanceFor: (Long) -> Long,
    selectedAccountId: Long?,
    onSelectAccount: (Long?) -> Unit,
    onAddAccountClick: () -> Unit,
    onDeleteTx: (TransactionEntity) -> Unit,
    onTestSms: (String) -> Unit
) {
    val colors = rememberLedgerColors()
    var unit by remember { mutableStateOf("toman") }
    var smsText by remember { mutableStateOf("") }

    val totalBalance = accounts.sumOf { balanceFor(it.id) }
    val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amountRial }
    val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amountRial }
    val visibleTx = if (selectedAccountId != null) transactions.filter { it.accountId == selectedAccountId } else transactions

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 16.dp, 16.dp, 80.dp)
    ) {
        item {
            // Hero balance card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(HeroGradient)
                    .padding(20.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("موجودی کل", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { unit = if (unit == "toman") "rial" else "toman" }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(if (unit == "toman") "تومان" else "ریال", color = Color.White, fontSize = 11.sp)
                        }
                    }
                    Text(
                        formatAmount(totalBalance, unit),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        Text("📈 ${formatAmount(totalIncome, unit)}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("📉 ${formatAmount(totalExpense, unit)}", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    ChipButton(
                        label = "همه حساب‌ها",
                        selected = selectedAccountId == null,
                        gradient = HeroGradient,
                        colors = colors,
                        onClick = { onSelectAccount(null) }
                    )
                }
                items(accounts) { a ->
                    val idx = accounts.indexOf(a)
                    ChipButton(
                        label = "${a.nickname} · ${formatAmount(balanceFor(a.id), unit)}",
                        selected = selectedAccountId == a.id,
                        gradient = ACCOUNT_GRADIENTS[idx % ACCOUNT_GRADIENTS.size],
                        colors = colors,
                        onClick = { onSelectAccount(a.id) }
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(colors.surface2)
                            .clickable { onAddAccountClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = colors.textSoft, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SMS test / paste box — useful for trying the parser without waiting for a real message
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.surface)
                    .padding(14.dp)
            ) {
                Text("💬  تست دستی پیامک بانکی", color = colors.textSoft, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = smsText,
                    onValueChange = { smsText = it },
                    placeholder = { Text("مثلاً: برداشت مبلغ ۴۵۰,۰۰۰ ریال از کارت …") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surface2,
                        unfocusedContainerColor = colors.surface2
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            "نمونه برداشت",
                            color = colors.textSoft,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable { smsText = SAMPLE_EXPENSE }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "نمونه واریز",
                            color = colors.textSoft,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable { smsText = SAMPLE_INCOME }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(HeroGradient)
                            .clickable(enabled = smsText.isNotBlank()) {
                                onTestSms(smsText)
                                smsText = ""
                            }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text("بررسی پیامک", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (visibleTx.isNotEmpty()) "${visibleTx.size} تراکنش" else "دفتر",
                color = colors.textSoft,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (visibleTx.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.surface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "هنوز تراکنشی ثبت نشده 🌱\nپیامک بانکی را بالا بچسبانید یا از نمونه‌ها استفاده کنید.",
                        color = colors.textSoft,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        items(visibleTx, key = { it.id }) { t ->
            val acc = accounts.firstOrNull { it.id == t.accountId }
            val meta = findCategoryMeta(t.category)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.surface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(meta.gradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(meta.emoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(t.note.ifBlank { "بدون توضیح" }, color = colors.text, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    Text(
                        "${t.category.ifBlank { "بدون دسته" }}${acc?.let { " · ${it.nickname}" } ?: ""}",
                        color = colors.textSoft,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${if (t.type == "income") "+" else "−"}${formatAmount(t.amountRial, unit)}",
                        color = if (t.type == "income") Emerald500 else Rose500,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Text(
                        "🗑",
                        fontSize = 12.sp,
                        color = colors.textSoft,
                        modifier = Modifier.clickable { onDeleteTx(t) }.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipButton(
    label: String,
    selected: Boolean,
    gradient: androidx.compose.ui.graphics.Brush,
    colors: com.rahim.bankledger.ui.theme.LedgerColors,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .background(if (selected) gradient else androidx.compose.ui.graphics.SolidColor(colors.surface2))
            .clickable { onClick() }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) Color.White else colors.textSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
