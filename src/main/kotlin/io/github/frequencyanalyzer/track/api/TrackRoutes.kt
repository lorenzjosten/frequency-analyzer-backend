package io.github.frequencyanalyzer.track.api

import io.github.frequencyanalyzer.track.requesthandler.TrackRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.DELETE
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

private const val URI = "/track"

@Configuration
class TrackRoutes(
    private val trackRequestHandler: TrackRequestHandler
) {

    @Bean
    fun findAll(): RouterFunction<ServerResponse> {
        return route(GET(URI), trackRequestHandler::findAll)
    }

    @Bean
    fun find(): RouterFunction<ServerResponse> {
        return route(GET("$URI/{id}"), trackRequestHandler::find)
    }

    @Bean
    fun delete(): RouterFunction<ServerResponse> {
        return route(DELETE("$URI/{id}"), trackRequestHandler::delete)
    }

    @Bean
    fun medium(): RouterFunction<ServerResponse> {
        return route(GET("$URI/{id}/medium"), trackRequestHandler::medium)
    }

    @Bean
    fun powerSpectrum(): RouterFunction<ServerResponse> {
        return route(GET("$URI/{id}/power-spectrum"), trackRequestHandler::powerSpectrum)
    }
}