package com.pc922.espaoilandroid.location

import com.pc922.espaoilandroid.model.AuthorizationState

data class LocationResult(
    val latitude: Double,
    val longitude: Double
)

interface LocationProvider {
    suspend fun getAuthorizationState(): AuthorizationState
    suspend fun requestCurrentLocation(): Result<LocationResult>
}

// Fake para pruebas
class FakeLocationProvider(
    private val authorize: Boolean = true
) : LocationProvider {
    override suspend fun getAuthorizationState(): AuthorizationState =
        if (authorize) AuthorizationState.AUTHORIZED else AuthorizationState.DENIED

    override suspend fun requestCurrentLocation(): Result<LocationResult> =
        if (authorize) Result.success(LocationResult(28.06, -16.62))
        else Result.failure(IllegalStateException("Acceso denegado"))
}