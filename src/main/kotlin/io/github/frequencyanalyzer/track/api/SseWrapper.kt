package io.github.frequencyanalyzer.track.api

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.codec.ServerSentEvent

abstract class SseWrapable(@JsonIgnore val sseEventName: String) {
    object ClosingEvent : SseWrapable("close")
}

class SseWrapper : (SseWrapable) -> ServerSentEvent<SseWrapable> {
    private val ids = generateSequence(0L) { it + 1 }.iterator()

    override fun invoke(data: SseWrapable) = ServerSentEvent
        .builder(data)
        .id("${ids.next()}")
        .event(data.sseEventName)
        .build()
}
