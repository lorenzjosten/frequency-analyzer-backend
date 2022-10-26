package io.github.frequencyanalyzer.spectralanalysis.service

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.decoder.service.DecoderService
import io.github.frequencyanalyzer.spectralanalysis.SpectralAnalysis
import io.github.frequencyanalyzer.spectralanalysis.model.*
import io.github.frequencyanalyzer.track.error.TrackDataNotFoundException
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

private typealias FrameAtTime = Pair<DecodedFrame, Float>
private typealias SpectrumAtTime = Pair<PcmPowerSpectrum, Float>

@Service
class SpectralAnalysisServiceImpl(
        private val trackData: TrackDataRepository,
        private val decoderService: DecoderService,
        private val spectrumMapper: PcmPowerSpectrumMapper,
        @Value("\${analysis.frame-window-ms}") private val frameWindowMs: Long,
        @Value("\${analysis.emission-delay-ms}") private val emissionDelayMs: Long,
        @Value("\${analysis.prefetch.high-tide}") private val highTide: Int,
        @Value("\${analysis.prefetch.low-tide}") private val lowTide: Int,
) : SpectralAnalysisService {
    private val logger = getLogger(this::class.java)

    override fun analyze(trackId: Long): Flux<TimedPcmPowerSpectrum> {
        logger.info("Analyzing track with id $trackId")

        var windowCount = 1L

        return trackData.find(trackId)
                .switchIfEmpty(Mono.error(TrackDataNotFoundException(trackId)))
                .flatMapMany(decoderService::decode)
                .index { frameCount, frame -> FrameAtTime(frame, frameCount * frame.durationMs) }
                .bufferUntil { (_, frameTime) -> frameTime >= windowCount * frameWindowMs }
                .doOnNext { windowCount++ }
                .map { frames -> frames.map(::analyzeFrame).reduce(::accumulate).normalize() }
                .map { (spectrum, frameTime) -> spectrumMapper(trackId, spectrum, frameTime) }
                .limitRate(highTide, lowTide)
                .delayElements(Duration.ofMillis(emissionDelayMs))
                .onBackpressureBuffer()
    }

    private fun analyzeFrame(frameAtTime: FrameAtTime): SpectrumAtTime {
        val (frame, frameTime) = frameAtTime
        val powerSpectrum = SpectralAnalysis(frame).pcmPowerSpectrum()
        return SpectrumAtTime(powerSpectrum, frameTime)
    }

    private fun accumulate(first: SpectrumAtTime, second: SpectrumAtTime): SpectrumAtTime {
        val (firstSpectrum, _) = first
        val (secondSpectrum, frameTime) = second
        val accumulated = firstSpectrum.accumulate(secondSpectrum)

        return SpectrumAtTime(accumulated, frameTime)
    }

    private fun SpectrumAtTime.normalize(): SpectrumAtTime {
        val (spectrum, frameTime) = this
        val normalized = spectrum.normalize()

        return SpectrumAtTime(normalized, frameTime)
    }
}
