package com.pc922.espaoilandroid.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.pc922.espaoilandroid.R

fun openDrivingDirections(
    context: Context,
    latitude: Double,
    longitude: Double,
    name: String?
) {
    val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(mapIntent)
    } catch (_: ActivityNotFoundException) {
        // Fallback a cualquier app de mapas
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
        val fallback = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(fallback)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.maps_not_found), Toast.LENGTH_SHORT).show()
        }
    }
}