package io.github.frequencyanalyzer.file.service

import io.github.frequencyanalyzer.file.model.File
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FileService {

    fun save(file: File): Mono<File>

    fun findById(id: Long): Mono<File>

    fun findAll(): Flux<File>

    fun deleteById(id: Long): Mono<Void>
}
