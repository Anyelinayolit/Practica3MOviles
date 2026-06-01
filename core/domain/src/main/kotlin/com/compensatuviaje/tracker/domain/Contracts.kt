package com.compensatuviaje.tracker.domain

import com.compensatuviaje.tracker.model.*
import kotlinx.coroutines.flow.Flow

sealed interface AppResult<out T> {
    data class Ok<T>(val value: T) : AppResult<T>
    data class Err(val kind: ErrorKind, val message: String? = null) : AppResult<Nothing>
}
enum class ErrorKind { NETWORK, UNAUTHORIZED, NOT_FOUND, CONFLICT, VALIDATION, SERVER, UNKNOWN }

interface MobileApi {
    suspend fun login(driverId: String, pin: String, licensePlate: String): AppResult<Session>
    suspend fun startTrip(startedAtIso: String, start: LatLng, accuracyMeters: Double): AppResult<String>
    suspend fun syncBatch(tripId: String, currentLocalDistanceKm: Double, points: List<GpsPoint>): AppResult<Int>
    suspend fun endTrip(tripId: String, endedAtIso: String, end: LatLng, accuracyMeters: Double, totalLocalDistanceKm: Double): AppResult<TripSummary>
}
data class TripSummary(val serverDistanceKm: Double, val co2Kg: Double)

interface TokenStorage {
    fun save(token: String)
    fun get(): String?
    fun clear()
}

interface SessionRepository {
    val current: Flow<Session?>
    suspend fun setSession(session: Session)
    suspend fun logout()
}

interface TripRepository {
    fun activeTrip(): Flow<Trip?>
    fun completedTrips(): Flow<List<Trip>>
    suspend fun create(trip: Trip)
    suspend fun update(trip: Trip)
    suspend fun setStatus(tripId: String, status: TripStatus)
    suspend fun get(tripId: String): Trip?
}
interface GpsPointRepository {
    suspend fun insert(point: GpsPoint)
    suspend fun unsynced(tripId: String): List<GpsPoint>
    suspend fun markSynced(ids: List<Long>)
    fun pointsForTrip(tripId: String): Flow<List<GpsPoint>>
}

interface LocationTracker {
    val points: Flow<GpsPoint>
    fun start(tripId: String)
    fun stop()
    val isTracking: Flow<Boolean>
}

interface DistanceCalculator {
    fun totalKm(points: List<GpsPoint>): Double
}

interface SyncManager {
    fun schedule(tripId: String)
    fun cancel()
}

interface ConnectivityMonitor { val isOnline: Flow<Boolean> }

interface RemoteMirror { suspend fun mirror(trip: Trip): AppResult<Unit> }
