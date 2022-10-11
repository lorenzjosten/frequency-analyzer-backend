package io.github.frequencyanalyzer.track.requesthandler

import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.track.model.MediumDto
import io.github.frequencyanalyzer.track.model.MediumMapper
import io.github.frequencyanalyzer.track.model.TrackDto
import io.github.frequencyanalyzer.track.model.TrackMapper
import io.github.frequencyanalyzer.track.service.TrackService
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
    private val trackMapper: TrackMapper,
    private val mediumMapper: MediumMapper
) {

    fun findAll(request: ServerRequest): Mono<ServerResponse> {
        val tracks = trackService.findAll().map(trackMapper)

        return ok().sse().contentType(MediaType.TEXT_EVENT_STREAM).body(tracks, TrackDto::class.java)
    }

    fun find(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val track = trackService.findById(id).map(trackMapper)

        return ok().contentType(MediaType.APPLICATION_JSON).body(track, TrackDto::class.java)
    }

    fun delete(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()

        return ok().body(trackService.deleteById(id), Void::class.java)
    }

    fun medium(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val medium = trackService.medium(id).map(mediumMapper)

        return ok().contentType(MediaType.APPLICATION_JSON).body(medium, MediumDto::class.java)
    }

    fun powerSpectrum(request: ServerRequest): Mono<ServerResponse> {
        val id = request.id()
        val powerSpectra = trackService.powerSpectrum(id)

        return ok().sse().contentType(MediaType.TEXT_EVENT_STREAM).body(powerSpectra, TimedPcmPowerSpectrum::class.java)
    }

    private fun ServerRequest.id() = pathVariable("id").toLong()
}