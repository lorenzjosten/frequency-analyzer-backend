package io.github.frequencyanalyzer.integration.track.api

import io.github.frequencyanalyzer.TestFileUtils
import io.github.frequencyanalyzer.decoder.service.DecoderServiceImpl
import io.github.frequencyanalyzer.spectralanalysis.model.PcmPowerSpectrumMapper
import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisServiceImpl
import io.github.frequencyanalyzer.track.api.TrackRequestHandler
import io.github.frequencyanalyzer.track.api.TrackRoutes
import io.github.frequencyanalyzer.track.error.TrackError
import io.github.frequencyanalyzer.track.error.TrackErrorHandler
import io.github.frequencyanalyzer.track.model.TrackDataMapper
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import io.github.frequencyanalyzer.track.model.TrackMapper
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.track.service.TrackServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest
@MockBean(TrackRepository::class, TrackDataRepository::class)
@Import(TrackRoutes::class, TrackRequestHandler::class, TrackErrorHandler::class, TrackServiceImpl::class, SpectralAnalysisServiceImpl::class, DecoderServiceImpl::class, TrackMapper::class, TrackDataMapper::class, PcmPowerSpectrumMapper::class)
class TrackRoutesTest(@Autowired private val client: WebTestClient, @Autowired private val trackRepository: TrackRepository, @Autowired private val trackDataRepository: TrackDataRepository) {

    @Test
    fun shouldFindTrack() {
        val track = TestFileUtils.createTrack()

        whenever(trackRepository.findById(track.id!!)).thenReturn(Mono.just(track))

        val response = client.get().uri("/track/${track.id!!}").accept(MediaType.APPLICATION_JSON).exchange()

        response.expectStatus().is2xxSuccessful
        response.expectBody().jsonPath("$.name").isEqualTo(track.name)
        response.expectBody().jsonPath("$.id").isEqualTo(track.id!!)
    }

    @Test
    fun shouldThrowIfTrackNotFound() {
        val id = 1L

        whenever(trackRepository.findById(id)).thenReturn(Mono.empty())

        val response = client.get().uri("/track/$id").accept(MediaType.APPLICATION_JSON).exchange()

        response.expectStatus().isNotFound
        response.expectBody(TrackError::class.java)
    }

    @Test
    fun shouldReturnClosingEvent() {
        val data = TestFileUtils.createTrackData()

        whenever(trackDataRepository.find(data.trackId)).thenReturn(Mono.just(data))

        val response = client.get().uri("/track/${data.trackId}/analyze").accept(MediaType.TEXT_EVENT_STREAM).exchange()

        response.expectStatus().is2xxSuccessful

        val events = response.expectBodyList(ServerSentEvent::class.java).returnResult().responseBody.orEmpty()

        assertEquals("close", events.lastOrNull()?.event())
    }

    @Test
    fun shouldCalculatePowerSpectrum() {
        val data = TestFileUtils.createTrackData()

        whenever(trackDataRepository.find(data.trackId)).thenReturn(Mono.just(data))

        val response = client.get().uri("/track/${data.trackId}/analyze").accept(MediaType.TEXT_EVENT_STREAM).exchange()

        response.expectStatus().is2xxSuccessful

        val events = response.expectBodyList(ServerSentEvent::class.java).returnResult().responseBody.orEmpty()

        assertTrue(events.any { it.event() == "spectrum" })
        assertEquals(events.count() - 1, events.count { it.event() == "spectrum" })
    }
}
