package io.github.frequencyanalyzer.unit.decoder

import io.github.frequencyanalyzer.TestFileUtils
import io.github.frequencyanalyzer.decoder.Mp3Decoder
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.Decoder
import javazoom.jl.decoder.SampleBuffer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class DecoderTest {
    private lateinit var testDecoder: TestDecoder

    @BeforeEach
    fun setUp() {
        testDecoder = TestDecoder()
    }

    @AfterEach
    fun tearDown() {
        testDecoder.close()
    }

    @Test
    fun shouldNotBeAbleToReadInvalidFile() {
        val tempFile = createTempFile()

        val decodedFrame = Mp3DecoderImpl(tempFile.inputStream()).use(Mp3Decoder::readFrame)

        assertNull(decodedFrame)

        tempFile.delete()
    }

    @Test
    fun shouldDecodeFrame() {
        val mp3 = TestFileUtils.TEST_FILE_RESOURCE.inputStream

        Mp3DecoderImpl(mp3).use {
            val decodedFrame = it.readFrame()

            assertNotNull(decodedFrame)
            assertEquals(testDecoder.readFrame()!!, decodedFrame)
        }
    }

    @Test
    fun shouldDecodeFrames() {
        val mp3 = TestFileUtils.TEST_FILE_RESOURCE.inputStream

        Mp3DecoderImpl(mp3).use {
            val decodedFrames = it.readFrames(3)

            assertEquals(3, decodedFrames.size)

            decodedFrames.forEach { frame -> assertEquals(testDecoder.readFrame()!!, frame) }
        }
    }

    @Test
    fun shouldStopDecodingAtEndOfFile() {
        val mp3 = TestFileUtils.TEST_FILE_RESOURCE.inputStream
        val mp3Decoder = Mp3DecoderImpl(mp3)

        while (testDecoder.readFrame() != null) {
            mp3Decoder.readFrame()
        }

        assertNull(mp3Decoder.readFrame())
    }

    private class TestDecoder : Decoder(), AutoCloseable {
        private val mp3 = TestFileUtils.TEST_FILE_RESOURCE.inputStream
        private val bitStream = Bitstream(mp3)

        fun readFrame(): DecodedFrame? {
            val header = bitStream.readFrame()

            return if (header != null) {
                val duration = header.ms_per_frame()
                val buffer = decodeFrame(header, bitStream) as SampleBuffer
                bitStream.closeFrame()
                DecodedFrame(buffer, duration)
            } else {
                close()
                null
            }
        }

        override fun close() {
            bitStream.close()
        }
    }

    private fun createTempFile() = File.createTempFile("invalid", ".mp3").apply { writeText(randomText()) }

    private fun randomText(): String = (0..500).map { randomChar().toString() }.reduce(String::plus)

    private fun randomChar(): Char = (Char.MIN_VALUE.rangeTo(Char.MAX_VALUE)).random()
}
