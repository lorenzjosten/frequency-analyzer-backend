package io.github.frequencyanalyzer.track.model

data class MediumDto(val trackId: Long, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediumDto

        if (trackId != other.trackId) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackId.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}