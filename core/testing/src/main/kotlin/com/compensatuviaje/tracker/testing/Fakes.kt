package com.compensatuviaje.tracker.testing

import com.compensatuviaje.tracker.domain.*
import com.compensatuviaje.tracker.model.*
import kotlinx.coroutines.flow.*

class FakeMobileApi(
    var loginResult: AppResult<Session> = AppResult.Ok(SampleData.session),
) : MobileApi {
    override suspend fun login(driverId: String, pin: String, licensePlate: String) = loginResult
    override suspend fun startTrip(startedAtIso: String, start: LatLng, accuracyMeters: Double) =
        AppResult.Ok("trip-uuid-888-999")
    override suspend fun syncBatch(tripId: String, currentLocalDistanceKm: Double, points: List<GpsPoint>) =
        AppResult.Ok(points.size)
    override suspend fun endTrip(tripId: String, endedAtIso: String, end: LatLng, accuracyMeters: Double, totalLocalDistanceKm: Double) =
        AppResult.Ok(TripSummary(146.0, 112.5))
}

class FakeTripRepository : TripRepository {
    private val active = MutableStateFlow<Trip?>(null)
    private val completed = MutableStateFlow<List<Trip>>(emptyList())
    override fun activeTrip() = active.asStateFlow()
    override fun completedTrips() = completed.asStateFlow()
    override suspend fun create(trip: Trip) { active.value = trip }
    override suspend fun update(trip: Trip) { active.value = trip }
    override suspend fun setStatus(tripId: String, status: TripStatus) {
        active.value = active.value?.copy(status = status)
    }
    override suspend fun get(tripId: String) = active.value?.takeIf { it.id == tripId }
}

class FakeLocationTracker : LocationTracker {
    private val _points = MutableSharedFlow<GpsPoint>(extraBufferCapacity = 64)
    private val _tracking = MutableStateFlow(false)
    override val points = _points.asSharedFlow()
    override val isTracking = _tracking.asStateFlow()
    override fun start(tripId: String) { _tracking.value = true }
    override fun stop() { _tracking.value = false }
    suspend fun emit(p: GpsPoint) = _points.emit(p)
}

class FakeConnectivityMonitor(initial: Boolean = true) : ConnectivityMonitor {
    private val _online = MutableStateFlow(initial)
    override val isOnline = _online.asStateFlow()
    fun set(value: Boolean) { _online.value = value }
}
