package io.github.frequencyanalyzer.file.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Component
import java.util.function.Function
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Table("file")
data class File(

    @Id
    val id: Long? = null,
    @NotNull
    @NotBlank
    val name: String,
    @NotNull
    @NotEmpty
    val data: ByteArray
) {

    fun hasId() = id != null
}

data class FileDto(
    val id: Long?,
    val name: String
)

@Component
class FileMapper : Function<File, FileDto> by Function({ f -> FileDto(f.id, f.name) })
