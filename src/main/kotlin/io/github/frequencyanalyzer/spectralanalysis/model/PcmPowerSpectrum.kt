package io.github.frequencyanalyzer.spectralanalysis.model

import kotlin.math.log10
import kotlin.math.roundToInt

private typealias Order = Int
private typealias Magnitude = Double
private typealias FourierCoefficients = Map<Order, Magnitude>

class PcmPowerSpectrum(
    private val fourierCoefficients: FourierCoefficients = emptyMap()
) : Map<Order, Magnitude> by fourierCoefficients {

    companion object {
        const val DECIBEL_FACTOR = 20
    }

    fun accumulate(other: PcmPowerSpectrum): PcmPowerSpectrum {
        val accumulated = toMutableMap()

        other.map { (o, m) -> accumulated[o] = accumulated[o]?.plus(m) ?: m }

        return PcmPowerSpectrum(accumulated)
    }

    fun scaleCoefficients(sampleRate: Int, sampleSize: Int): PcmPowerSpectrum {
        val scalingFactor = sampleRate.toDouble() / sampleSize
        val scale: (Order) -> Order = { order: Order -> (order * scalingFactor).roundToInt() }
        val byScaledOrder = entries.groupBy { (order, _) -> scale(order) }
        val accumulated = byScaledOrder.mapValues { (_, toAccumulate) -> toAccumulate.sumOf { it.value } }

        return PcmPowerSpectrum(accumulated)
    }

    fun logScaleMagnitude(): PcmPowerSpectrum {
        val scaled = mapValues { DECIBEL_FACTOR * log10(it.value) }

        return PcmPowerSpectrum(scaled)
    }

    override fun toString(): String {
        return entries.toString()
    }
}
