package io.github.frequencyanalyzer.track.service

import io.github.frequencyanalyzer.track.model.Medium
import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.track.model.Track
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TrackService {
    fun findById(id: Long): Mono<Track>

    fun findAll(): Flux<Track>

    fun deleteById(id: Long): Mono<Void>

    fun medium(id: Long): Mono<Medium>

    fun powerSpectrum(id: Long): Flux<TimedPcmPowerSpectrum>
}