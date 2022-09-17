package io.github.frequencyanalyzer.unit.decoder

import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE
import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE_RESOURCE
import io.github.frequencyanalyzer.decoder.Mp3Decoder
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.decoder.model.FrameMapper
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.Decoder
import javazoom.jl.decoder.SampleBuffer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
        val tempFile = File
            .createTempFile("invalid", ".mp3")
            .apply { writeText(randomText()) }

        val decodedFrame = Mp3DecoderImpl(tempFile.readBytes())
            .use(Mp3Decoder::readFrame)

        assertNull(decodedFrame)

        tempFile.delete()
    }

    @Test
    fun shouldDecodeFrame() {
        Mp3DecoderImpl(TEST_FILE.data).use {
            val decodedFrame = it.readFrame()

            assertNotNull(decodedFrame)
            assertEquals(testDecoder.readFrame()!!, decodedFrame)
        }
    }

    @Test
    fun shouldDecodeFrames() {
        Mp3DecoderImpl(TEST_FILE.data).use {
            val decodedFrames = it.readFrames(3)

            assertEquals(3, decodedFrames.size)

            decodedFrames
                .forEach { frame -> assertEquals(testDecoder.readFrame()!!, frame) }
        }
    }

    @Test
    fun shouldStopDecodingAtEndOfFile() {
        val mp3Decoder = Mp3DecoderImpl(TEST_FILE.data)

        while (testDecoder.readFrame() != null) {
            mp3Decoder.readFrame()
        }

        assertNull(mp3Decoder.readFrame())
    }

    private class TestDecoder : Decoder(), AutoCloseable {

        private val bitStream = Bitstream(TEST_FILE_RESOURCE.inputStream)

        fun readFrame(): DecodedFrame? {
            val header = bitStream.readFrame()

            return if (header != null) {
                val duration = header.ms_per_frame()
                val buffer = decodeFrame(header, bitStream) as SampleBuffer
                bitStream.closeFrame()
                buffer.let(FrameMapper(duration))
            } else {
                close()
                null
            }
        }

        override fun close() {
            bitStream.close()
        }
    }

    private fun randomText(): String = (0..500).map { randomChar().toString() }.reduce(String::plus)

    private fun randomChar(): Char = (Char.MIN_VALUE.rangeTo(Char.MAX_VALUE)).random()
}