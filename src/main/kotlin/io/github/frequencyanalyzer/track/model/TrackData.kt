package io.github.frequencyanalyzer.track.model

import io.r2dbc.spi.Blob
import org.springframework.data.relational.core.mapping.Table
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Table("track_data")
data class TrackData(
    val trackId: Long,
    val blob: Blob
) {

    fun streamAsFlux() = blob.stream().toFlux()

    fun streamAsMono() = blob.stream().toMono()
}
