package io.github.frequencyanalyzer.upload.error

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain

@Configuration
class UploadErrorFilter {

    @Bean
    fun fileUploadNotFound() = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        chain.filter(exchange)
            .onErrorResume(NoFileUploadException::class.java) {
                exchange.response.statusCode = HttpStatus.BAD_REQUEST
                exchange.response.setComplete()
            }
    }
}
