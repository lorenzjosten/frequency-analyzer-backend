package io.github.frequencyanalyzer.decoder.service

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream
import io.github.frequencyanalyzer.decoder.Mp3Decoder
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.track.model.TrackData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.InputStream

@Service
class DecoderServiceImpl : DecoderService {

    override fun decode(trackData: TrackData): Flux<DecodedFrame> {
        return trackData
                .streamAsFlux()
                .map(::ByteBufferBackedInputStream)
                .flatMap(::decodingFlux)
    }

    private fun decodingFlux(input: InputStream): Flux<DecodedFrame> {
        return Flux.using(
                { Mp3DecoderImpl(input) },
                { frameReadingFlux(it) },
                { it.close() }
        )
    }

    private fun frameReadingFlux(decoder: Mp3Decoder): Flux<DecodedFrame> {
        return Flux.generate {
            when (val frame = decoder.readFrame()) {
                null -> it.complete()
                else -> it.next(frame)
            }
        }
    }
}