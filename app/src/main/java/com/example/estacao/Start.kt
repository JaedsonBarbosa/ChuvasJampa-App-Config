package com.example.estacao

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.android.material.snackbar.Snackbar

class StationDB (
    var id: String = "",
    var name: String = "",
    var cityCode: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var homologation: Boolean = false
)

val RegisteredStationsList = mutableListOf<StationDB>()

class Start : Fragment(R.layout.fragment_start) {
    private fun requestStations(view: View) {
        val httpAsync = "http://192.168.0.109:5001/chuvasjampa/us-central1/GetEstacoesProprias"
            .httpGet()
            .responseJson() { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        val notText = "Erro ao buscar as estações."
                        val not = Snackbar.make(view, notText, Snackbar.LENGTH_INDEFINITE)
                        not.setAction("Tentar\nnovamente") { requestStations(view) }
                        not.show()
                    }
                    is Result.Success -> {
                        val stations = result.get().array()
                        val numStations = stations.length()
                        if (numStations > 0) RegisteredStationsList.clear()
                        for (i in 0 until numStations) {
                            val station = stations.getJSONObject(i)
                            val isActive = station["Ativa"] as Boolean
                            if (!isActive) continue
                            val id = station["id"] as String
                            val cityCode = station["CodIBGE"] as Int
                            val local = station.getJSONObject("Local")
                            val latitude = local["_latitude"] as Double
                            val longitude = local["_longitude"] as Double
                            val name = station["Nome"] as String
                            val homologation = station["Homologacao"] as Boolean
                            RegisteredStationsList.add(StationDB(
                                id,
                                name,
                                cityCode,
                                latitude,
                                longitude,
                                homologation
                            ))
                        }
                        activity!!.runOnUiThread {
                            view.findNavController().navigate(R.id.action_start_to_selectDevice)
                        }
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = requestStations(view)
}