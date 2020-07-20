package com.example.estacao

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class StationStatus(
    var cloud: Boolean = false,
    var bluetooth: Boolean = false,
    var clockGPS: Boolean = false,
    var clockNTP: Boolean = false,
    var wifi: Boolean = false,
    var gps: Boolean = false
)

var CurrentCallback : (String) -> Unit = { }
lateinit var SendToDevice : (message: String) -> Unit

var GPSAvailable: Boolean = false
var StationGPSAvailable = false

var currentSSID = ""
var currentPassword = ""
lateinit var currentStation: StationDB

class SelectDevice : Fragment(R.layout.fragment_select_device) {
    lateinit var ctrDevices : Spinner
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gpsPermission = ActivityCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (gpsPermission != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, 200)
        }

        ctrDevices = view.findViewById(R.id.ctrDevices) as Spinner
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_spinner_item,
            bluetoothManager.pairedDevicesList.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ctrDevices.adapter = adapter
        view.findViewById<Button>(R.id.ctrConnect).setOnClickListener {
            val selectedPosition = ctrDevices.selectedItemPosition
            val mac = bluetoothManager.pairedDevicesList[selectedPosition].address
            bluetoothManager.openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onConnected) {
                    Snackbar.make(view!!, "Erro na conexão.", Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    fun onConnected(connectedDevice: BluetoothSerialDevice) {
        val device = connectedDevice.toSimpleDeviceInterface()
        device.setListeners({ CurrentCallback(it) }, { }) {
            Snackbar.make(
                view!!,
                "Erro desconhecido.",
                Snackbar.LENGTH_INDEFINITE
            ).show()
        }
        SendToDevice = { device.sendMessage(it) }
        CurrentCallback = { message ->
            val json = JSONObject(message)
            currentSSID = json["ssidWiFi"] as String
            currentPassword = json["senhaWiFi"] as String
            StationGPSAvailable = json["possuiGPS"] as Boolean

            if (StationGPSAvailable || GPSAvailable) {
                val id = json["idEstacao"] as String
                val navController = view!!.findNavController()
                if (RegisteredStationsList.any { v -> v.id == id }) {
                    currentStation = RegisteredStationsList.first { v -> v.id == id }
                    navController.navigate(R.id.action_selectDevice_to_status)
                } else {
                    if (RegisteredStationsList.count() > 0) {
                        navController.navigate(R.id.action_selectDevice_to_isNewStation)
                    } else {
                        navController.navigate(R.id.action_selectDevice_to_registerStation)
                    }
                }
            } else {
                Snackbar.make(
                    view!!,
                    "Não há fonte de localização disponível",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        SendToDevice("{\"metodo\":\"GetDados\"}")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            GPSAvailable = true
        } else {
            Snackbar.make(
                view!!,
                "Apenas o GPS da estação poderá ser usado então.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}