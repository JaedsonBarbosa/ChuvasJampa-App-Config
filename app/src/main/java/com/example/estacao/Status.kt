package com.example.estacao

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class Status : Fragment(R.layout.fragment_status) {
    private val currentStatus = StationStatus()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctrStatusBluetooth = view.findViewById<ImageButton>(R.id.ctrStatusBluetooth)
        val ctrStatusCloud = view.findViewById<ImageButton>(R.id.ctrStatusCloud)
        val ctrStatusWiFi = view.findViewById<ImageButton>(R.id.ctrStatusWiFi)
        val ctrStatusClock = view.findViewById<ImageButton>(R.id.ctrStatusClock)
        val ctrStatusGPS = view.findViewById<ImageButton>(R.id.ctrStatusGPS)

        ctrStatusBluetooth.setOnClickListener {
            Snackbar.make(
                it,
                getString(R.string.statusBluetooth),
                Snackbar.LENGTH_LONG
            ).show()
        }

        ctrStatusCloud.setOnClickListener {
            Snackbar.make(
                it,
                if (currentStatus.cloud) getString(R.string.statusCloudOK)
                else getString(R.string.statusCloudNo),
                Snackbar.LENGTH_LONG
            ).show()
        }

        ctrStatusWiFi.setOnClickListener {
            Snackbar.make(
                it,
                if (currentStatus.wifi) getString(R.string.statusWiFiOK)
                else getString(R.string.statusWiFiNo),
                Snackbar.LENGTH_LONG
            ).show()
        }

        ctrStatusClock.setOnClickListener {
            Snackbar.make(
                it,
                if (currentStatus.clockGPS) getString(R.string.statusClockGPS)
                else if (currentStatus.clockNTP) getString(R.string.statusClockNTP)
                else getString(R.string.statusClockNo),
                Snackbar.LENGTH_LONG
            ).show()
        }

        ctrStatusGPS.setOnClickListener {
            Snackbar.make(
                it,
                if (!StationGPSAvailable) getString(R.string.statusGpsNo)
                else if (currentStatus.gps) getString(R.string.statusGpsSignalOK)
                else getString(R.string.statusGpsSignalNo),
                Snackbar.LENGTH_LONG
            ).show()
        }

        val activeColor = ContextCompat.getColor(view.context, R.color.colorAccent)
        CurrentCallback = {
            val json = JSONObject(it)
            currentStatus.cloud = json["nuvemConectada"] as Boolean
            currentStatus.bluetooth = json["bluetoothAtivado"] as Boolean
            currentStatus.clockGPS = json["relogioConfiguradoGPS"] as Boolean
            currentStatus.clockNTP = json["relogioConfiguradoNTP"] as Boolean
            currentStatus.wifi = json["wifiConectado"] as Boolean
            currentStatus.gps = json["gpsConectado"] as Boolean

            fun analyse(control: ImageButton, status: Boolean) {
                control.backgroundTintList = if (status) {
                    ColorStateList.valueOf(activeColor)
                } else null
            }

            val ctrLoading = view.findViewById<ProgressBar>(R.id.ctrLoading)
            ctrLoading.visibility = View.GONE
            analyse(ctrStatusBluetooth, currentStatus.bluetooth)
            analyse(ctrStatusCloud, currentStatus.cloud)
            analyse(ctrStatusWiFi, currentStatus.wifi)
            analyse(ctrStatusClock, currentStatus.clockGPS || currentStatus.clockNTP)
            analyse(ctrStatusGPS, currentStatus.gps)
        }
        SendToDevice("{\"metodo\":\"GetStatus\"}")

        view.findViewById<ImageButton>(R.id.ctrOpenConfig).setOnClickListener {
            view.findNavController().navigate(R.id.action_status_to_isNewStation)
        }
    }
}