package io.github.frequencyanalyzer.file.controller

import io.github.frequencyanalyzer.file.error.FileNotFoundException
import io.github.frequencyanalyzer.file.error.FileProcessingException
import io.github.frequencyanalyzer.file.model.FileDto
import io.github.frequencyanalyzer.file.model.FileMapper
import io.github.frequencyanalyzer.file.service.FileService
import io.github.frequencyanalyzer.file.service.UploadService
import io.github.frequencyanalyzer.spectralanalysis.model.TimedPcmPowerSpectrum
import io.github.frequencyanalyzer.spectralanalysis.service.SpectralAnalysisService
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/file")
@Validated
class FileController(
    private val fileService: FileService,
    private val uploadService: UploadService,
    private val spectralAnalysisService: SpectralAnalysisService
) {
    private val logger: Logger = getLogger("FileController")

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun post(@RequestPart("file") multiPartFile: Mono<FilePart>): Mono<FileDto> {
        logger.info("Creating a new file.")

        return multiPartFile
            .flatMap(uploadService::retrieveFile)
            .flatMap(fileService::save)
            .map(FileMapper())
            .onErrorResume { Mono.error(FileProcessingException()) }
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun get(@PathVariable("id") id: Long): Mono<FileDto> {
        logger.info("Retrieving file with id $id.")

        return fileService
            .findById(id)
            .map(FileMapper())
            .switchIfEmpty(Mono.error(FileNotFoundException(id)))
    }

    @GetMapping("/{id}/power-spectrum", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun powerSpectrum(@PathVariable("id") id: Long): Flux<TimedPcmPowerSpectrum> {
        logger.info("Calculating power spectrum for file with id $id.")

        return spectralAnalysisService
            .analyseFile(id)
            .switchIfEmpty(Mono.error(FileProcessingException()))
    }

}
