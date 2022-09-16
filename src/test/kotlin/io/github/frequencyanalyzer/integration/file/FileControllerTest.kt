package io.github.frequencyanalyzer.integration.file

import io.github.frequencyanalyzer.file.model.FileMapper
import io.github.frequencyanalyzer.file.repository.FileRepository
import io.github.frequencyanalyzer.file.service.FileServiceImpl
import io.github.frequencyanalyzer.file.service.UploadServiceImpl
import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE
import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE_RESOURCE
import io.github.frequencyanalyzer.file.error.FileErrorDto
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
@MockBean(FileRepository::class)
@Import(FileServiceImpl::class, UploadServiceImpl::class, FileMapper::class)
class FileControllerTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val fileRepository: FileRepository
) {

    @Test
    fun shouldReturnNewFileDto() {
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
    fun shouldThrowIfNoMp3File() {
        fileRepository.save(any())
            .thenReturn(Mono.just(TEST_FILE))

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
    fun shouldReturnOldFileDto() {
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

    private fun fileBodyInserter(): BodyInserters.MultipartInserter {
        return BodyInserters.fromMultipartData(testFileBodyBuilder().build())
    }

    private fun testFileBodyBuilder(): MultipartBodyBuilder {
        val bodyBuilder = MultipartBodyBuilder()

        bodyBuilder.part("file", TEST_FILE_RESOURCE)

        return bodyBuilder
    }
}