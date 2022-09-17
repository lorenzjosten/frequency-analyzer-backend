package io.github.frequencyanalyzer.unit.spectralanalysis

import io.github.frequencyanalyzer.spectralanalysis.model.PcmPowerSpectrum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PcmPowerSpectrumTest {

    @Test
    fun shouldAccumulate() {
        val fourierCoefficients = mapOf(0.0 to 0.0, 1.0 to 1.0, 2.0 to 2.0, 3.0 to 3.0)
        val powerSpectrum = PcmPowerSpectrum(fourierCoefficients)
        val other = PcmPowerSpectrum(fourierCoefficients)
        val expected = listOf(0.0 to 0.0, 1.0 to 2.0, 2.0 to 4.0, 3.0 to 6.0)
        val accumulated = powerSpectrum.accumulate(other)

        accumulated.entries.forEachIndexed { i, entry -> assertEquals(expected[i], entry.toPair()) }
    }
}
