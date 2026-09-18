package com.rahim.bankledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rahim.bankledger.ui.theme.HeroGradient
import com.rahim.bankledger.ui.theme.rememberLedgerColors

@Composable
fun AccountForm(onSubmit: (bank: String, last4: String, nickname: String, openingRial: Long) -> Unit) {
    val colors = rememberLedgerColors()
    var bank by remember { mutableStateOf(BANKS.first()) }
    var expanded by remember { mutableStateOf(false) }
    var last4 by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var opening by remember { mutableStateOf("") }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = colors.surface2,
        unfocusedContainerColor = colors.surface2,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("بانک", color = colors.textSoft)
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface2)
                    .clickable { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(bank, color = colors.text)
                Text("˅", color = colors.textSoft)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                BANKS.forEach { b ->
                    DropdownMenuItem(text = { Text(b) }, onClick = { bank = b; expanded = false })
                }
            }
        }

        Text("۴ رقم آخر کارت (اختیاری)", color = colors.textSoft)
        OutlinedTextField(
            value = last4,
            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) last4 = it },
            placeholder = { Text("1234") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = fieldColors,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Text("اسم دلخواه", color = colors.textSoft)
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            placeholder = { Text("مثلاً کارت خرید") },
            colors = fieldColors,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Text("موجودی فعلی به تومان (اختیاری)", color = colors.textSoft)
        OutlinedTextField(
            value = opening,
            onValueChange = { if (it.all { c -> c.isDigit() }) opening = it },
            placeholder = { Text("0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = fieldColors,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(HeroGradient)
                .clickable {
                    val openingRial = (opening.toLongOrNull() ?: 0L) * 10
                    onSubmit(bank, last4, nickname.ifBlank { bank }, openingRial)
                },
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("افزودن حساب", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}
