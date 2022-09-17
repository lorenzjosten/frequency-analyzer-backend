package io.github.frequencyanalyzer.spectralanalysis.model

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D

class FourierTransform(private val frame: DecodedFrame) {

    fun complexForward(): DoubleArray {
        val dftData = prepareDftData()

        DoubleFFT_1D(frame.bufferSize).complexForward(dftData)

        return dftData
    }

    private fun prepareDftData(): DoubleArray {
        return DoubleArray(frame.bufferSize * 2) {
            when {
                it % 2 == 0 -> frame.buffer[it / 2].toDouble()
                else -> 0.0
            }
        }
    }
}
