package com.example.estacao

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class RegisterStation : Fragment(R.layout.fragment_register_station), View.OnClickListener, LocationListener {
    private lateinit var locationManager: LocationManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!GPSAvailable || !StationGPSAvailable) {
            val ctrUseStationGPS = view!!.findViewById<Switch>(R.id.ctrUseStationGPS)
            ctrUseStationGPS.visibility =  View.GONE
        }

        currentStation = StationDB()
        val ctrSave : Button = view.findViewById(R.id.ctrContinue)
        ctrSave.setOnClickListener(this)
    }

    override fun onClick(v0: View?) {
        val ctrName : TextInputEditText = view!!.findViewById(R.id.ctrName)
        val ctrCityCode : TextInputEditText = view!!.findViewById(R.id.ctrCityCode)
        val ctrHomologation : Switch = view!!.findViewById(R.id.ctrHomologation)
        currentStation.name = ctrName.text.toString()
        currentStation.cityCode = ctrCityCode.text.toString().toInt()
        currentStation.homologation = ctrHomologation.isChecked

        val ctrUseStationGPS = view!!.findViewById<Switch>(R.id.ctrUseStationGPS)
        if (ctrUseStationGPS.isChecked || !GPSAvailable) {
            CurrentCallback = this::onGetGPS
            SendToDevice("{\"metodo\":\"GetGPS\"}")
        } else {
            locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsPermission = ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (gpsPermission == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1.0f,
                    this
                )
            } else {
                Snackbar.make(
                    view!!,
                    "As permissões de acesso não foram concedidas.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun onGetGPS(message: String) {
        val json = JSONObject(message)
        val valid = json["valid"] as Boolean
        if (valid) {
            currentStation.latitude = json["lat"] as Double
            currentStation.longitude = json["lon"] as Double
            save()
        } else Snackbar.make(view!!, "Sem sinal do GPS", Snackbar.LENGTH_SHORT).show()
    }

    private fun save() {
        val requestBody = JSONObject()
        requestBody.put("Nome", currentStation.name)
        requestBody.put("CodIBGE", currentStation.cityCode)
        requestBody.put("Latitude", currentStation.latitude)
        requestBody.put("Longitude", currentStation.longitude)
        requestBody.put("Homologacao", currentStation.homologation)

        "http://192.168.0.109:5001/chuvasjampa/us-central1/CadastrarEstacao"
            .httpPost()
            .jsonBody(requestBody.toString())
            .responseString() { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        activity!!.runOnUiThread {
                            Snackbar.make(view!!, "Erro ao salvar", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Tentar\nNovamente") { save() }
                                .show()
                        }
                    }
                    is Result.Success -> {
                        currentStation.id = result.get()
                        activity!!.runOnUiThread {
                            view!!.findNavController().navigate(R.id.action_registerStation_to_configStation)
                        }
                    }
                }
            }
            .join()
    }

    var isLocationFind = false

    override fun onLocationChanged(p0: Location) {
        if (!isLocationFind && p0.accuracy < 10) {
            isLocationFind = true
            locationManager.removeUpdates(this)
            currentStation.latitude = p0.latitude
            currentStation.longitude = p0.longitude
            save()
        }
    }
}