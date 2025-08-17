package com.pc922.espaoilandroid.data

import com.pc922.espaoilandroid.model.FuelType
import com.pc922.espaoilandroid.model.GasStation

interface GasStationRepository {
    suspend fun findNearbyStations(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        fuelType: FuelType
    ): Result<List<GasStation>>
}

// Fake para previews y pruebas de UI
class FakeGasStationRepository : GasStationRepository {
    override suspend fun findNearbyStations(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        fuelType: FuelType
    ): Result<List<GasStation>> {
        val sample = listOf(
            GasStation(
                id = "1",
                name = "OCÉANO",
                address = "Chafiras (Las), San Miguel de Abona",
                municipality = "Adeje",
                latitude = 28.061,
                longitude = -16.611,
                schedule = "L-D: 24H",
                priceEurPerLitre = 0.925,
                distanceKm = 4.1
            ),
            GasStation(
                id = "2",
                name = "PLENERGY",
                address = "Chafiras (Las), San Miguel de Abona",
                municipality = "Adeje",
                latitude = 28.062,
                longitude = -16.62,
                schedule = "L-D: 24H",
                priceEurPerLitre = 0.925,
                distanceKm = 4.5
            ),
            GasStation(
                id = "3",
                name = "TGAS-TU TRÉBOL",
                address = "Adeje",
                municipality = "Adeje",
                latitude = 28.09,
                longitude = -16.65,
                schedule = "L-D: 08:00-21:00",
                priceEurPerLitre = 0.98,
                distanceKm = 10.2
            )
        )
        return Result.success(sample)
    }
}