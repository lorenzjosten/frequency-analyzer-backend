package io.github.frequencyanalyzer.unit.spectralanalysis.service

import io.github.frequencyanalyzer.TestFileUtils
import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisService
import io.github.frequencyanalyzer.track.error.TrackDataError
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
@SpringBootTest
class SpectralAnalysisServiceImplTest(
        @Mock private val trackDataRepository: TrackDataRepository,
        @InjectMocks @Autowired private val spectralAnalysisService: SpectralAnalysisService
) {

    @Test
    fun shouldCalculatePowerSpectrum() {
        val data = TestFileUtils.createTrackData()

        whenever(trackDataRepository.find(data.trackId)).thenReturn(Mono.just(data))

        StepVerifier
                .create(spectralAnalysisService.analyze(data.trackId))
                .expectNextMatches {
                    it.time != 0f &&
                            it.amplitudes.isNotEmpty() &&
                            it.frequencies.isNotEmpty() &&
                            it.amplitudes.size == it.frequencies.size
                    it.amplitudes.none { v -> v.toDouble() > 1.0 }
                }
    }

    @Test
    fun shouldThrowIfFileNotFound() {
        val id = 1L

        whenever(trackDataRepository.find(id)).thenReturn(Mono.empty())

        StepVerifier
                .create(spectralAnalysisService.analyze(id))
                .expectError(TrackDataError::class.java)
    }
}
