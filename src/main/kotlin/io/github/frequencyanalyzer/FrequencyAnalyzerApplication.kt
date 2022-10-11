package io.github.frequencyanalyzer

import io.github.frequencyanalyzer.track.model.Medium
import io.github.frequencyanalyzer.track.model.MediumRepository
import io.github.frequencyanalyzer.track.model.Track
import io.github.frequencyanalyzer.track.model.TrackRepository
import io.github.frequencyanalyzer.upload.model.File
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@SpringBootApplication
class FrequencyAnalyzerApplication

fun main(args: Array<String>) {
    runApplication<FrequencyAnalyzerApplication>(*args)
}

@Component
class SetupData(
    private val resourceResolver: ResourcePatternResolver,
    private val tracks: TrackRepository,
    private val media: MediumRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        readFiles().forEach { saveFileData(it).subscribe() }
    }

    @Transactional
    fun saveFileData(file: File): Mono<Medium> {
        return createTrack(file).flatMap { createMedium(it, file) }
    }

    fun createTrack(file: File): Mono<Track> {
        val track = Track(name = file.name)
        return tracks.save(track)
    }

    fun createMedium(track: Track, file: File): Mono<Medium> {
        val medium = Medium(trackId = track.id!!, data = file.data)
        return media.save(medium)
    }

    private fun readFiles(): List<File> {
        return resourceResolver
            .getResources("classpath:mp3/*.mp3")
            .map { it.file }
            .map { File(name = it.name, data = it.readBytes()) }
    }
}
