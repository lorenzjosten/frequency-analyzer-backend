package io.github.frequencyanalyzer.track.model

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TrackDataRepository {

    fun save(data: TrackData): Mono<TrackData>

    fun find(trackId: Long): Mono<TrackData>

    fun findAll(): Flux<TrackData>

    fun delete(trackId: Long): Mono<Void>

    fun deleteAll(): Mono<Void>

    fun exists(trackId: Long): Mono<Boolean>
}