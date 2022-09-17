package io.github.frequencyanalyzer.decoder.model

data class DecodedFrame(
    val sampleFrequency: Int,
    val bufferSize: Int,
    val buffer: ShortArray
)
