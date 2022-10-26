package io.github.frequencyanalyzer.decoder.extension

import javazoom.jl.decoder.SampleBuffer

fun SampleBuffer.adjustedBuffer() = ShortArray(bufferLength / channelCount) { buffer[it * channelCount] }
