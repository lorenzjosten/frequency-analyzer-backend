package io.github.frequencyanalyzer.track.model

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface MediumRepository : ReactiveCrudRepository<Medium, Long> {

    @Query("SELECT * FROM medium where track_id = :trackId")
    fun findByTrackId(trackId: Long): Mono<Medium>

    @Query("DELETE FROM medium where track_id = :trackId")
    fun deleteByTrackId(trackId: Long): Mono<Void>
}