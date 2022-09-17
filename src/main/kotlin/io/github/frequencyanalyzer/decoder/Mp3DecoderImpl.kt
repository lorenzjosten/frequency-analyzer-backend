package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.decoder.model.FrameMapper
import javazoom.jl.decoder.Header
import javazoom.jl.decoder.SampleBuffer

class Mp3DecoderImpl(byteArray: ByteArray) : Mp3Decoder(byteArray) {

    override fun readFrames(n: Int): List<DecodedFrame> {
        return if (n < 0) {
            emptyList()
        } else {
            generateSequence { readFrame() }.take(n).toList()
        }
    }

    override fun readFrame(): DecodedFrame? {
        return bitStream.readFrame()?.let(::decodeFrame)
    }

    private fun decodeFrame(currentHeader: Header): DecodedFrame {
        val duration = currentHeader.ms_per_frame()
        val decodedBuffer = decodeFrame(currentHeader, bitStream) as SampleBuffer

        bitStream.closeFrame()

        return decodedBuffer.let(FrameMapper(duration))
    }
}