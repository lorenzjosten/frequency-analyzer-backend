package io.github.frequencyanalyzer.spectralanalysis.fourier

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
import io.github.frequencyanalyzer.decoder.model.DecodedFrame

/**
 * Wrapper class for the jtransforms library.
 * Provides methods for a 1D fourier transformation of the audio signal of a decoded mp3-frame.
 * @author Lorenz Josten
 * @param frame the decoded mp3-frame
 */
class FourierTransform(private val frame: DecodedFrame) {

    /**
     * Applies the jtransform 1D fourier transformation to the audio signal of the mp3-frame.
     * @return The 1D fourier transformed data of the audio signal
     */
    fun complexForward(): DoubleArray {
        val dftData = prepareDftData()

        DoubleFFT_1D(frame.bufferSize).complexForward(dftData)

        return dftData
    }

    /**
     * Prepares the input array for the jtransforms fourier transformation.
     * The library expects the input array to consist of real and imaginary values.
     * Every (2*n)-th value is the real part of an audio signal data entry.
     * Every (2*n+1)-th value is the imaginary part of an audio signal data entry.
     * Since the signal consist only of real values, every second value of the input array is zero.
     */
    private fun prepareDftData(): DoubleArray {
        return DoubleArray(frame.bufferSize * 2) {
            when {
                it % 2 == 0 -> frame.buffer[it / 2].toDouble()
                else -> 0.0
            }
        }
    }
}
