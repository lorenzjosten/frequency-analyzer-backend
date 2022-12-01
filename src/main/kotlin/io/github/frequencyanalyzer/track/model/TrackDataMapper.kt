package io.github.frequencyanalyzer.track.model

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class TrackDataMapper(
    bufferFactory: DataBufferFactory = DefaultDataBufferFactory()
) : (TrackData) -> Mono<DataBuffer> by {
    it.blob.stream().toMono().map(bufferFactory::wrap)
}