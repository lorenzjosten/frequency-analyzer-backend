package io.github.frequencyanalyzer

import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackData
import io.github.frequencyanalyzer.upload.model.Upload
import io.r2dbc.spi.Blob
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import reactor.core.publisher.Mono
import java.nio.ByteBuffer

object TestFileUtils {
    val TEST_FILE_RESOURCE: Resource = ClassPathResource("440Hz-5sec.mp3")

    fun createTrack(): Track {
        val id = 1L
        val name = TEST_FILE_RESOURCE.file.name

        return Track(id, name)
    }

    fun createTrackData(): TrackData {
        val trackId = 1L
        val data = createBlob()

        return TrackData(trackId, data)
    }

    fun createUpload(): Upload {
        val name = TEST_FILE_RESOURCE.file.name
        val data = createBlob()

        return Upload(name, data)
    }

    fun createEmptyUpload(): Upload {
        val name = TEST_FILE_RESOURCE.file.name
        val buffer = ByteBuffer.allocate(0)
        val data = Blob.from(Mono.just(buffer))

        return Upload(name, data)
    }

    private fun createBlob(): Blob {
        val bytes = TEST_FILE_RESOURCE.file.readBytes()
        val buffered = ByteBuffer.wrap(bytes)

        return Blob.from(Mono.just(buffered))
    }
}
