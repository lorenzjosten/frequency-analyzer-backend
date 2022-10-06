package io.github.frequencyanalyzer.spectralanalysis.service

import io.github.frequencyanalyzer.decoder.Mp3Decoder
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.file.error.FileNotFoundException
import io.github.frequencyanalyzer.file.model.File
import io.github.frequencyanalyzer.file.service.FileService
import io.github.frequencyanalyzer.spectralanalysis.model.PcmPowerSpectrumMapper
import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.spectralanalysis.util.SpectralAnalysis
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SpectralAnalysisServiceImpl(private val fileService: FileService) : SpectralAnalysisService {

    override fun analyseFile(id: Long): Flux<TimedPcmPowerSpectrum> {
        return fileService
            .findById(id)
            .switchIfEmpty(Mono.error(FileNotFoundException(id)))
            .flatMapMany(::decodeFile)
            .index { i, frame ->
                val spectrum = SpectralAnalysis(frame).pcmPowerSpectrum()
                val time = i * frame.durationMs

                spectrum.let(PcmPowerSpectrumMapper(time))
            }
    }

    private fun decodeFile(file: File): Flux<DecodedFrame> {
        return Flux.using(
            { Mp3DecoderImpl(file.data) },
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
