package com.pc922.espaoilandroid.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// API contract matching the provided example:
// GET /gas-stations/near?lat=...&lon=...&distance=5000&gasType=95_E5
// Response: JSON array of station objects
interface ApiService {
    @GET("gas-stations/near")
    suspend fun getNearbyStations(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("distance") distanceMeters: Int,
        @Query("gasType") gasType: String
    ): List<ApiStation>
}

// DTO representing an API station (fields from the sample response)
data class ApiStation(
    val name: String?,
    val town: String?,
    val municipality: String?,
    val schedule: String?,
    val price: String?,
    val latitude: String?,
    val longitude: String?
)
