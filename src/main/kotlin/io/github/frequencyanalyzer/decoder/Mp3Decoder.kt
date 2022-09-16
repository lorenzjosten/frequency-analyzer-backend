package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import java.io.InputStream

abstract class Mp3Decoder(protected val inputStream: InputStream) {

    abstract fun canDecode(): Boolean

    abstract fun decodeFrames(n: Int): List<DecodedFrame>

    abstract fun decodeFrame(): DecodedFrame
}