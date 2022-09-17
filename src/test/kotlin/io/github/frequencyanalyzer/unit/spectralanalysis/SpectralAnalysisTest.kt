package io.github.frequencyanalyzer.unit.spectralanalysis

import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE
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
            .map { it.powerSpectrum() }
            .map { it.peakFrequency() }
        val acceptable = (expectedFreq - toleranceFreq / 2 .. expectedFreq + toleranceFreq / 2)
        val accepted = peakFrequencies.count { it in acceptable }

        assertTrue(failThreshold < accepted.toDouble() / peakFrequencies.size)
    }

    private fun decodeTestFile(): List<DecodedFrame> {
        return Mp3DecoderImpl(TEST_FILE.data)
            .use { generateSequence { it.readFrame() }.toList() }
    }
}