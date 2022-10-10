package io.github.frequencyanalyzer.upload.service

import io.github.frequencyanalyzer.upload.model.File
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface UploadService {

    fun retrieveFile(filePart: FilePart): Mono<File>
}
