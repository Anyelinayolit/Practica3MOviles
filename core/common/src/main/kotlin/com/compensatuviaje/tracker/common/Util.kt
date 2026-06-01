package com.compensatuviaje.tracker.common

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object Iso8601 {
    private val fmt = DateTimeFormatter.ISO_INSTANT
    fun now(): String = fmt.format(Instant.now())
    fun of(epochMillis: Long): String = fmt.format(Instant.ofEpochMilli(epochMillis).atOffset(ZoneOffset.UTC).toInstant())
}

fun formatKm(km: Double): String = "%.1f km".format(km)
fun formatDuration(seconds: Long): String =
    "%02d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, seconds % 60)
