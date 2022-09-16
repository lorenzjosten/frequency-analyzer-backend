package io.github.frequencyanalyzer.file.service

import io.github.frequencyanalyzer.file.model.File
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface UploadService {

    fun retrieveFile(filePart: FilePart): Mono<File>
}
