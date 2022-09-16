package io.github.frequencyanalyzer.decoder.model

data class DecodedFrame(
    val sampleFrequency: Int,
    val buffer: ShortArray,
    val bufferSize: Int
)
