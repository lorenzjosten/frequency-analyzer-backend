package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame

class Mp3DecoderImpl(byteArray: ByteArray) : Mp3Decoder(byteArray) {

    override fun readFrames(n: UInt): List<DecodedFrame> {
        TODO("Not yet implemented")
    }

    override fun readFrame(): DecodedFrame? {
        TODO("Not yet implemented")
    }
}