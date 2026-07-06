package com.cephcoding.swipeledger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.cephcoding.feature.dashboard.presentation.DashboardScreen
import com.cephcoding.feature.dashboard.presentation.DashboardViewModel
import com.cephcoding.core.ui.theme.SwipeLedgerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "SMS parsing active!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission required to automate ledger logs.", Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkSmsPermission()
        setContent {
            SwipeLedgerTheme {
                val viewModel = koinViewModel<DashboardViewModel>()
                DashboardScreen(viewModel)
            }
        }
    }
    private fun checkSmsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Already authorized! Background pipeline is good to go.
            }
            else -> {
                // Trigger the system request dialog prompt
                requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
            }
        }
    }
}
