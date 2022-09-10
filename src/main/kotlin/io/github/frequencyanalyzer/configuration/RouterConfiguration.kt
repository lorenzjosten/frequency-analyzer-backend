package io.github.frequencyanalyzer.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Configuration
class RouterConfiguration {

    @Bean
    fun htmlRouter(@Value("classpath:/static/index.html") html: Resource): RouterFunction<ServerResponse> {
        return route(GET("/")) { ok().contentType(MediaType.TEXT_HTML).bodyValue(html) }
    }
}