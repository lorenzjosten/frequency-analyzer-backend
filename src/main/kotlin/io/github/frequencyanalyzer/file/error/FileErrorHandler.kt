package io.github.frequencyanalyzer.file.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

sealed class FileError(override val message: String) : RuntimeException()

class FileNotFoundException(id: Long) : FileError("Cannot find file with id $id.")

class FileProcessingException : FileError("Cannot process file.")

data class FileErrorDto(val message: String)

@ControllerAdvice
class FileErrorHandler {

    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(e: FileNotFoundException): ResponseEntity<FileErrorDto> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(FileErrorDto(e.message))
    }

    @ExceptionHandler(FileProcessingException::class)
    fun handleFileProcessingException(e: FileProcessingException): ResponseEntity<FileErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(FileErrorDto(e.message))
    }
}
