package com.pc922.espaoilandroid.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pc922.espaoilandroid.data.GasStationRepository
import com.pc922.espaoilandroid.location.LocationProvider
import com.pc922.espaoilandroid.model.AuthorizationState
import com.pc922.espaoilandroid.model.FuelType
import com.pc922.espaoilandroid.model.GasStation
import com.pc922.espaoilandroid.model.SortOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyLocationSearchUiState(
    val title: String = "EspaOil",
    val selectedFuelType: FuelType = FuelType.default,
    val radiusKmInput: String = "20",
    val authorizationState: AuthorizationState = AuthorizationState.NOT_DETERMINED,
    val isLoadingLocation: Boolean = false,
    val isLoadingStations: Boolean = false,
    val locationErrorMessage: String? = null,
    val stationsErrorMessage: String? = null,
    val sortOption: SortOption = SortOption.PRICE,
    val stations: List<GasStation> = emptyList()
)

class MyLocationSearchViewModel(
    private val repository: GasStationRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyLocationSearchUiState())
    val uiState: StateFlow<MyLocationSearchUiState> = _uiState

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(authorizationState = AuthorizationState.LOADING) }
            val auth = locationProvider.getAuthorizationState()
            _uiState.update { it.copy(authorizationState = auth) }
        }
    }

    fun onFuelTypeSelected(type: FuelType) {
        _uiState.update { it.copy(selectedFuelType = type) }
    }

    fun onRadiusChanged(input: String) {
        _uiState.update { it.copy(radiusKmInput = input) }
    }

    fun onSortOptionSelected(option: SortOption) {
        _uiState.update { state ->
            val sorted = sortStations(state.stations, option)
            state.copy(sortOption = option, stations = sorted)
        }
    }

    /**
     * Re-check authorization state from the location provider and update UI.
     * Called after the UI requests runtime permission and the user responds.
     */
    fun refreshAuthorizationState() {
        viewModelScope.launch {
            val auth = locationProvider.getAuthorizationState()
            _uiState.update { it.copy(authorizationState = auth) }
        }
    }

    fun search() {
        if (_uiState.value.isLoadingLocation || _uiState.value.isLoadingStations) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingLocation = true,
                    locationErrorMessage = null,
                    stationsErrorMessage = null
                )
            }
            try {
            val radius = _uiState.value.radiusKmInput.toDoubleOrNull() ?: 0.0
            when (locationProvider.getAuthorizationState()) {
                AuthorizationState.DENIED -> {
                    _uiState.update {
                        it.copy(
                            isLoadingLocation = false,
                            authorizationState = AuthorizationState.DENIED,
                            locationErrorMessage = "Acceso denegado"
                        )
                    }
                    return@launch
                }
                AuthorizationState.NOT_DETERMINED -> {
                    _uiState.update { it.copy(authorizationState = AuthorizationState.LOADING) }
                }
                else -> Unit
            }

            delay(300) // animación suave
            val location = locationProvider.requestCurrentLocation()
            if (location.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoadingLocation = false,
                        authorizationState = AuthorizationState.DENIED,
                        locationErrorMessage = "No se pudo obtener la ubicación"
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoadingLocation = false,
                    authorizationState = AuthorizationState.AUTHORIZED,
                    isLoadingStations = true
                )
            }

            val loc = location.getOrThrow()
            val result = repository.findNearbyStations(
                latitude = loc.latitude,
                longitude = loc.longitude,
                radiusKm = radius,
                fuelType = _uiState.value.selectedFuelType
            )

            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoadingStations = false,
                        stationsErrorMessage = result.exceptionOrNull()?.message ?: "Error cargando gasolineras"
                    )
                }
                return@launch
            }

            val stationsList = result.getOrThrow()
            if (stationsList.isEmpty() && com.pc922.espaoilandroid.BuildConfig.DEBUG) {
                android.util.Log.d("MyLocationVM", "Repository returned empty list of stations")
            }
            val sorted = sortStations(stationsList, _uiState.value.sortOption)
            _uiState.update {
                it.copy(
                    isLoadingStations = false,
                    stations = sorted,
                    stationsErrorMessage = if (stationsList.isEmpty()) "" else null
                )
            }
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingLocation = false,
                        isLoadingStations = false,
                        stationsErrorMessage = ex.message ?: "Error inesperado"
                    )
                }
            }
        }
    }

    private fun sortStations(list: List<GasStation>, option: SortOption): List<GasStation> =
        when (option) {
            SortOption.PRICE -> list.sortedWith(compareBy(nullsLast()) { it.priceEurPerLitre })
            SortOption.DISTANCE -> list.sortedWith(compareBy(nullsLast()) { it.distanceKm })
        }
}