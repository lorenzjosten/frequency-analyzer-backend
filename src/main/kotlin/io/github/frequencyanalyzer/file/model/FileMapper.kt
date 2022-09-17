package io.github.frequencyanalyzer.file.model

class FileMapper : (File) -> FileDto by { FileDto(it.id, it.name) }
