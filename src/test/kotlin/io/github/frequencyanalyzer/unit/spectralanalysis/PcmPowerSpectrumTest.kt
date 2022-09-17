package io.github.frequencyanalyzer.unit.spectralanalysis

import io.github.frequencyanalyzer.spectralanalysis.model.PcmPowerSpectrum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.log10

class PcmPowerSpectrumTest {

    private val powerSpectrum: PcmPowerSpectrum = PcmPowerSpectrum(fourierCoefficients())

    @Test
    fun shouldAccumulate() {
        val other = PcmPowerSpectrum(fourierCoefficients())
        val expected = listOf(0 to 0.0, 1 to 2.0, 2 to 4.0, 3 to 6.0)
        val accumulated = powerSpectrum.accumulate(other)

        accumulated.entries.forEachIndexed { i, entry -> assertEquals(expected[i], entry.toPair()) }
    }

    @Test
    fun shouldScaleCoefficients() {
        val expected = listOf(0 to 0.0, 1 to 3.0, 2 to 3.0)
        val scaled = powerSpectrum.scaleCoefficients(2, 4)

        scaled.entries.forEachIndexed { i, entry -> assertEquals(expected[i], entry.toPair()) }
    }

    @Test
    fun shouldLogScaleMagnitude() {
        val expected = powerSpectrum.mapValues { PcmPowerSpectrum.DECIBEL_FACTOR * log10(it.value) }.toList()
        val scaled = powerSpectrum.logScaleMagnitude()

        scaled.entries.forEachIndexed { i, entry -> assertEquals(expected[i], entry.toPair()) }
    }

    private fun fourierCoefficients(): MutableMap<Int, Double> {
        return mutableMapOf(0 to 0.0, 1 to 1.0, 2 to 2.0, 3 to 3.0)
    }
}
