package io.github.frequencyanalyzer.file.repository

import io.github.frequencyanalyzer.file.model.File
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface FileRepository : ReactiveCrudRepository<File, Long>
