package io.github.frequencyanalyzer.spectralanalysis.model

import kotlin.math.log10

private typealias Order = Double
private typealias Magnitude = Double
private typealias PowerSpectrum = Map<Order, Magnitude>

class PcmPowerSpectrum(
    private val powerSpectrum: PowerSpectrum = emptyMap()
) : Map<Order, Magnitude> by powerSpectrum {

    companion object {
        const val DECIBEL_FACTOR = 20
    }

    fun accumulate(other: PcmPowerSpectrum): PcmPowerSpectrum {
        val accumulated = toMutableMap()

        other.forEach { o, m -> accumulated[o] = accumulated[o]?.plus(m) ?: m }

        return PcmPowerSpectrum(accumulated)
    }

    fun scaleOrder(sampleRate: Int, sampleSize: Int): PcmPowerSpectrum {
        val scalingFactor = sampleRate.toDouble() / sampleSize
        val scale: (Order) -> Order = { order: Order -> order * scalingFactor }
        val byScaledOrder = entries.groupBy { (order, _) -> scale(order) }
        val accumulated = byScaledOrder.mapValues { (_, toAccumulate) -> toAccumulate.sumOf { it.value } }

        return PcmPowerSpectrum(accumulated)
    }

    fun scaleMagnitude(): PcmPowerSpectrum {
        val scaled = mapValues {
            when (it.value) {
                0.0 -> 0.0
                else -> DECIBEL_FACTOR * log10(it.value)
            }
        }

        return PcmPowerSpectrum(scaled)
    }

    override fun toString(): String {
        return entries.toString()
    }
}
