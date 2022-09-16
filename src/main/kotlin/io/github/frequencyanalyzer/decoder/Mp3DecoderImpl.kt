package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import java.io.InputStream

class Mp3DecoderImpl(inputStream: InputStream) : Mp3Decoder(inputStream) {

    override fun canDecode(): Boolean {
        TODO("Not yet implemented")
    }

    override fun decodeFrames(n: Int): List<DecodedFrame> {
        TODO("Not yet implemented")
    }

    override fun decodeFrame(): DecodedFrame {
        TODO("Not yet implemented")
    }
}