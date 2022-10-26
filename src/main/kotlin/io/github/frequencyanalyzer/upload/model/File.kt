package io.github.frequencyanalyzer.upload.model

import io.r2dbc.spi.Blob

class File(
        val name: String,
        val data: Blob
)