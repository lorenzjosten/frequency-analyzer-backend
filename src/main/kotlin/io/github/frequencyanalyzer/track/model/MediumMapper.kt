package io.github.frequencyanalyzer.track.model

import org.springframework.stereotype.Component

@Component
class MediumMapper : (Medium) -> MediumDto by { MediumDto(it.trackId!!, it.data) }