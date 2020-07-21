package com.example.estacao

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
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
                requireView().findNavController().navigate(R.id.action_selectStation_to_configStation)
            }
        }
    }
}