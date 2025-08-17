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