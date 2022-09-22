package io.github.frequencyanalyzer.spectralanalysis

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import kotlin.math.log10
import kotlin.math.sqrt

class SpectralAnalysis(private val decodedFrame: DecodedFrame) {

    private val fourierSequence: DoubleArray = FourierTransform(decodedFrame).complexForward()

    companion object {
        const val DECIBEL_FACTOR = 20
    }

    fun powerSpectrum(): PcmPowerSpectrum {
        val fourierCoefficients = (0 until fourierSequence.size / 4).associate(::fourierCoefficient)

        return PcmPowerSpectrum(fourierCoefficients)
    }

    private fun fourierCoefficient(fourierSequenceIndex: Int): Pair<Double, Double> {
        val order = coefficientOrder(fourierSequenceIndex)
        val magnitude = coefficientMagnitude(fourierSequenceIndex)

        return order to magnitude
    }

    private fun coefficientOrder(fourierSequenceIndex: Int): Double {
        val scalingFactor = decodedFrame.run { sampleFrequency.toDouble() / bufferSize }

        return fourierSequenceIndex * scalingFactor
    }

    private fun coefficientMagnitude(fourierSequenceIndex: Int): Double {
        val magnitude = magnitude(fourierSequenceIndex)

        return scaleMagnitude(magnitude)
    }

    private fun magnitude(fourierSequenceIndex: Int): Double {
        val re = fourierSequence[2 * fourierSequenceIndex]
        val im = fourierSequence[2 * fourierSequenceIndex + 1]

        return sqrt(re * re + im * im)
    }

    private fun scaleMagnitude(magnitude: Double): Double {
        return when (magnitude) {
            0.0 -> 0.0
            else -> DECIBEL_FACTOR * log10(magnitude)
        }
    }
}
