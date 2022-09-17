package io.github.frequencyanalyzer.decoder.model

import io.github.frequencyanalyzer.decoder.extension.adjustedBuffer
import javazoom.jl.decoder.SampleBuffer

class FrameMapper(duration: Float) : (SampleBuffer) -> (DecodedFrame) by {
    DecodedFrame(
        it.sampleFrequency,
        it.bufferLength,
        it.adjustedBuffer(),
        duration
    )
}
