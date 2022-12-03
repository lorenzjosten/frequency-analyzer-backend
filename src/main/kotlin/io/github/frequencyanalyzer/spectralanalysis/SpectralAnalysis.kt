package io.github.frequencyanalyzer.spectralanalysis

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.spectralanalysis.fourier.FourierTransform
import io.github.frequencyanalyzer.spectralanalysis.model.PcmPowerSpectrum
import kotlin.math.log10
import kotlin.math.sqrt

/** Provides methods to calculate a power-spectrum for a given mp3-frame.
 * @author Lorenz Josten
 * @param decodedFrame Decoded mp3-frame to calculate a power-spectrum for.
 */
class SpectralAnalysis(decodedFrame: DecodedFrame) {

    /** fourierSequence is the fourier-transformed audio signal of the decoded mp3-frame.
     * Indexes represent the order of the fourier coefficients.
     * Values represent the magnitude of the fourier coefficients.
     * Real parts of the magnitude of the n-th fourier coefficient are stored at every (2*n)-th index.
     * Imaginary parts of the magnitude of the n-th fourier coefficient are stored at every (2*n+1)-th index.
     */
    private val fourierSequence: DoubleArray = FourierTransform(decodedFrame).complexForward()

    /** frequencyPerSequenceIndex is the scaling factor for the indices of the fourierSequence.
     * It is used to transform the fourier coefficient order into frequency values.
     * The digital audio signal is discrete in time, so is the frequency domain of the fourier transformed signal.
     * The sampleFrequency of the audio signal relates to the frequency resolution of the data.
     */
    private val frequencyPerSequenceIndex = decodedFrame.run { sampleFrequency.toDouble() / bufferSize }

    companion object {
        const val DECIBEL_FACTOR = 20
    }

    /**
     * Calculates the power-spectrum from the fourier transformed audio signal stored in fourierSequence.
     * Every entry of the power-spectrum is calculated from the real and imaginary part of a fourierCoefficient.
     * Only half of the values are of interest (see Nyquist-Shannon sampling theorem).
     * Thus, the resulting power spectrum is a quarter in size of fourierSequence.
     */
    fun pcmPowerSpectrum(): PcmPowerSpectrum {
        val powerSpectrum = (0 until fourierSequence.size / 4).associate(::frequencyAmplitudeAtSequenceIndex)

        return PcmPowerSpectrum(powerSpectrum)
    }

    /**
     * Associates a frequency of the power spectrum with its respective magnitude.
     * @param fourierSequenceIndex The order of the fourier coefficient
     */
    private fun frequencyAmplitudeAtSequenceIndex(fourierSequenceIndex: Int): Pair<Double, Double> {
        return frequency(fourierSequenceIndex) to amplitude(fourierSequenceIndex)
    }

    /**
     * Calculates the frequency associated to a fourier coefficient
     * @param fourierSequenceIndex The order of the fourier coefficient
     */
    private fun frequency(fourierSequenceIndex: Int): Double {
        return fourierSequenceIndex * frequencyPerSequenceIndex
    }

    /**
     * Calculates the amplitude of a fourier coefficient.
     * @param fourierSequenceIndex The order of the fourier coefficient
     */
    private fun amplitude(fourierSequenceIndex: Int): Double {
        return when (val magnitude = magnitude(fourierSequenceIndex)) {
            0.0 -> 0.0
            else -> DECIBEL_FACTOR * log10(magnitude)
        }
    }

    /**
     * Calculates the magnitude of a fourier coefficient.
     * @param fourierSequenceIndex The order of the fourier coefficient
     */
    private fun magnitude(fourierSequenceIndex: Int): Double {
        val re = fourierSequence[2 * fourierSequenceIndex]
        val im = fourierSequence[2 * fourierSequenceIndex + 1]

        return sqrt(re * re + im * im)
    }
}
