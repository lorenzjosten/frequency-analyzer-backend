package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.Decoder

abstract class Mp3Decoder(byteArray: ByteArray) : Decoder(), AutoCloseable {

    protected val bitStream = Bitstream(byteArray.inputStream())

    abstract fun readFrames(n: Int): List<DecodedFrame>

    abstract fun readFrame(): DecodedFrame?

    override fun close() = bitStream.close()
}
