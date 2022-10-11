package io.github.frequencyanalyzer.upload.facade

import io.github.frequencyanalyzer.track.model.Medium
import io.github.frequencyanalyzer.track.model.MediumRepository
import io.github.frequencyanalyzer.track.event.TrackCreatedEvent
import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.upload.model.File
import io.github.frequencyanalyzer.upload.service.UploadService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class UploadFacadeImpl(
    private val publisher: ApplicationEventPublisher,
    private val uploadService: UploadService,
    private val media: MediumRepository,
    private val tracks: TrackRepository
) : UploadFacade {

    @Transactional
    private fun persistData(file: File): Mono<Track> {
        return createTrack(file).flatMap { createMedium(it, file).then(it.toMono()) }
    }

    fun createTrack(file: File): Mono<Track> {
        val track = Track(name = file.name)
        return tracks.save(track)
    }

    fun createMedium(track: Track, file: File): Mono<Medium> {
        val medium = Medium(trackId = track.id!!, data = file.data)
        return media.save(medium)
    }

    @Transactional
    override fun fileUpload(filePart: FilePart): Mono<Track> {
        return uploadService.retrieveFile(filePart)
            .flatMap(::persistData)
            .doOnNext(::emitTrackCreated)
    }

    private fun emitTrackCreated(track: Track) {
        publisher.publishEvent(TrackCreatedEvent(this, track))
    }
}
