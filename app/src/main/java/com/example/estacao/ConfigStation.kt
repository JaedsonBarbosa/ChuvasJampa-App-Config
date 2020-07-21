package com.example.estacao

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class ConfigStation : Fragment(R.layout.fragment_config_station) {
    private lateinit var ctrSSID: TextInputEditText
    private lateinit var ctrPassword: TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ctrSSID = view.findViewById(R.id.ctrSSID)
        ctrPassword = view.findViewById(R.id.ctrPassword)
        ctrSSID.setText(currentSSID)
        ctrPassword.setText(currentPassword)
        view.findViewById<Button>(R.id.ctrSave).setOnClickListener { salvar() }
    }

    private fun salvar() {
        currentPassword = ctrPassword.text.toString()
        currentSSID = ctrSSID.text.toString()
        val requestBody = JSONObject()
        requestBody.put("metodo", "SetDados")
        requestBody.put("senhaWiFi", currentPassword)
        requestBody.put("ssidWiFi", currentSSID)
        requestBody.put("idEstacao", currentStation.id)
        CurrentCallback = {
            val responseBody = JSONObject(it)
            if (responseBody["success"] as Boolean) {
                requireActivity().runOnUiThread {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.successConfig),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(getString(R.string.close)) { activity?.finish() }
                        .show()
                }
            } else {
                requireActivity().runOnUiThread {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.errorSave),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(getString(R.string.retry)) { salvar() }
                        .show()
                }
            }
        }
        SendToDevice(requestBody.toString())
    }
}