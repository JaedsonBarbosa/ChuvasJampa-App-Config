package com.example.estacao

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.harrysoft.androidbluetoothserial.BluetoothManager
import java.util.jar.Manifest


val bluetoothManager: BluetoothManager = BluetoothManager.getInstance()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)

        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast
                .makeText(this, "O bluetooth não está ativado.", Toast.LENGTH_LONG)
                .show() // Replace context with your context instance.
            finish()
        }
    }
}