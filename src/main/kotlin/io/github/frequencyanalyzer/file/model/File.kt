package io.github.frequencyanalyzer.file.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

@Table("file")
data class File(
    @Id val id: Long? = null,
    @NotBlank val name: String,
    @NotEmpty val data: ByteArray
) {

    fun hasId(): Boolean = id != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (id != other.id) return false
        if (name != other.name) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
