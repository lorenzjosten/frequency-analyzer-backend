package io.github.frequencyanalyzer.upload.service

import io.github.frequencyanalyzer.upload.model.File
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UploadServiceImpl : UploadService {

    override fun retrieveFile(filePart: FilePart): Mono<File> {
        val fileData = retrieveFileData(filePart)
        val fileName = filePart.filename()

        return fileData.map { File(name = fileName, data = it) }
    }

    private fun retrieveFileData(filePart: FilePart): Mono<ByteArray> {
        return filePart
            .content()
            .map(::readDataBuffer)
            .reduce(ByteArray::plus)
    }

    private fun readDataBuffer(dataBuffer: DataBuffer): ByteArray {
        val readableBytes = dataBuffer.readableByteCount()
        val byteArray = ByteArray(readableBytes)

        dataBuffer.read(byteArray, 0, readableBytes)

        return byteArray
    }
}
