package io.github.frequencyanalyzer.upload.api

import io.github.frequencyanalyzer.track.model.TrackDto
import io.github.frequencyanalyzer.track.model.TrackMapper
import io.github.frequencyanalyzer.upload.facade.UploadFacade
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@Component
class UploadRequestHandler(
    private val uploadFacade: UploadFacade,
    private val trackMapper: TrackMapper
) {

    fun upload(request: ServerRequest): Mono<ServerResponse> {
        val fileParts = request.multipartData()
            .map {
                it["file"]
            }
            .mapNotNull {
               it.first() as FilePart
            }
        val newTrack = fileParts.flatMap(uploadFacade::fileUpload).map(trackMapper)

        return ok().contentType(MediaType.APPLICATION_JSON).body(newTrack, TrackDto::class.java)
    }
}