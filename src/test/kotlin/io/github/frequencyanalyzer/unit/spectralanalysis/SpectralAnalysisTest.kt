package io.github.frequencyanalyzer.unit.spectralanalysis

import io.github.frequencyanalyzer.TestFileUtils
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.spectralanalysis.SpectralAnalysis
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SpectralAnalysisTest {

    private val decodedFrames: List<DecodedFrame> = decodeTestFile()

    private val failThreshold: Double = 0.98

    private val expectedFreq: Double = 440.0

    private val toleranceFreq: Double = 40.0

    @Test
    fun frequencyPeaksOfAnalyzedFramesShouldBeAsExpected() {
        val peakFrequencies = decodedFrames
            .map { SpectralAnalysis(it) }
            .map { it.pcmPowerSpectrum() }
            .map { it.peakFrequency() }
        val acceptable = (expectedFreq - toleranceFreq / 2..expectedFreq + toleranceFreq / 2)
        val accepted = peakFrequencies.count { it in acceptable }

        assertTrue(failThreshold < accepted.toDouble() / peakFrequencies.size)
    }

    private fun decodeTestFile(): List<DecodedFrame> {
        val mp3 = TestFileUtils.TEST_FILE_RESOURCE.inputStream

        return Mp3DecoderImpl(mp3)
            .use { generateSequence { it.readFrame() }.toList() }
    }
}
