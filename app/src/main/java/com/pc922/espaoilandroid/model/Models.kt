package com.pc922.espaoilandroid.model

enum class FuelType(val displayName: String) {
    GASOLINA_95_E5("95 E5"),
    GASOLINA_95_E5_PREMIUM("95 E5 Premium"),
    GASOLINA_95_E10("95 E10"),
    GASOLINA_98_E5("98 E5"),
    GASOLINA_98_E10("98 E10"),
    GASOIL_A("Gasoil A"),
    GASOIL_B("Gasoil B"),
    GASOIL_PREMIUM("Gasoil Premium"),
    BIODIESEL("Biodiesel"),
    BIOETANOL("Bioetanol"),
    GAS_NATURAL_COMPRIMIDO("Gas Natural Comprimido"),
    GAS_NATURAL_LICUADO("Gas Natural Licuado"),
    GLP("Gases licuados del petróleo"),
    HIDROGENO("Hidrógeno");

    companion object {
        val default = GASOLINA_95_E5
    }
}

// Simple API name mapping for fuel types. Adjust values to match your backend API.
// Mapping to the API's expected gasType values, adjust as needed.
val FuelType.apiParam: String
    get() = when (this) {
        FuelType.GASOLINA_95_E5 -> "95_E5"
        FuelType.GASOLINA_95_E5_PREMIUM -> "95_E5_PREMIUM"
        FuelType.GASOLINA_95_E10 -> "95_E10"
        FuelType.GASOLINA_98_E5 -> "98_E5"
        FuelType.GASOLINA_98_E10 -> "98_E10"
        FuelType.GASOIL_A -> "GASOIL_A"
        FuelType.GASOIL_B -> "GASOIL_B"
        FuelType.GASOIL_PREMIUM -> "GASOIL_PREMIUM"
        FuelType.BIODIESEL -> "BIODIESEL"
        FuelType.BIOETANOL -> "BIOETANOL"
        FuelType.GAS_NATURAL_COMPRIMIDO -> "GNC"
        FuelType.GAS_NATURAL_LICUADO -> "GNL"
        FuelType.GLP -> "GLP"
        FuelType.HIDROGENO -> "HIDROGENO"
    }

enum class SortOption { PRICE, DISTANCE }

enum class AuthorizationState { AUTHORIZED, DENIED, NOT_DETERMINED, LOADING }

data class GasStation(
    val id: String,
    val name: String,
    val address: String,
    val municipality: String?,
    val latitude: Double?,
    val longitude: Double?,
    val schedule: String?,
    val priceEurPerLitre: Double?,
    val distanceKm: Double?
)