package io.github.frequencyanalyzer.track.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotBlank

@Table("tracks")
data class Track(
        @Id val id: Long? = null,
        @NotBlank val name: String
)
