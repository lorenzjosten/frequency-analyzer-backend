package io.github.frequencyanalyzer

import io.github.frequencyanalyzer.file.model.File
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Path

sealed class FileTestUtils {
    companion object {
        val TEST_FILE_RESOURCE: Resource = ClassPathResource("440Hz-5sec.mp3")
        val TEST_FILE_PART: FilePart = createFilePart()
        val TEST_FILE: File = createFile()

        private fun createFile(): File {
            return TEST_FILE_RESOURCE.file.run { File(name = name, data = readBytes()) }
        }

        private fun createFilePart(): FilePart {
            return object : FilePart {
                override fun name(): String = "file"
                override fun headers(): HttpHeaders = HttpHeaders()
                override fun content(): Flux<DataBuffer> = buffer()
                override fun filename(): String = TEST_FILE_RESOURCE.file.name
                override fun transferTo(dest: Path): Mono<Void> = Mono.empty()

                private fun buffer(): Flux<DataBuffer> {
                    return DataBufferUtils.read(TEST_FILE_RESOURCE, DefaultDataBufferFactory(), DEFAULT_BUFFER_SIZE)
                }
            }
        }
    }
}
