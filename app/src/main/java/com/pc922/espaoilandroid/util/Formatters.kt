package com.pc922.espaoilandroid.util

import java.text.DecimalFormat

private val priceFormat = DecimalFormat("0.000")
private val distanceFormat = DecimalFormat("0.0#")

fun formatPriceEurPerLitre(value: Double?): String =
    value?.let { "${priceFormat.format(it)} €/L" } ?: "N/A"

fun formatDistanceKm(value: Double?): String =
    value?.let { "${distanceFormat.format(it)} km" } ?: ""