package io.github.frequencyanalyzer.track.api

import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisService
import io.github.frequencyanalyzer.track.api.SseWrapable.ClosingEvent
import io.github.frequencyanalyzer.track.model.TrackDataMapper
import io.github.frequencyanalyzer.track.model.TrackDto
import io.github.frequencyanalyzer.track.model.TrackMapper
import io.github.frequencyanalyzer.track.service.TrackService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@Component
class TrackRequestHandler(
    private val trackService: TrackService,
    private val spectralAnalysisService: SpectralAnalysisService,
    private val toTrackDto: TrackMapper,
    private val toTrackDataDto: TrackDataMapper
) {
    fun subscribe(request: ServerRequest): Mono<ServerResponse> {
        val toSse = SseWrapper()
        val tracks = trackService.findAll().map(toTrackDto).map(toSse)

        return ok().body(tracks, ServerSentEvent::class.java)
    }

    fun find(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val track = trackService.find(id).map(toTrackDto)

        return ok().contentType(MediaType.APPLICATION_JSON).body(track, TrackDto::class.java)
    }

    fun delete(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()

        return ok().body(trackService.delete(id), Void::class.java)
    }

    fun data(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val buffer = trackService.data(id).flatMap(toTrackDataDto)

        return ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(buffer, DataBuffer::class.java)
    }

    fun analyze(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val toSse = SseWrapper()
        val analysis = spectralAnalysisService.analyze(id).map(toSse)

        return ok().body(analysis.concatWithValues(toSse(ClosingEvent)), ServerSentEvent::class.java)
    }

    private fun ServerRequest.id() = pathVariable("id").toLong()
}