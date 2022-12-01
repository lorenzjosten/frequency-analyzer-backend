package io.github.frequencyanalyzer.upload.error

sealed class UploadError(override val message: String) : RuntimeException()

class NoFileUploadException : UploadError("No form data with element 'file' found.")
