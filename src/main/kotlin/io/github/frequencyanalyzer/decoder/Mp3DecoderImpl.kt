package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import javazoom.jl.decoder.Header
import javazoom.jl.decoder.SampleBuffer
import java.io.InputStream

class Mp3DecoderImpl(mp3: InputStream) : Mp3Decoder(mp3) {

    override fun readFrames(n: Int): List<DecodedFrame> {
        return if (n == 0) {
            emptyList()
        } else {
            generateSequence { readFrame() }.take(n).toList()
        }
    }

    override fun readFrame(): DecodedFrame? {
        return when (val header = bitStream.readFrame()) {
            null -> null
            else -> decodeFrame(header)
        }
    }

    private fun decodeFrame(header: Header): DecodedFrame {
        val duration = header.ms_per_frame()
        val decodedBuffer = decodeFrame(header, bitStream) as SampleBuffer

        bitStream.closeFrame()

        return DecodedFrame(decodedBuffer, duration)
    }
}
