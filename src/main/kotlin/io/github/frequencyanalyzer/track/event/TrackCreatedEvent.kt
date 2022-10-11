package io.github.frequencyanalyzer.track.event

import io.github.frequencyanalyzer.track.model.Track
import org.springframework.context.ApplicationEvent

class TrackCreatedEvent(source: Any, val track: Track) : ApplicationEvent(source)
