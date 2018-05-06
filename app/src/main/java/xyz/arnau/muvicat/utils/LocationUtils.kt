package xyz.arnau.muvicat.utils

import android.location.Location
import kotlin.math.ceil

object LocationUtils {
    fun getDistance(location: Location, latitude: Double, longitude: Double): Int {
        val theta = location.longitude - longitude
        var dist =
            Math.sin(deg2rad(location.latitude)) * Math.sin(deg2rad(latitude)) + Math.cos(deg2rad(location.latitude)) * Math.cos(
                deg2rad(latitude)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515
        dist *= 1.609344

        return ceil(dist).toInt()
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}