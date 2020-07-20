package com.example.estacao

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.navigation.findNavController

class SelectStation : Fragment(R.layout.fragment_select_station) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctrAvailableStations = view.findViewById<Spinner>(R.id.ctrAvailableStations)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_spinner_item,
            RegisteredStationsList.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ctrAvailableStations.adapter = adapter
        view.findViewById<Button>(R.id.ctrContinue).setOnClickListener {
            val selectedIndex = ctrAvailableStations.selectedItemPosition
            if (selectedIndex >= 0) {
                val selectedStation = RegisteredStationsList[selectedIndex]
                currentStation = selectedStation
                view!!.findNavController().navigate(R.id.action_selectStation_to_configStation)
            }
        }
    }
}