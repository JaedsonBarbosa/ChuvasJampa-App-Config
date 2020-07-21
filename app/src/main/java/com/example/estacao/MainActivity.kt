package com.example.estacao

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.harrysoft.androidbluetoothserial.BluetoothManager

val bluetoothManager: BluetoothManager = BluetoothManager.getInstance()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)

        if (bluetoothManager.pairedDevicesList.size == 0) {
            Toast.makeText(
                    this,
                    getString(R.string.noBluetooth),
                    Toast.LENGTH_LONG
                ).show()
        }
    }
}