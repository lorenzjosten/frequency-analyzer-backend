package io.github.frequencyanalyzer.spectralanalysis.model

private typealias CoefficientOrder = Int
private typealias CoefficientMagnitude = Double
private typealias FourierCoefficients = MutableMap<CoefficientOrder, CoefficientMagnitude>

class PcmPowerSpectrum(private val fourierCoefficients: FourierCoefficients) : Map<Int, Double> by fourierCoefficients {

    companion object {
        const val DECIBEL_FACTOR = 20
    }

    fun accumulate(other: PcmPowerSpectrum): PcmPowerSpectrum {
        TODO("Not yet implemented")
    }

    fun scaleCoefficients(sampleRate: Int, sampleSize: Int): PcmPowerSpectrum {
        TODO("Not yet implemented")
    }

    fun logScaleMagnitude(): PcmPowerSpectrum {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return fourierCoefficients.entries.toString()
    }
}
