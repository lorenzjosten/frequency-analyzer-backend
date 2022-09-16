package io.github.frequencyanalyzer.integration.file

import io.github.frequencyanalyzer.file.model.File
import io.github.frequencyanalyzer.file.repository.FileRepository
import io.github.frequencyanalyzer.file.service.FileService
import io.github.frequencyanalyzer.integration.IntegrationTest
import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class FileServiceImplTest(
    @Autowired private val fileRepository: FileRepository,
    @Autowired private val fileService: FileService
) : IntegrationTest {

    @BeforeEach
    fun clearDb() {
        fileRepository
            .deleteAll()
            .block()
    }

    @Test
    fun shouldSaveNewFile() {
        StepVerifier
            .create(fileService.save(TEST_FILE))
            .expectNextMatches {
                it.name == TEST_FILE.name &&
                it.hasId() &&
                it.data.contentEquals(TEST_FILE.data)
            }
            .verifyComplete()

        StepVerifier
            .create(fileRepository.findAll())
            .expectNextMatches {
                it.name == TEST_FILE.name &&
                it.hasId() &&
                it.data.contentEquals(TEST_FILE.data)
            }
            .verifyComplete()
    }

    @Test
    fun shouldNotSaveFileWithoutData() {
        val file = TEST_FILE.copy(data = ByteArray(0))

        StepVerifier
            .create(fileService.save(file))
            .expectError()

        StepVerifier
            .create(fileRepository.findAll())
            .verifyComplete()
    }

    @Test
    fun shouldNotSaveFileWithoutName() {
        val file = TEST_FILE.copy(name = "")

        StepVerifier
            .create(fileService.save(file))
            .expectError()

        StepVerifier
            .create(fileRepository.findAll())
            .verifyComplete()
    }

    @Test
    fun shouldFindFileForKnownId() {
        val id = createTestFile().id!!

        StepVerifier
            .create(fileService.findById(id))
            .expectNextMatches {
                it.name == TEST_FILE.name &&
                it.hasId() &&
                it.data.contentEquals(TEST_FILE.data)
            }
            .verifyComplete()
    }

    @Test
    fun shouldNotFindFileForUnknownId() {
        val id = createTestFile().id!!

        StepVerifier
            .create(fileService.findById(id+1))
            .verifyComplete()
    }

    private fun createTestFile(): File {
        return fileRepository
            .save(TEST_FILE)
            .block()!!
    }
}