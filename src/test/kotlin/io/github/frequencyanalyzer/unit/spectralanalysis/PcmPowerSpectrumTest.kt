package io.github.frequencyanalyzer.unit.spectralanalysis

import io.github.frequencyanalyzer.spectralanalysis.extension.rms
import io.github.frequencyanalyzer.spectralanalysis.model.PcmPowerSpectrum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.log10

class PcmPowerSpectrumTest {

    private val powerSpectrum: PcmPowerSpectrum = PcmPowerSpectrum(fourierCoefficients())

    @Test
    fun shouldAccumulate() {
        val other = PcmPowerSpectrum(fourierCoefficients())
        val accumulated = powerSpectrum.accumulate(other)
        val expected = List(powerSpectrum.size) { i -> powerSpectrum[i]!! + other[i]!! }

        accumulated.values.forEachIndexed { i, magnitude -> assertEquals(expected[i], magnitude) }
    }

    @Test
    fun shouldScaleCoefficients() {
        val sampleRate = 2
        val sampleSize = 4
        val scalingFactor = sampleRate.toDouble() / sampleSize
        val scaled = powerSpectrum.scaleCoefficients(sampleRate, sampleSize)
        val expected = powerSpectrum.keys.map { it * scalingFactor }

        scaled.keys.forEachIndexed { i, coefficient -> assertEquals(expected[i], coefficient) }
    }

    @Test
    fun shouldLogScaleMagnitude() {
        val scaled = powerSpectrum.logScaleMagnitude()
        val expected = powerSpectrum.values.map { PcmPowerSpectrum.DECIBEL_FACTOR * log10(powerSpectrum.rms()) }

        scaled.values.forEachIndexed { i, magnitude -> assertEquals(expected[i], magnitude) }
    }

    private fun fourierCoefficients(): MutableMap<Int, Double> {
        return mutableMapOf(0 to 0.0, 1 to 1.0, 2 to 2.0, 3 to 3.0)
    }
}
