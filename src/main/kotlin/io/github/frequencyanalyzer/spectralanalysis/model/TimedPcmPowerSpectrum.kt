package io.github.frequencyanalyzer.spectralanalysis.model

data class TimedPcmPowerSpectrum(
        val trackId: Long,
        val time: Float,
        val frequencies: Collection<Number>,
        val amplitudes: Collection<Number>,
        val peakAt: Number
)
