package io.github.frequencyanalyzer.spectralanalysis.model

class PcmPowerSpectrum(
        private val powerSpectrum: Map<Double, Double>,
) : Map<Double, Double> by powerSpectrum {

    fun normalize(): PcmPowerSpectrum {
        val min: Double = minByOrNull { it.value }?.value ?: 0.0
        val max: Double = maxByOrNull { it.value }?.value ?: 0.0
        val normalized = mapValues { it.value.normalize(min, max) }

        return PcmPowerSpectrum(normalized)
    }

    fun accumulate(other: PcmPowerSpectrum): PcmPowerSpectrum {
        val accumulated = toMutableMap()

        other.forEach { o, m -> accumulated[o] = accumulated[o]?.plus(m) ?: m }

        return PcmPowerSpectrum(accumulated)
    }

    fun peakFrequency(): Double {
        return maxByOrNull { it.value }?.key ?: 0.0
    }

    private fun Double.normalize(min: Double, max: Double): Double {
        return if (max - min != 0.0) (this - min).div(max - min) else 0.0
    }
}
