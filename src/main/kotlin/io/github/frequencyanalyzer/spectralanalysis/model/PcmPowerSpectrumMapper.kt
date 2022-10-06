package io.github.frequencyanalyzer.spectralanalysis.model

class PcmPowerSpectrumMapper(time: Float) : (PcmPowerSpectrum) -> TimedPcmPowerSpectrum by {
    TimedPcmPowerSpectrum(time, it.normalize())
}
