package com.pc922.espaoilandroid.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.pc922.espaoilandroid.model.AuthorizationState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class DeviceLocationProvider(private val context: Context) : LocationProvider {

    override suspend fun getAuthorizationState(): AuthorizationState {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        return if (fine == android.content.pm.PackageManager.PERMISSION_GRANTED) AuthorizationState.AUTHORIZED
        else AuthorizationState.NOT_DETERMINED
    }

    override suspend fun requestCurrentLocation(): Result<LocationResult> {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (fine != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return Result.failure(IllegalStateException("Location permission not granted"))
        }

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return Result.failure(IllegalStateException("LocationManager not available"))
    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

    // Always request a single fresh update (ignore any last known location)
    return try {
            val fresh: Location? = withTimeoutOrNull(5000L) {
                suspendCancellableCoroutine { cont ->
                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            if (cont.isActive) {
                                cont.resume(location)
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }

                    // Register listener for both providers - ignore exceptions per provider
                    providers.forEach { p ->
                        try { lm.requestLocationUpdates(p, 0L, 0f, listener, Looper.getMainLooper()) } catch (_: Exception) {}
                    }

                    cont.invokeOnCancellation {
                        try { lm.removeUpdates(listener) } catch (_: Exception) {}
                    }
                }
            }

            if (fresh != null) {
                Result.success(LocationResult(fresh.latitude, fresh.longitude))
            } else {
                Result.failure(IllegalStateException("No location available"))
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
