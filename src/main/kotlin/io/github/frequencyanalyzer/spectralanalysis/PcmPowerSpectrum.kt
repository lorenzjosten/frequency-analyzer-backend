package io.github.frequencyanalyzer.spectralanalysis

class PcmPowerSpectrum(
    private val powerSpectrum: Map<Double, Double>
) : Map<Double, Double> by powerSpectrum {

    fun normalize(): PcmPowerSpectrum {
        val maxAmplitude = maxBy { it.value }.value
        val normalized = mapValues { it.value / maxAmplitude }

        return PcmPowerSpectrum(normalized)
    }

    fun accumulate(other: PcmPowerSpectrum): PcmPowerSpectrum {
        val accumulated = toMutableMap()

        other.forEach { o, m -> accumulated[o] = accumulated[o]?.plus(m) ?: m }

        return PcmPowerSpectrum(accumulated)
    }

    fun peakFrequency(): Double {
        return maxBy { it.value }.key
    }

    override fun toString(): String {
        return entries.joinToString("\n") { "${it.key} ${it.value}" }
    }
}
