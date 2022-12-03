package io.github.frequencyanalyzer.spectralanalysis.model

import io.github.frequencyanalyzer.track.api.SseWrapable

/**
 * Wraps the data of a PcmPowerSpectrum and the associated time of the audio track
 * @author Lorenz Josten
 * @param trackId ID of the track which this data belongs to.
 * @param time time in milliseconds of the audio track which the power-spectrum belongs to
 * @param frequencies frequencies of the power-spectrum
 * @param amplitudes amplitudes of the power-spectrum
 * @param peakAt frequency of the peak amplitude
 */
data class TimedPcmPowerSpectrum(
    val trackId: Long,
    val time: Float,
    val frequencies: Collection<Number>,
    val amplitudes: Collection<Number>,
    val peakAt: Number
) : SseWrapable("spectrum")
