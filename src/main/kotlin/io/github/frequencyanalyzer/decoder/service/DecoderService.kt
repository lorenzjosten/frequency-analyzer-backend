package io.github.frequencyanalyzer.decoder.service

import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.track.model.TrackData
import reactor.core.publisher.Flux

interface DecoderService {

    fun decode(trackData: TrackData): Flux<DecodedFrame>
}
