package io.github.frequencyanalyzer.track.error

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain

@Configuration
class TrackDataErrorHandler {

    @Bean
    fun trackDataNotFound() = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        chain.filter(exchange)
            .onErrorResume(TrackDataNotFoundException::class.java) {
                exchange.response.statusCode = HttpStatus.NOT_FOUND
                exchange.response.setComplete()
            }
    }

    @Bean
    fun trackDataNotPersisted() = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        chain.filter(exchange)
            .onErrorResume(TrackDataPersistenceException::class.java) {
                exchange.response.statusCode = HttpStatus.BAD_REQUEST
                exchange.response.setComplete()
            }
    }
}
