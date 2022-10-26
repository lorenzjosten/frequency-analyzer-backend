package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.Decoder
import java.io.InputStream

abstract class Mp3Decoder(mp3: InputStream) : Decoder(), AutoCloseable {

    protected val bitStream: Bitstream = Bitstream(mp3)

    abstract fun readFrames(n: Int): List<DecodedFrame>

    abstract fun readFrame(): DecodedFrame?

    override fun close() = bitStream.close()
}
