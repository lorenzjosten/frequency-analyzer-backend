package io.github.frequencyanalyzer

import io.github.frequencyanalyzer.file.model.File
import io.github.frequencyanalyzer.file.service.FileService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@SpringBootApplication
class FrequencyAnalyzerApplication

fun main(args: Array<String>) {
    runApplication<FrequencyAnalyzerApplication>(*args)
}

@Component
class SetupData(
    private val resourceResolver: ResourcePatternResolver,
    private val fileService: FileService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        readFiles().map(fileService::save).map(Mono<File>::block)
    }

    private fun readFiles(): List<File> {
        return resourceResolver
            .getResources("classpath:mp3/*.mp3")
            .map { it.file }
            .map { File(name = it.name, data = it.readBytes()) }
    }
}
