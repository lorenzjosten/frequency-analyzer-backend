package io.github.frequencyanalyzer.spectralanalysis.model

import io.github.frequencyanalyzer.track.api.SseWrapable

data class TimedPcmPowerSpectrum(
    val trackId: Long,
    val time: Float,
    val frequencies: Collection<Number>,
    val amplitudes: Collection<Number>,
    val peakAt: Number
) : SseWrapable("spectrum")
