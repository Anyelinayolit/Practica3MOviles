package com.compensatuviaje.tracker.model

data class LatLng(val lat: Double, val lng: Double)

enum class TripStatus { IN_PROGRESS, PENDING_END, COMPLETED }

data class Truck(
    val id: String,
    val licensePlate: String,
    val category: String,
)

data class Session(
    val token: String,
    val driverName: String,
    val truck: Truck,
)

data class GpsPoint(
    val id: Long = 0,
    val tripId: String,
    val timestampIso: String,
    val lat: Double,
    val lng: Double,
    val speedKmh: Double,
    val heading: Double,
    val accuracyMeters: Double,
    val synced: Boolean = false,
)

data class Trip(
    val id: String,
    val status: TripStatus,
    val startedAtIso: String,
    val endedAtIso: String? = null,
    val totalLocalDistanceKm: Double = 0.0,
    val isSyncedToServer: Boolean = false,
    val serverDistanceKm: Double? = null,
    val co2Kg: Double? = null,
)
