package io.github.frequencyanalyzer.upload.api

import io.github.frequencyanalyzer.track.model.TrackDto
import io.github.frequencyanalyzer.track.model.TrackMapper
import io.github.frequencyanalyzer.track.service.TrackService
import io.github.frequencyanalyzer.upload.error.NoFileUploadException
import io.github.frequencyanalyzer.upload.model.File
import io.r2dbc.spi.Blob
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

private const val FORM_FIELD = "file"

@Component
class UploadRequestHandler(
        private val trackService: TrackService,
        private val trackMapper: TrackMapper
) {

    fun upload(request: ServerRequest): Mono<ServerResponse> {
        val track = retrieveFile(request).flatMap(trackService::create).map(trackMapper)

        return ok().contentType(MediaType.APPLICATION_JSON).body(track, TrackDto::class.java)
    }

    private fun retrieveFile(request: ServerRequest): Mono<File> {
        return retrieveFilePart(request)
                .map { file ->
                    val name = file.filename()
                    val content = DataBufferUtils.join(file.content())
                    val bytes = content.map { it.asByteBuffer() }
                    val blob = Blob.from(bytes)

                    File(name = name, data = blob)
                }
    }

    private fun retrieveFilePart(request: ServerRequest): Mono<FilePart> {
        return request
                .multipartData()
                .map { it[FORM_FIELD] }
                .map { it.firstOrNull(::isFilePart) }
                .mapNotNull { it as FilePart }
                .switchIfEmpty(Mono.error(NoFileUploadException()))
    }

    private fun isFilePart(part: Part): Boolean = part is FilePart
}