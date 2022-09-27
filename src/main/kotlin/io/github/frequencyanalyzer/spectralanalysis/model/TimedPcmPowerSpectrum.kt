package io.github.frequencyanalyzer.spectralanalysis.model

data class TimedPcmPowerSpectrum(
    val time: Float,
    val powerSpectrum: PcmPowerSpectrum
)
