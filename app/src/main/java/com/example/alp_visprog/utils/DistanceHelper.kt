package com.example.alp_visprog.utils


import android.location.Location

object DistanceHelper {
    fun getDistance(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): String {
        val results = FloatArray(1)
        // Built-in Android calculation
        Location.distanceBetween(startLat, startLon, endLat, endLon, results)

        val distanceInMeters = results[0]

        return if (distanceInMeters >= 1000) {
            // If > 1 km, show in KM (e.g., "2.5 km")
            String.format("%.1f km", distanceInMeters / 1000)
        } else {
            // If < 1 km, show in Meters (e.g., "500 m")
            "${distanceInMeters.toInt()} m"
        }
    }
}