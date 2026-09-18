package com.rahim.bankledger

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.rahim.bankledger.ui.DashboardScreen
import com.rahim.bankledger.ui.LedgerViewModel
import com.rahim.bankledger.ui.ManageAccountsDialog
import com.rahim.bankledger.ui.OnboardingScreen
import com.rahim.bankledger.ui.CategorizeDialog
import com.rahim.bankledger.ui.theme.BankLedgerTheme
import com.rahim.bankledger.ui.theme.rememberLedgerColors

class MainActivity : ComponentActivity() {

    private val viewModel: LedgerViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Compose reads permission state fresh each time it needs it; nothing to store here. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNeededPermissions()

        setContent {
            // Force RTL regardless of the device's system language, to match the intended design.
            CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Rtl) {
                BankLedgerTheme {
                    val colors = rememberLedgerColors()
                    Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
                        AppRoot(viewModel)
                    }
                }
            }
        }
    }

    private fun requestNeededPermissions() {
        val perms = mutableListOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(perms.toTypedArray())
    }
}

@Composable
private fun AppRoot(viewModel: LedgerViewModel) {
    val accounts by viewModel.accounts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val pending by viewModel.pendingTransactions.collectAsState()

    var showManageAccounts by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf<Long?>(null) }
    var dismissedIds by remember { mutableStateOf(setOf<Long>()) }

    if (accounts.isEmpty()) {
        OnboardingScreen(onAdd = viewModel::addAccount)
    } else {
        DashboardScreen(
            accounts = accounts,
            transactions = transactions,
            balanceFor = { accountId -> viewModel.balanceFor(accountId, accounts, transactions) },
            selectedAccountId = selectedAccountId,
            onSelectAccount = { selectedAccountId = it },
            onAddAccountClick = { showManageAccounts = true },
            onDeleteTx = viewModel::deleteTransaction,
            onTestSms = viewModel::testParseSms
        )
    }

    if (showManageAccounts) {
        ManageAccountsDialog(
            accounts = accounts,
            onAdd = viewModel::addAccount,
            onRemove = viewModel::removeAccount,
            onDismiss = { showManageAccounts = false }
        )
    }

    val nextPending = pending.firstOrNull { it.id !in dismissedIds }
    if (nextPending != null) {
        CategorizeDialog(
            tx = nextPending,
            accounts = accounts,
            onSave = { note, category, accountId, amount, type ->
                viewModel.categorize(nextPending, note, category, accountId, amount, type)
            },
            onDismiss = { dismissedIds = dismissedIds + nextPending.id }
        )
    }
}
