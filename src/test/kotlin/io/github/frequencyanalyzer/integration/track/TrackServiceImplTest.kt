package io.github.frequencyanalyzer.integration.track

import io.github.frequencyanalyzer.TestFileUtils
import io.github.frequencyanalyzer.integration.IntegrationTest
import io.github.frequencyanalyzer.track.error.TrackNotFoundException
import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.track.service.TrackService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class TrackServiceImplTest(
    @Autowired private val trackRepository: TrackRepository,
    @Autowired private val trackDataRepository: TrackDataRepository,
    @Autowired private val trackService: TrackService
) : IntegrationTest {

    @BeforeEach
    fun clearDb() {
        trackDataRepository.deleteAll().block()

        trackRepository.deleteAll().block()
    }

    @Test
    fun shouldSaveNewTrack() {
        val file = TestFileUtils.createUpload()

        StepVerifier
            .create(trackService.create(file))
            .expectNextMatches { it.name == file.name && it.id != null }
            .verifyComplete()

        StepVerifier
            .create(trackRepository.findAll())
            .expectNextMatches { it.name == file.name && it.id != null }
            .verifyComplete()

        StepVerifier
            .create(trackDataRepository.findAll())
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun shouldNotSaveFileWithoutData() {
        val file = TestFileUtils.createEmptyUpload()

        StepVerifier
            .create(trackService.create(file))
            .expectError()

        StepVerifier
            .create(trackRepository.findAll())
            .verifyComplete()

        StepVerifier
            .create(trackDataRepository.findAll())
            .verifyComplete()
    }

    @Test
    fun shouldNotSaveFileWithoutName() {
        val file = TestFileUtils.createUpload().copy(name = "")

        StepVerifier
            .create(trackService.create(file))
            .expectError()

        StepVerifier
            .create(trackRepository.findAll())
            .verifyComplete()

        StepVerifier
            .create(trackDataRepository.findAll())
            .verifyComplete()
    }

    @Test
    fun shouldFindTrackForKnownId() {
        val track = persistTrack()

        StepVerifier
            .create(trackService.find(track.id!!))
            .expectNextMatches { it.name == track.name }
            .verifyComplete()
    }

    @Test
    fun shouldNotFindFileForUnknownId() {
        val track = persistTrack()

        StepVerifier
            .create(trackService.find(track.id!! + 1))
            .expectError(TrackNotFoundException::class.java)
    }

    private fun persistTrack(): Track {
        val file = TestFileUtils.createUpload()

        return trackService.create(file).block()!!
    }
}
