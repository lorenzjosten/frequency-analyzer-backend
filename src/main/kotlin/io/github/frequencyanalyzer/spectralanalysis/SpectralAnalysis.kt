package io.github.frequencyanalyzer.spectralanalysis

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import kotlin.math.log10
import kotlin.math.sqrt

class SpectralAnalysis(decodedFrame: DecodedFrame) {

    private val fourierSequence: DoubleArray = FourierTransform(decodedFrame).complexForward()

    private val frequencyPerSequenceIndex = decodedFrame.run { sampleFrequency.toDouble() / bufferSize }

    companion object {
        const val DECIBEL_FACTOR = 20
    }

    fun pcmPowerSpectrum(): PcmPowerSpectrum {
        val powerSpectrum = (0 until fourierSequence.size / 4).associate(::frequencyAmplitudeAtSequenceIndex)

        return PcmPowerSpectrum(powerSpectrum)
    }

    private fun frequencyAmplitudeAtSequenceIndex(fourierSequenceIndex: Int): Pair<Double, Double> {
        return frequency(fourierSequenceIndex) to amplitude(fourierSequenceIndex)
    }

    private fun frequency(fourierSequenceIndex: Int): Double {
        return fourierSequenceIndex * frequencyPerSequenceIndex
    }

    private fun amplitude(fourierSequenceIndex: Int): Double {
        return when (val magnitude = magnitude(fourierSequenceIndex)) {
            0.0 -> 0.0
            else -> DECIBEL_FACTOR * log10(magnitude)
        }
    }

    private fun magnitude(fourierSequenceIndex: Int): Double {
        val re = fourierSequence[2 * fourierSequenceIndex]
        val im = fourierSequence[2 * fourierSequenceIndex + 1]

        return sqrt(re * re + im * im)
    }
}
