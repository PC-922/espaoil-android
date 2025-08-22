package com.pc922.espaoilandroid.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pc922.espaoilandroid.data.remote.ApiStation
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

class ApiStationMappingTest {

    @Test
    fun `parse scratch json and map to GasStation`() {
        val json = this::class.java.classLoader.getResourceAsStream("scratch.json")!!.readAllBytes().toString(Charsets.UTF_8)
        val gson = Gson()
        val type = object : TypeToken<List<ApiStation>>() {}.type
        val list: List<ApiStation> = gson.fromJson(json, type)
        assertEquals(3, list.size)

        val originLat = 40.4168
        val originLon = -3.7038
    val mapped = list.mapIndexedNotNull { idx, api -> api.toGasStation(idx, originLat, originLon) }
        assertEquals(3, mapped.size)

        val first = mapped[0]
        assertEquals("BALLENOIL", first.name)
        assertNotNull(first.priceEurPerLitre)
        assertEquals(40.373, first.latitude)
        assertEquals(-3.65, first.longitude)
    }
}
