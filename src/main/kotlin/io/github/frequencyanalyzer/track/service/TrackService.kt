package io.github.frequencyanalyzer.track.service

import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackData
import io.github.frequencyanalyzer.upload.model.File
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TrackService {

    fun create(file: File): Mono<Track>

    fun find(id: Long): Mono<Track>

    fun findAll(): Flux<Track>

    fun delete(id: Long): Mono<Void>

    fun data(id: Long): Mono<TrackData>
}