package io.github.frequencyanalyzer.unit.file

import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE
import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE_PART
import io.github.frequencyanalyzer.upload.service.UploadService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class UploadServiceImplTest(@Autowired private val uploadService: UploadService) {

    @Test
    fun shouldRetrieveFile() {
        StepVerifier
            .create(uploadService.retrieveFile(TEST_FILE_PART))
            .expectNextMatches {
                it.name == TEST_FILE.name &&
                    it.data.contentEquals(TEST_FILE.data)
            }
            .verifyComplete()
    }
}
