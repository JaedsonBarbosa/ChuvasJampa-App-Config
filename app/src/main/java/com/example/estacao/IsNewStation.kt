package com.example.estacao

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar

class IsNewStation : Fragment(R.layout.fragment_is_new_station) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctrContinue : Button = view.findViewById(R.id.ctrContinue)
        ctrContinue.setOnClickListener {
            val ctrNewStation : Switch = view.findViewById(R.id.ctrNewStation)
            val navController = view.findNavController()
            if (StationGPSAvailable || GPSAvailable) {
                navController.navigate(
                    if (ctrNewStation.isChecked) R.id.action_isNewStation_to_registerStation
                    else R.id.action_isNewStation_to_selectStation
                )
            } else {
                Snackbar.make(
                    view!!,
                    "Não há fonte de localização disponível",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}