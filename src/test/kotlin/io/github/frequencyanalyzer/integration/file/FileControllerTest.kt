package io.github.frequencyanalyzer.integration.file

import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE
import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE_RESOURCE
import io.github.frequencyanalyzer.file.error.FileErrorDto
import io.github.frequencyanalyzer.file.repository.FileRepository
import io.github.frequencyanalyzer.file.service.FileServiceImpl
import io.github.frequencyanalyzer.file.service.UploadServiceImpl
import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisServiceImpl
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
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono

@WebFluxTest
@MockBean(FileRepository::class)
@Import(FileServiceImpl::class, UploadServiceImpl::class, SpectralAnalysisServiceImpl::class)
class FileControllerTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val fileRepository: FileRepository,
) {

    @Test
    fun shouldCreateNewFile() {
        val id = 1L

        whenever(fileRepository.save(any()))
            .thenReturn(Mono.just(TEST_FILE.copy(id = id)))

        client
            .post()
            .uri("/file")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(fileBodyInserter())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                { it.expectStatus().is2xxSuccessful },
                { it.expectBody().jsonPath("$.name").isEqualTo(TEST_FILE.name) },
                { it.expectBody().jsonPath("$.id").isEqualTo(id) }
            )
    }

    @Test
    fun shouldThrowIfFileErroneous() {
        whenever(fileRepository.save(any()))
            .thenReturn(Mono.error(IllegalArgumentException("corrupt file")))

        client
            .post()
            .uri("/file")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(fileBodyInserter())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                { it.expectBody(FileErrorDto::class.java) },
                { it.expectStatus().is4xxClientError }
            )
    }

    @Test
    fun shouldFindFile() {
        val id = 1L

        whenever(fileRepository.findById(id))
            .thenReturn(Mono.just(TEST_FILE.copy(id = id)))

        client
            .get()
            .uri("/file/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                { it.expectStatus().is2xxSuccessful },
                { it.expectBody().jsonPath("$.name").isEqualTo(TEST_FILE.name) },
                { it.expectBody().jsonPath("$.id").isEqualTo(id) }
            )
    }

    @Test
    fun shouldThrowIfFileNotFound() {
        val id = 1L

        whenever(fileRepository.findById(id))
            .thenReturn(Mono.empty())

        client
            .get()
            .uri("/file/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectAll(
                { it.expectBody(FileErrorDto::class.java) },
                { it.expectStatus().isNotFound }
            )
    }

    @Test
    fun shouldCalculatePowerSpectrum() {
        val id = 1L

        whenever(fileRepository.findById(id))
            .thenReturn(Mono.just(TEST_FILE.copy(id = id)))

        client
            .get()
            .uri("/file/$id/power-spectrum")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectAll(
                { it.expectBodyList<TimedPcmPowerSpectrum>() },
                { it.expectStatus().is2xxSuccessful }
            )
    }

    @Test
    fun shouldThrowIfCalculatePowerSpectrumForEmptyFile() {
        val id = 1L

        whenever(fileRepository.findById(id))
            .thenReturn(Mono.just(TEST_FILE.copy(id = id, data = ByteArray(0))))

        client
            .get()
            .uri("/file/$id/power-spectrum")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectAll(
                { it.expectBodyList<FileErrorDto>().contains(FileErrorDto("Cannot process file.")) },
                { it.expectStatus().is4xxClientError }
            )
    }

    private fun fileBodyInserter(): BodyInserters.MultipartInserter {
        return BodyInserters.fromMultipartData(testFileBodyBuilder().build())
    }

    private fun testFileBodyBuilder(): MultipartBodyBuilder {
        val bodyBuilder = MultipartBodyBuilder()

        bodyBuilder.part("file", TEST_FILE_RESOURCE)

        return bodyBuilder
    }
}
