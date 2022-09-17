package io.github.frequencyanalyzer.spectralanalysis.model

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import kotlin.math.log10
import kotlin.math.sqrt

class SpectralAnalysis(private val decodedFrame: DecodedFrame) {

    private val fourierTransform: DoubleArray = FourierTransform(decodedFrame).complexForward()

    companion object {
        const val DECIBEL_FACTOR = 20
    }

    fun powerSpectrum(): PcmPowerSpectrum {
        val powerSpectrum = (0 until fourierTransform.size / 4)
            .associate {
                coefficientOrder(it) to coefficientMagnitude(it)
            }

        return PcmPowerSpectrum(powerSpectrum)
    }

    private fun coefficientOrder(order: Int): Double {
        val scalingFactor = decodedFrame.run { sampleFrequency.toDouble() / bufferSize }

        return order * scalingFactor
    }

    private fun coefficientMagnitude(order: Int): Double {
        val magnitude = magnitude(order)

        return scaleMagnitude(magnitude)
    }

    private fun magnitude(order: Int): Double {
        val re = fourierTransform[2 * order]
        val im = fourierTransform[2 * order + 1]

        return sqrt(re * re + im * im)
    }

    private fun scaleMagnitude(magnitude: Double): Double {
        return when (magnitude) {
            0.0 -> 0.0
            else -> DECIBEL_FACTOR * log10(magnitude)
        }
    }
}
