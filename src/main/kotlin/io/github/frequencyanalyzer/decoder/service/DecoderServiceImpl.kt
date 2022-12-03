package io.github.frequencyanalyzer.decoder.service

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream
import io.github.frequencyanalyzer.decoder.Mp3Decoder
import io.github.frequencyanalyzer.decoder.Mp3DecoderImpl
import io.github.frequencyanalyzer.decoder.model.DecodedFrame
import io.github.frequencyanalyzer.track.model.TrackData
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.InputStream

/**
 * A service providing methods for decoding a mp3 audio track.
 * @author Lorenz Josten
 */
@Service
class DecoderServiceImpl : DecoderService {

    /**
     * Creates a Flux of decoded mp3-frames from a given audio track.
     * @param trackData TrackData containing the mp3 media data in Blob format
     * @return a Flux of decoded mp3-frames
     */
    override fun decode(trackData: TrackData): Flux<DecodedFrame> {
        return trackData
            .streamAsFlux()
            .map(::ByteBufferBackedInputStream)
            .flatMap(::decodingFlux)
    }

    /**
     * Creates a Flux of decoded mp3-frames from a given InputStream.
     * @param input An input stream of a mp3 medium
     * @return a Flux of decoded mp3-frames
     */
    private fun decodingFlux(input: InputStream): Flux<DecodedFrame> {
        return Flux.using(
            { Mp3DecoderImpl(input) },
            { frameReadingFlux(it) },
            { it.close() }
        )
    }

    /**
     * Creates a Flux of decoded mp3-frames using a given Mp3Decoder.
     * @param decoder A Mp3Decoder
     * @return a Flux of decoded mp3-frames
     */
    private fun frameReadingFlux(decoder: Mp3Decoder): Flux<DecodedFrame> {
        return Flux.generate {
            when (val frame = decoder.readFrame()) {
                null -> it.complete()
                else -> it.next(frame)
            }
        }
    }
}
