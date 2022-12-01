package io.github.frequencyanalyzer.track.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.DELETE
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

private const val URI = "/track"

@Configuration
class TrackRoutes(private val trackRequestHandler: TrackRequestHandler) {

    @Bean
    fun subscribe(): RouterFunction<ServerResponse> {
        return route(GET(URI), trackRequestHandler::subscribe)
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
    fun data(): RouterFunction<ServerResponse> {
        return route(GET("$URI/{id}/data"), trackRequestHandler::data)
    }

    @Bean
    fun analyze(): RouterFunction<ServerResponse> {
        return route(GET("$URI/{id}/analyze"), trackRequestHandler::analyze)
    }
}
