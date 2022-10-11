package io.github.frequencyanalyzer.upload.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.contentType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse


private const val URI = "/upload"

@Configuration
class UploadRoutes(private val uploadRequestHandler: UploadRequestHandler) {

    @Bean
    fun create(): RouterFunction<ServerResponse> {
        return route(POST(URI).and(contentType(MediaType.MULTIPART_FORM_DATA)), uploadRequestHandler::upload)
    }
}