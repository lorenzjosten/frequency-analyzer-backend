package io.github.frequencyanalyzer.spectralanalysis.model

import org.springframework.stereotype.Component

@Component
class PcmPowerSpectrumMapper : (PcmPowerSpectrum, Float) -> TimedPcmPowerSpectrum by { spectrum, time ->
    TimedPcmPowerSpectrum(time, spectrum.normalize())
}
