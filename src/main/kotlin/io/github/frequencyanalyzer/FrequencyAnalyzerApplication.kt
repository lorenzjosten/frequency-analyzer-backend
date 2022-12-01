package io.github.frequencyanalyzer

import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackData
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.upload.model.Upload
import io.r2dbc.spi.Blob
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.nio.ByteBuffer

@SpringBootApplication
class FrequencyAnalyzerApplication

fun main(args: Array<String>) {
    runApplication<FrequencyAnalyzerApplication>(*args)
}

@Component
class SetupData(
    private val resourceResolver: ResourcePatternResolver,
    private val tracks: TrackRepository,
    private val trackData: TrackDataRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        readFiles().flatMap { saveUploadData(it) }.subscribe()
    }

    @Transactional
    fun saveUploadData(upload: Upload): Mono<TrackData> {
        return createTrack(upload).flatMap { createTrackData(it, upload) }
    }

    fun createTrack(upload: Upload): Mono<Track> {
        val track = Track(name = upload.name)
        return tracks.save(track)
    }

    fun createTrackData(track: Track, upload: Upload): Mono<TrackData> {
        val data = TrackData(track.id!!, upload.data)
        return trackData.save(data)
    }

    private fun readFiles(): Flux<Upload> {
        return resources()
            .map {
                val file = it.file
                val data = ByteBuffer.wrap(file.readBytes())
                val blob = Blob.from(Mono.just(data))
                Upload(name = it.filename!!, data = blob)
            }
            .toFlux()
    }

    private fun resources(): Flux<Resource> {
        return resourceResolver.getResources("classpath:mp3/*.mp3").toFlux()
    }
}
