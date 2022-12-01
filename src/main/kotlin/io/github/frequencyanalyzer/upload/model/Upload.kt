package io.github.frequencyanalyzer.upload.model

import io.r2dbc.spi.Blob

data class Upload(
    val name: String,
    val data: Blob
)
