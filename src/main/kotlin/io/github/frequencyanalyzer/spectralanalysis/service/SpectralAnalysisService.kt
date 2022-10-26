package io.github.frequencyanalyzer.spectralanalysis.service

import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import reactor.core.publisher.Flux

interface SpectralAnalysisService {

    fun analyze(trackId: Long): Flux<TimedPcmPowerSpectrum>
}
