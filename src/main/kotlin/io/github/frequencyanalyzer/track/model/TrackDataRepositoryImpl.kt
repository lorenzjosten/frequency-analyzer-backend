package io.github.frequencyanalyzer.track.model

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream
import io.r2dbc.spi.Blob
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.nio.ByteBuffer

private const val SAVE = "INSERT INTO track_data (track_id, blob) VALUES (:trackId, :blob)"

private const val FIND = "SELECT t.track_id as trackId, t.blob as blob FROM track_data as t WHERE t.track_id = :trackId"

private const val DELETE = "DELETE FROM track_data WHERE track_id = :trackId"

private const val EXISTS = "SELECT COUNT(1) FROM track_data WHERE track_id = :trackId"

@Repository
class TrackDataRepositoryImpl(private val client: DatabaseClient) : TrackDataRepository {
    /**
     * r2dbc-h2 does currently (10/22) not support writing blob data directly.
     * See https://github.com/r2dbc/r2dbc-h2/issues/129 for more details.
     * As a workaround blob data is written as ByteArray (blocking) and read as ByteBuffer
     */
    override fun save(data: TrackData): Mono<TrackData> {
        return data
                .streamAsMono()
                .map(::ByteBufferBackedInputStream)
                .flatMap {
                    client.sql(SAVE)
                            .bind("trackId", data.trackId!!)
                            .bind("blob", it.readAllBytes())
                            .map { _ -> data }
                            .first()
                }
    }

    override fun find(trackId: Long): Mono<TrackData> {
        return client.sql(FIND)
                .bind("trackId", trackId)
                .map { row ->
                    TrackData(
                            trackId = row["trackId"] as Long,
                            blob = (row["blob"] as ByteBuffer).toBlob()
                    )
                }
                .first()
    }

    override fun delete(trackId: Long): Mono<Void> {
        return client.sql(DELETE)
                .bind("trackId", trackId)
                .then()
    }


    override fun exists(trackId: Long): Mono<Boolean> {
        return client.sql(EXISTS)
                .bind("trackId", trackId)
                .map { row -> row[0] == 1 }
                .first()
    }

    private fun ByteBuffer.toBlob() = Blob.from(Mono.just(this))

}