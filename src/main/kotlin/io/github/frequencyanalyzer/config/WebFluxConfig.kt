package io.github.frequencyanalyzer.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
@Profile("dev")
class WebFluxConfig : WebFluxConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/track/**")
            .allowedOrigins("*")
            .allowedMethods("*")
        registry
            .addMapping("/upload")
            .allowedOrigins("*")
            .allowedMethods("*")
    }
}
