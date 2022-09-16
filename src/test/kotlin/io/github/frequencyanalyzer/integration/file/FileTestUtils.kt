package io.github.frequencyanalyzer.integration.file

import io.github.frequencyanalyzer.file.model.File
import org.springframework.core.io.ClassPathResource

sealed class FileTestUtils {
    companion object {
        val TEST_FILE_RESOURCE = ClassPathResource("testfile")
        val TEST_FILE = File(
            name = TEST_FILE_RESOURCE.file.name,
            data = TEST_FILE_RESOURCE.file.readBytes()
        )
    }
}