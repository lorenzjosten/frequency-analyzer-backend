package io.github.frequencyanalyzer.decoder.model

import io.github.frequencyanalyzer.decoder.extension.adjustedBuffer
import javazoom.jl.decoder.SampleBuffer

/**
 * Wraps the properties of interest of a decoded mp3-frame
 * @author Lorenz Josten
 * @param sampleFrequency Sample frequency of the audio
 * @param bufferSize Actual written bytes of the mp3-frame
 * @param buffer Decoded mp3-frame data
 * @param durationMs Duration in millis of the mp3-frame
 */
data class DecodedFrame(
    val sampleFrequency: Int,
    val bufferSize: Int,
    val buffer: ShortArray,
    val durationMs: Float
) {
    constructor(buffer: SampleBuffer, durationMs: Float) : this(
        buffer.sampleFrequency,
        buffer.adjustedBuffer().size,
        buffer.adjustedBuffer(),
        durationMs
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecodedFrame

        if (sampleFrequency != other.sampleFrequency) return false
        if (bufferSize != other.bufferSize) return false
        if (!buffer.contentEquals(other.buffer)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sampleFrequency
        result = 31 * result + bufferSize
        result = 31 * result + buffer.contentHashCode()
        return result
    }
}
