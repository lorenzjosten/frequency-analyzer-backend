package io.github.frequencyanalyzer

import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackData
import io.github.frequencyanalyzer.track.model.TrackDataRepository
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.upload.model.Upload
import io.r2dbc.spi.Blob
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.Resource
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
    private val tracks: TrackRepository,
    private val trackData: TrackDataRepository,
    @Value("classpath:mp3/1kHz-5sec.mp3") val _1kHz: Resource,
    @Value("classpath:mp3/10kHz-5sec.mp3") val _10kHz: Resource,
    @Value("classpath:mp3/100Hz-5sec.mp3") val _100Hz: Resource,
    @Value("classpath:mp3/250Hz-5sec.mp3") val _250Hz: Resource,
    @Value("classpath:mp3/440Hz-5sec.mp3") val _440Hz: Resource
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        readFiles().flatMap { persistTracks(it) }.subscribe()
    }

    @Transactional
    fun persistTracks(file: Upload): Mono<TrackData> {
        return createTrack(file).flatMap { createTrackData(it, file) }
    }

    fun createTrack(file: Upload): Mono<Track> {
        val track = Track(name = file.name)
        return tracks.save(track)
    }

    fun createTrackData(track: Track, file: Upload): Mono<TrackData> {
        val data = TrackData(track.id!!, file.data)
        return trackData.save(data)
    }

    private fun readFiles(): Flux<Upload> {
        return resources()
            .map {
                val stream = it.inputStream
                val data = ByteBuffer.wrap(stream.readAllBytes())
                val blob = Blob.from(Mono.just(data))
                Upload(name = it.filename!!, data = blob)
            }
            .toFlux()
    }

    private fun resources(): Flux<Resource> {
        return Flux.just(_1kHz, _440Hz, _10kHz, _100Hz, _250Hz)
    }
}
