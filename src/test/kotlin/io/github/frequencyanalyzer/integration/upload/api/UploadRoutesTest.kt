package io.github.frequencyanalyzer.integration.upload.api

import io.github.frequencyanalyzer.TestFileUtils
import io.github.frequencyanalyzer.TestFileUtils.TEST_FILE_RESOURCE
import io.github.frequencyanalyzer.track.error.TrackDataError
import io.github.frequencyanalyzer.track.error.TrackDataErrorHandler
import io.github.frequencyanalyzer.track.error.TrackError
import io.github.frequencyanalyzer.track.error.TrackErrorHandler
import io.github.frequencyanalyzer.track.model.TrackDataMapper
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import io.github.frequencyanalyzer.track.model.TrackMapper
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.track.service.TrackServiceImpl
import io.github.frequencyanalyzer.upload.api.UploadRequestHandler
import io.github.frequencyanalyzer.upload.api.UploadRoutes
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono

@WebFluxTest
@MockBean(TrackRepository::class, TrackDataRepository::class)
@Import(
    UploadRoutes::class,
    UploadRequestHandler::class,
    TrackServiceImpl::class,
    TrackMapper::class,
    TrackDataMapper::class,
    TrackErrorHandler::class,
    TrackDataErrorHandler::class
)
class UploadRoutesTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val trackRepository: TrackRepository,
    @Autowired private val trackDataRepository: TrackDataRepository
) {

    @Test
    fun shouldCreateNewTrack() {
        val track = TestFileUtils.createTrack()
        val data = TestFileUtils.createTrackData()

        whenever(trackRepository.save(any()))
            .thenReturn(Mono.just(track))
        whenever(trackDataRepository.save(any()))
            .thenReturn(Mono.just(data))

        val response = client
            .post()
            .uri("/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(fileBodyInserter())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        response.expectStatus().is2xxSuccessful
        response.expectBody().jsonPath("$.name").isEqualTo(track.name)
        response.expectBody().jsonPath("$.id").isEqualTo(track.id!!)
    }

    @Test
    fun shouldThrowIfTrackNotSaved() {
        val payload = fileBodyInserter()

        whenever(trackRepository.save(any()))
            .thenReturn(Mono.error(IllegalArgumentException("Cannot save track.")))

        val response = client
            .post()
            .uri("/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(payload)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        response.expectStatus().is4xxClientError
        response.expectBody(TrackError::class.java)
    }

    @Test
    fun shouldThrowIfTrackDataNotSaved() {
        val track = TestFileUtils.createTrack()
        val payload = fileBodyInserter()

        whenever(trackRepository.save(any()))
            .thenReturn(Mono.just(track))
        whenever(trackDataRepository.save(any()))
            .thenReturn(Mono.error(IllegalArgumentException("Cannot save track data.")))

        val response = client
            .post()
            .uri("/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(payload)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        response.expectStatus().is4xxClientError
        response.expectBody(TrackDataError::class.java)
    }

    private fun fileBodyInserter(): BodyInserters.MultipartInserter {
        return BodyInserters.fromMultipartData(fileBodyBuilder().build())
    }

    private fun fileBodyBuilder(): MultipartBodyBuilder {
        val bodyBuilder = MultipartBodyBuilder()

        bodyBuilder.part("file", TEST_FILE_RESOURCE)

        return bodyBuilder
    }
}
