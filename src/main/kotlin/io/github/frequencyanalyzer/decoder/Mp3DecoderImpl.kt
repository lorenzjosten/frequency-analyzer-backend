package io.github.frequencyanalyzer.decoder

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import javazoom.jl.decoder.Header
import javazoom.jl.decoder.SampleBuffer
import java.io.InputStream

/**
 * Wraps the javazoom library.
 * Provides methods for decoding a mp3 media file into frames.
 * @author Lorenz Josten
 * @param mp3 Inputstream of the mp3 media file
 */
class Mp3DecoderImpl(mp3: InputStream) : Mp3Decoder(mp3) {

    /**
     * Decodes n mp3-frames from the mp3 InputStream.
     * If more frames are requested than available, all available frames will be decoded and returned.
     * @param n Number of frames to decode
     * @return a List of at most n decoded mp3-frames
     */
    override fun readFrames(n: Int): List<DecodedFrame> {
        return if (n == 0) {
            emptyList()
        } else {
            generateSequence { readFrame() }.take(n).toList()
        }
    }

    /**
     * Reads a mp3-frame header from the mp3 InputStream.
     * @return null if no header can be read from input source, the next decoded mp3-frame from the input source
     */
    override fun readFrame(): DecodedFrame? {
        return when (val header = bitStream.readFrame()) {
            null -> null
            else -> decodeFrame(header)
        }
    }

    /**
     * Decodes a mp3-frame from the mp3 InputStream as defined by the mp3-frame header.
     * @param header Mp3-frame header
     * @return A decoded mp3-frame from the input source
     */
    private fun decodeFrame(header: Header): DecodedFrame {
        val duration = header.ms_per_frame()
        val decodedBuffer = decodeFrame(header, bitStream) as SampleBuffer

        bitStream.closeFrame()

        return DecodedFrame(decodedBuffer, duration)
    }
}
