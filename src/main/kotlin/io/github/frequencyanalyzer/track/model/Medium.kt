package io.github.frequencyanalyzer.track.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotEmpty

@Table("medium")
data class Medium(
    @Id val id: Long? = null,
    val trackId: Long? = null,
    @NotEmpty val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Medium

        if (id != other.id) return false
        if (trackId != other.trackId) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (trackId?.hashCode() ?: 0)
        result = 31 * result + data.contentHashCode()
        return result
    }
}

