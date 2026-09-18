package com.rahim.bankledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahim.bankledger.ui.theme.HeroGradient
import com.rahim.bankledger.ui.theme.rememberLedgerColors

@Composable
fun OnboardingScreen(onAdd: (String, String, String, Long) -> Unit) {
    val colors = rememberLedgerColors()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 72.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(HeroGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💰", fontSize = 28.sp)
                }
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)) {
                    Text(
                        "دفتر حساب شما",
                        color = colors.text,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "اول یک حساب یا کارت بانکی معرفی کن تا تراکنش‌ها زیر آن ثبت شوند.",
                        color = colors.textSoft,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                    )
                }
            }
            AccountForm(onSubmit = onAdd)
        }
    }
}
