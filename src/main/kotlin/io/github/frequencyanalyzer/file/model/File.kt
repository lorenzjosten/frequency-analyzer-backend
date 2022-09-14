package io.github.frequencyanalyzer.file.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("file")
data class File(

    @Id
    val id: Long = 0,
    val name: String,
    val data: ByteArray
)
