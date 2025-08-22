package com.pc922.espaoilandroid.data

import com.pc922.espaoilandroid.data.remote.ApiService
import android.util.Log
import com.pc922.espaoilandroid.model.FuelType
import com.pc922.espaoilandroid.model.GasStation
import com.pc922.espaoilandroid.data.remote.ApiStation
import com.pc922.espaoilandroid.model.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RealGasStationRepository(private val baseUrl: String?) : GasStationRepository {

    private val api: ApiService? = if (!baseUrl.isNullOrBlank()) {
        val timeoutMs = com.pc922.espaoilandroid.BuildConfig.NETWORK_TIMEOUT_MS
        val client = OkHttpClient.Builder()
            .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .callTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    } else null
    // If no baseUrl is configured, do not fallback to fake data; return failure so UI can show error.

    override suspend fun findNearbyStations(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        fuelType: FuelType
    ): Result<List<GasStation>> {
        if (api == null) {
            // In debug builds keep failure to help development; in release, return empty list silently
            return if (com.pc922.espaoilandroid.BuildConfig.DEBUG) {
                Result.failure(IllegalStateException("BASE_API_URL is not configured"))
            } else {
                Result.success(emptyList())
            }
        }

        return try {
            val distanceMeters = (radiusKm * 1000).toInt()
            if (com.pc922.espaoilandroid.BuildConfig.DEBUG) {
                Log.d("RealRepo", "BASE_API_URL=$baseUrl, timeoutMs=${com.pc922.espaoilandroid.BuildConfig.NETWORK_TIMEOUT_MS}")
            }
            // Log the exact request URL for debugging
            if (com.pc922.espaoilandroid.BuildConfig.DEBUG) {
                val base = baseUrl?.trimEnd('/') ?: ""
                val requestUrl = "${if (base.isNotEmpty()) base else ""}/gas-stations/near?lat=$latitude&lon=$longitude&distance=$distanceMeters&gasType=${fuelType.apiParam}"
                Log.d("RealRepo", "Requesting $requestUrl")
            }
            val resp = api.getNearbyStations(latitude, longitude, distanceMeters, fuelType.apiParam)
            if (com.pc922.espaoilandroid.BuildConfig.DEBUG) {
                Log.d("RealRepo", "API returned ${resp.size} stations")
            }
            val mapped = resp.mapIndexedNotNull { idx, apiStation ->
                apiStation.toGasStation(idx, latitude, longitude)
            }
            if (com.pc922.espaoilandroid.BuildConfig.DEBUG) {
                Log.d("RealRepo", "Mapped ${mapped.size} stations")
            }
            Result.success(mapped)
        } catch (ex: Exception) {
            // On network or parsing error, return failure with message
            if (com.pc922.espaoilandroid.BuildConfig.DEBUG) {
                Log.e("RealRepo", "Error fetching stations", ex)
            }
            Result.failure(ex)
        }
    }
}
