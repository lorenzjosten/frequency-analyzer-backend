package io.github.frequencyanalyzer.spectralanalysis.model

/**
 * Represents the power-spectrum of an audio signal as a map and offers methods for manipulation.
 * Keys represent frequencies of the spectrum.
 * Values represent their corresponding amplitude.
 * @author Lorenz Josten
 * @param powerSpectrum frequencies and the corresponding amplitudes of this PcmPowerSpectrum
 */
class PcmPowerSpectrum(
    private val powerSpectrum: Map<Double, Double>,
) : Map<Double, Double> by powerSpectrum {

    /**
     * Calculates a normalized PcmPowerSpectrum from the values of this PcmPowerSpectrum.
     * @return A new PcmPowerSpectrum consisting of normalized values.
     */
    fun normalize(): PcmPowerSpectrum {
        val min: Double = minByOrNull { it.value }?.value ?: 0.0
        val max: Double = maxByOrNull { it.value }?.value ?: 0.0
        val normalized = mapValues { it.value.normalize(min, max) }

        return PcmPowerSpectrum(normalized)
    }

    /**
     * Accumulates the values of this PcmPowerSpectrum and another PcmPowerSpectrum.
     * @param other The PcmPowerSpectrum, which values will be accumulated with the values of this PcmPowerSpectrum
     * @return A new PcmPowerSpectrum consisting of accumulated values.
     */
    fun accumulate(other: PcmPowerSpectrum): PcmPowerSpectrum {
        val accumulated = toMutableMap()

        other.forEach { o, m -> accumulated[o] = accumulated[o]?.plus(m) ?: m }

        return PcmPowerSpectrum(accumulated)
    }

    /**
     * Calculates the frequency of the peak amplitude of this PcmPowerSpectrum.
     * @return The frequency which belong to the peak amplitude.
     */
    fun peakFrequency(): Double {
        return maxByOrNull { it.value }?.key ?: 0.0
    }

    private fun Double.normalize(min: Double, max: Double): Double {
        return if (max - min != 0.0) (this - min).div(max - min) else 0.0
    }
}
