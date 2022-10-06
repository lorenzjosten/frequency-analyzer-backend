package io.github.frequencyanalyzer.file.service

import io.github.frequencyanalyzer.file.model.File
import io.github.frequencyanalyzer.file.repository.FileRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FileServiceImpl(private val fileRepository: FileRepository) : FileService {

    override fun save(file: File): Mono<File> {
        return fileRepository.save(file)
    }

    override fun findById(id: Long): Mono<File> {
        return fileRepository.findById(id)
    }

    override fun findAll(): Flux<File> {
        return fileRepository.findAll()
    }

    override fun deleteById(id: Long): Mono<Void> {
        return fileRepository.deleteById(id)
    }
}
