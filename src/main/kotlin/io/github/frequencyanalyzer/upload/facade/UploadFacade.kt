package io.github.frequencyanalyzer.upload.facade

import io.github.frequencyanalyzer.track.model.Track
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface UploadFacade {

    fun fileUpload(filePart: FilePart): Mono<Track>
}