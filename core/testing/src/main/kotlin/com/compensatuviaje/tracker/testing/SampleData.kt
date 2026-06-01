package com.compensatuviaje.tracker.testing

import com.compensatuviaje.tracker.model.*

object SampleData {
    val truck = Truck("truck-uuid-123", "XYZ-987", "heavy_duty")
    val session = Session("eyJfaketoken", "Carlos Mendoza", truck)

    val sampleTrack: List<GpsPoint> = listOf(
        GpsPoint(1, "trip-1", "2026-06-01T14:31:00Z", -12.0470, -77.0430, 45.5, 180.0, 4.1),
        GpsPoint(2, "trip-1", "2026-06-01T14:32:00Z", -12.0485, -77.0430, 50.2, 180.0, 3.8),
        GpsPoint(3, "trip-1", "2026-06-01T14:33:00Z", -12.0500, -77.0430, 48.0, 180.0, 4.0),
    )
    val sampleTrip = Trip("trip-1", TripStatus.IN_PROGRESS, "2026-06-01T14:30:00Z")
}
