package io.github.frequencyanalyzer.track.error

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain

class FileProcessingException : TrackError("Cannot process file.")

data class TrackErrorDto(val message: String)

@Configuration
class TrackErrorFilter {

    @Bean
    fun trackNotFound() = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        chain.filter(exchange)
            .onErrorResume(TrackNotFoundException::class.java) {
                exchange.response.statusCode = HttpStatus.NOT_FOUND
                exchange.response.setComplete()
            }
    }
}
