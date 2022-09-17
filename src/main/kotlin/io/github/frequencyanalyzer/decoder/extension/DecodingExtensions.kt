package io.github.frequencyanalyzer.decoder.extension

import javazoom.jl.decoder.SampleBuffer

fun SampleBuffer.adjustedBuffer() = buffer.take(bufferLength).toShortArray()
