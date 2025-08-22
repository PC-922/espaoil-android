package com.pc922.espaoilandroid.data

import com.pc922.espaoilandroid.data.remote.ApiStation
import com.pc922.espaoilandroid.model.GasStation
import com.pc922.espaoilandroid.util.haversineDistanceKm

/**
 * Public mapper used by repository and tests.
 */
fun ApiStation.toGasStation(index: Int, originLat: Double, originLon: Double): GasStation? {
    val lat = latitude?.toDoubleOrNull()
    val lon = longitude?.toDoubleOrNull()
    val price = price?.replace(',', '.').orEmpty().toDoubleOrNull()
    val id = "station-$index"
    val distanceKm = if (lat != null && lon != null) {
        haversineDistanceKm(originLat, originLon, lat, lon)
    } else null
    return GasStation(
        id = id,
        name = name ?: "",
        address = town ?: "",
        municipality = municipality ?: "",
        latitude = lat,
        longitude = lon,
        schedule = schedule,
        priceEurPerLitre = price,
        distanceKm = distanceKm
    )
}
