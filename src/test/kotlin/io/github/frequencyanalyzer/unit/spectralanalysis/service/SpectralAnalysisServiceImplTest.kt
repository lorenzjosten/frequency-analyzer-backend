package io.github.frequencyanalyzer.unit.spectralanalysis.service

import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE
import io.github.frequencyanalyzer.file.error.FileNotFoundException
import io.github.frequencyanalyzer.file.service.FileService
import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
@SpringBootTest
class SpectralAnalysisServiceImplTest(
    @Mock private val fileService: FileService,
    @InjectMocks @Autowired private val spectralAnalysisService: SpectralAnalysisService
) {

    @Test
    fun shouldCalculatePowerSpectrum() {
        val id = 1L

        whenever(fileService.findById(id)).thenReturn(Mono.just(TEST_FILE))

        StepVerifier
            .create(spectralAnalysisService.analyseFile(id))
            .expectNextMatches {
                it.time != 0f &&
                    it.powerSpectrum.isNotEmpty() &&
                    it.powerSpectrum.values.none { v -> v > 1.0 }
            }
    }

    @Test
    fun shouldThrowIfFileNotFound() {
        val id = 1L

        whenever(fileService.findById(id)).thenReturn(Mono.empty())

        StepVerifier
            .create(spectralAnalysisService.analyseFile(id))
            .expectError(FileNotFoundException::class)
    }
}
