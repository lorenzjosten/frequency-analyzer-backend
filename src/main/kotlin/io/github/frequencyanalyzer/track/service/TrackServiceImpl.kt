package io.github.frequencyanalyzer.track.service

import io.github.frequencyanalyzer.track.error.TrackDataNotFoundException
import io.github.frequencyanalyzer.track.error.TrackNotFoundException
import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackData
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.upload.model.File
import io.r2dbc.spi.Blob
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Service
class TrackServiceImpl(
        private val tracks: TrackRepository,
        private val trackData: TrackDataRepository
) : TrackService {
    private val logger = getLogger(this::class.java)
    private val newTracks: Sinks.Many<Track> = Sinks.many().multicast().directAllOrNothing()

    @Transactional
    override fun create(file: File): Mono<Track> {
        logger.info("Creating track \"${file.name}\".")

        return saveTrack(file.name)
                .flatMap { saveTrackData(it.id!!, file.data).then(Mono.just(it)) }
                .doOnNext(newTracks::tryEmitNext)
    }

    private fun saveTrack(name: String): Mono<Track> {
        val track = Track(name = name)
        return tracks.save(track)
    }

    private fun saveTrackData(trackId: Long, blob: Blob): Mono<TrackData> {
        val data = TrackData(trackId, blob)
        return trackData.save(data)
    }

    override fun find(id: Long): Mono<Track> {
        logger.info("Finding track with id $id.")

        return tracks.findById(id).switchIfEmpty(Mono.error(TrackNotFoundException(id)))
    }

    override fun findAll(): Flux<Track> {
        logger.info("Subscribed to tracks.")

        return Flux.concat(tracks.findAll(), newTracks.asFlux())
    }

    @Transactional
    override fun delete(id: Long): Mono<Void> {
        logger.info("Deleting track with id $id.")

        return trackData.delete(id).then(tracks.deleteById(id))
    }

    override fun data(id: Long): Mono<TrackData> {
        logger.info("Loading data for track with id $id.")

        return trackData.find(id).switchIfEmpty(Mono.error(TrackDataNotFoundException(id)))
    }
}