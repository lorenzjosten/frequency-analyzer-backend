package io.github.frequencyanalyzer.unit.decoder

import io.github.frequencyanalyzer.FileTestUtils.Companion.TEST_FILE_RESOURCE
import io.github.frequencyanalyzer.decoder.Mp3Decoder
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import org.junit.jupiter.api.BeforeEach

class DecoderTest {

    lateinit var mp3Decoder: Mp3Decoder

    @BeforeEach
    fun initDecoder() {
        mp3Decoder = Mp3DecoderImpl(TEST_FILE_RESOURCE.inputStream)
    }

}