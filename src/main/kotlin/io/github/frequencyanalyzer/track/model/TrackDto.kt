package io.github.frequencyanalyzer.track.model

import io.github.frequencyanalyzer.track.api.SseWrapable

data class TrackDto(val id: Long, val name: String) : SseWrapable("track")