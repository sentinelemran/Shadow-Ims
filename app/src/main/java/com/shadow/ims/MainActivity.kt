package com.shadow.ims

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.shadow.ims.ui.MainScreen
import com.shadow.ims.ui.MainViewModel
import com.shadow.ims.ui.ShadowTheme

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    private val notifPerm =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* ignore */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notifPerm.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            ShadowTheme {
                MainScreen(
                    vm = vm,
                    onOpenDevSettings = {
                        runCatching {
                            startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                        }
                    },
                    onPairingStart = {
                        vm.startPairingService()
                        runCatching {
                            startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                        }
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refresh()
    }
}
