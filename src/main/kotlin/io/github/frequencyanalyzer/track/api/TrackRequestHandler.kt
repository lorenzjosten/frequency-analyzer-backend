package io.github.frequencyanalyzer.track.api

import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisService
import io.github.frequencyanalyzer.track.model.TrackDataMapper
import io.github.frequencyanalyzer.track.model.TrackDto
import io.github.frequencyanalyzer.track.model.TrackMapper
import io.github.frequencyanalyzer.track.service.TrackService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.sse
import reactor.core.publisher.Mono

@Component
class TrackRequestHandler(
        private val trackService: TrackService,
        private val spectralAnalysisService: SpectralAnalysisService,
        private val trackMapper: TrackMapper,
        private val trackDataMapper: TrackDataMapper
) {

    fun subscribe(request: ServerRequest): Mono<ServerResponse> {
        val tracks = trackService.findAll().map(trackMapper)

        return ok().sse().contentType(MediaType.TEXT_EVENT_STREAM).body(tracks, TrackDto::class.java)
    }

    fun find(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val track = trackService.find(id).map(trackMapper)

        return ok().contentType(MediaType.APPLICATION_JSON).body(track, TrackDto::class.java)
    }

    fun delete(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()

        return ok().body(trackService.delete(id), Void::class.java)
    }

    fun data(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val buffer = trackService.data(id).flatMap(trackDataMapper)

        return ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(buffer, DataBuffer::class.java)
    }

    fun analyze(serverRequest: ServerRequest): Mono<ServerResponse> {
        val id = serverRequest.id()
        val analysis = spectralAnalysisService.analyze(id)

        return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(analysis, TimedPcmPowerSpectrum::class.java)
    }

    private fun ServerRequest.id() = pathVariable("id").toLong()
}