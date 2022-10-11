package io.github.frequencyanalyzer.spectralanalysis.service

import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import reactor.core.publisher.Flux

interface SpectralAnalysisService {

    fun analyseTrack(id: Long): Flux<TimedPcmPowerSpectrum>
}
