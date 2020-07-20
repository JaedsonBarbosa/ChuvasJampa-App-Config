package com.example.estacao

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result;
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
                activity!!.runOnUiThread {
                    Snackbar.make(view!!, "Alterações salvas com sucesso", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Fechar") { activity?.finish() }
                        .show()
                }
            } else {
                activity!!.runOnUiThread {
                    Snackbar.make(view!!, "Erro ao tentar salvar", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Tentar\nNovamente") { salvar() }
                        .show()
                }
            }
        }
        SendToDevice(requestBody.toString())
    }
}