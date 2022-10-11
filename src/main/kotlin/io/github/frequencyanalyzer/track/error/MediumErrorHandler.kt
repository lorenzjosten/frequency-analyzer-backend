package io.github.frequencyanalyzer.track.error

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain

@Configuration
class MediumErrorHandler {

    @Bean
    fun mediumNotFound() = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        chain.filter(exchange)
            .onErrorResume(MediumNotFoundException::class.java) {
                exchange.response.statusCode = HttpStatus.NOT_FOUND
                exchange.response.setComplete()
            }
    }

    @Bean
    fun mediumNotProcessable() = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        chain.filter(exchange)
            .onErrorResume(MediumProcessingException::class.java) {
                exchange.response.statusCode = HttpStatus.NOT_FOUND
                exchange.response.setComplete()
            }
    }
}
