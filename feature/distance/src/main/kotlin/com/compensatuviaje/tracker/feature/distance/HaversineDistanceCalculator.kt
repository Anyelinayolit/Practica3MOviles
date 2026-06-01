package com.compensatuviaje.tracker.feature.distance

import com.compensatuviaje.tracker.domain.DistanceCalculator
import com.compensatuviaje.tracker.model.GpsPoint
import kotlin.math.*

class HaversineDistanceCalculator(
    private val maxAccuracyMeters: Double = 50.0,
) : DistanceCalculator {

    override fun totalKm(points: List<GpsPoint>): Double {
        val good = points.filter { it.accuracyMeters <= maxAccuracyMeters }
        if (good.size < 2) return 0.0
        var total = 0.0
        for (i in 1 until good.size) total += haversineKm(good[i - 1], good[i])
        return total
    }

    private fun haversineKm(a: GpsPoint, b: GpsPoint): Double {
        val r = 6371.0088
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLng = Math.toRadians(b.lng - a.lng)
        val h = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(a.lat)) * cos(Math.toRadians(b.lat)) * sin(dLng / 2).pow(2)
        return 2 * r * asin(min(1.0, sqrt(h)))
    }
}
