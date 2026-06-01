package com.compensatuviaje.tracker.testing

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockApiDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: ""
        return when {
            path.endsWith("/auth/login") -> MockResponse().setResponseCode(200).setBody(
                """{"token":"eyJfaketoken","driver_name":"Carlos Mendoza",
                   "truck":{"id":"truck-uuid-123","license_plate":"XYZ-987","category":"heavy_duty"}}""".trimIndent())
            path.endsWith("/trips/start") -> MockResponse().setResponseCode(201).setBody(
                """{"trip_id":"trip-uuid-888-999","status":"in_progress"}""")
            path.endsWith("/sync") -> MockResponse().setResponseCode(200).setBody(
                """{"success":true,"synced_points_count":2,"message":"Batch processed successfully"}""")
            path.endsWith("/end") -> MockResponse().setResponseCode(200).setBody(
                """{"trip_id":"trip-uuid-888-999","status":"completed",
                   "summary":{"server_calculated_distance_km":146.0,"total_co2_kg":112.5}}""".trimIndent())
            else -> MockResponse().setResponseCode(404)
        }
    }
}
