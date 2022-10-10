package io.github.frequencyanalyzer.track.model

import org.springframework.stereotype.Component

@Component
class TrackMapper : (Track) -> TrackDto by { track -> TrackDto(track.id!!, track.name) }