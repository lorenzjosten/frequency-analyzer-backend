package io.github.frequencyanalyzer.spectralanalysis.service

import io.github.frequencyanalyzer.decoder.Mp3Decoder
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.track.error.MediumNotFoundException
import io.github.frequencyanalyzer.track.model.Medium
import io.github.frequencyanalyzer.track.model.MediumRepository
import io.github.frequencyanalyzer.spectralanalysis.model.PcmPowerSpectrumMapper
import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.spectralanalysis.SpectralAnalysis
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SpectralAnalysisServiceImpl(
    private val media: MediumRepository,
    private val spectrumMapper: PcmPowerSpectrumMapper
    ) : SpectralAnalysisService {

    override fun analyseTrack(id: Long): Flux<TimedPcmPowerSpectrum> {
        return media
            .findById(id)
            .switchIfEmpty(Mono.error(MediumNotFoundException(id)))
            .flatMapMany(::decodeMedium)
            .index { i, frame ->
                val spectrum = SpectralAnalysis(frame).pcmPowerSpectrum()
                val time = i * frame.durationMs

                spectrumMapper(spectrum, time)
            }
    }

    private fun decodeMedium(medium: Medium): Flux<DecodedFrame> {
        return Flux.using(
            { Mp3DecoderImpl(medium.data) },
            { decodingFlux(it) },
            { it.close() }
        )
    }

    private fun decodingFlux(decoder: Mp3Decoder): Flux<DecodedFrame> {
        return Flux.generate {
            when (val frame = decoder.readFrame()) {
                null -> it.complete()
                else -> it.next(frame)
            }
        }
    }
}
