package io.github.frequencyanalyzer.decoder.model

data class DecodedFrame(
    val sampleFrequency: Int,
    val bufferSize: Int,
    val buffer: ShortArray,
    val durationMs: Float
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecodedFrame

        if (sampleFrequency != other.sampleFrequency) return false
        if (bufferSize != other.bufferSize) return false
        if (!buffer.contentEquals(other.buffer)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sampleFrequency
        result = 31 * result + bufferSize
        result = 31 * result + buffer.contentHashCode()
        return result
    }
}
