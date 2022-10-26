package io.github.frequencyanalyzer

import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackData
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.upload.model.File
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
        readFiles().flatMap { saveFileData(it) }.subscribe()
    }

    @Transactional
    fun saveFileData(file: File): Mono<TrackData> {
        return createTrack(file).flatMap { createTrackData(it, file) }
    }

    fun createTrack(file: File): Mono<Track> {
        val track = Track(name = file.name)
        return tracks.save(track)
    }

    fun createTrackData(track: Track, file: File): Mono<TrackData> {
        val data = TrackData(track.id!!, file.data)
        return trackData.save(data)
    }

    private fun readFiles(): Flux<File> {
        return resources()
                .map {
                    val file = it.file
                    val data = ByteBuffer.wrap(file.readBytes())
                    val blob = Blob.from(Mono.just(data))
                    File(name = it.filename!!, data = blob)
                }
                .toFlux()
    }

    private fun resources(): Flux<Resource> {
        return resourceResolver.getResources("classpath:mp3/*.mp3").toFlux()
    }
}
