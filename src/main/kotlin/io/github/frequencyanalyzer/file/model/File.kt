package io.github.frequencyanalyzer.file.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Component
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

@Table("file")
data class File(
    @Id val id: Long? = null,
    @NotBlank val name: String,
    @NotEmpty val data: ByteArray
) {
    fun hasId(): Boolean = id != null
}

data class FileDto(
    val id: Long?,
    val name: String
)

@Component
class FileMapper : (File) -> FileDto by { FileDto(it.id, it.name) }
