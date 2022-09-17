package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import javazoom.jl.decoder.Bitstream

abstract class Mp3Decoder(byteArray: ByteArray) : AutoCloseable {

    private val bitStream = Bitstream(byteArray.inputStream())

    abstract fun readFrames(n: UInt): List<DecodedFrame>

    abstract fun readFrame(): DecodedFrame?

    override fun close() = bitStream.close()
}