package io.github.frequencyanalyzer.spectralanalysis.model

import org.springframework.stereotype.Component

@Component
class PcmPowerSpectrumMapper : (Long, PcmPowerSpectrum, Float) -> TimedPcmPowerSpectrum by { trackId, spectrum, time ->
    spectrum.run { TimedPcmPowerSpectrum(trackId, time, keys, values, peakFrequency()) }
}
