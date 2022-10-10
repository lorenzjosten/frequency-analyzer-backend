package io.github.frequencyanalyzer.track.service

import io.github.frequencyanalyzer.track.error.MediumNotFoundException
import io.github.frequencyanalyzer.track.error.MediumProcessingException
import io.github.frequencyanalyzer.track.model.Medium
import io.github.frequencyanalyzer.track.model.MediumRepository
import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisService
import io.github.frequencyanalyzer.track.error.TrackNotFoundException
import io.github.frequencyanalyzer.track.event.TrackCreatedEvent
import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import reactor.core.publisher.Sinks

@Service
class TrackServiceImpl(
    private val spectralAnalysisService: SpectralAnalysisService,
    private val tracks: TrackRepository,
    private val media: MediumRepository
) : TrackService {
    private val logger = getLogger(this::class.java)
    private val newTracks: Sinks.Many<Track> = Sinks.many().replay().all()

    override fun findById(id: Long): Mono<Track> {
        logger.info("Finding track with id $id.")

        return tracks.findById(id).switchIfEmpty(Mono.error(TrackNotFoundException(id)))
    }

    override fun findAll(): Flux<Track> {
        logger.info("Finding all tracks.")

        return Flux.concat(tracks.findAll(), newTracks.asFlux())
    }

    override fun deleteById(id: Long): Mono<Void> {
        logger.info("Deleting track with id $id.")

        return tracks.deleteById(id).then(media.deleteByTrackId(id))
    }

    override fun medium(id: Long): Mono<Medium> {
        logger.info("Loading medium for track with id $id.")

        return media.findByTrackId(id).switchIfEmpty(Mono.error(MediumNotFoundException(id)))
    }

    override fun powerSpectrum(id: Long): Flux<TimedPcmPowerSpectrum> {
        logger.info("Calculating power spectrum for file with id $id.")

        return spectralAnalysisService
            .analyseTrack(id)
            .switchIfEmpty(Mono.error(MediumProcessingException(id)))
    }

    @Async
    @EventListener(TrackCreatedEvent::class)
    private fun onTrackCreated(event: TrackCreatedEvent) {
        logger.info("Created track with id ${event.track.id}.")

        newTracks.tryEmitNext(event.track)
    }
}