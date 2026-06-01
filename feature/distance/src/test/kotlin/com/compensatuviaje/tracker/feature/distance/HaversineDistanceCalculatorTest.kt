package com.compensatuviaje.tracker.feature.distance

import com.compensatuviaje.tracker.model.GpsPoint
import com.compensatuviaje.tracker.testing.SampleData
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HaversineDistanceCalculatorTest {
    private val calc = HaversineDistanceCalculator()

    @Test fun `empty or single point returns zero`() {
        assertThat(calc.totalKm(emptyList())).isEqualTo(0.0)
        assertThat(calc.totalKm(listOf(SampleData.sampleTrack.first()))).isEqualTo(0.0)
    }

    @Test fun `accumulates distance over a known track`() {
        val d = calc.totalKm(SampleData.sampleTrack)
        assertThat(d).isGreaterThan(0.0)
        assertThat(d).isLessThan(1.0)
    }

    @Test fun `discards low accuracy points`() {
        val noisy = SampleData.sampleTrack + SampleData.sampleTrack.first().copy(id = 99, accuracyMeters = 120.0)
        assertThat(calc.totalKm(noisy)).isEqualTo(calc.totalKm(SampleData.sampleTrack))
    }
}
