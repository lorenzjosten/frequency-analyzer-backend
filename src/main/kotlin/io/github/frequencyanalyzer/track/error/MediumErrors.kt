package io.github.frequencyanalyzer.track.error

sealed class MediumError(override val message: String) : RuntimeException()

class MediumNotFoundException(trackId: Long) : MediumError("Cannot find data for track with id $trackId.")

class MediumProcessingException(trackId: Long) : MediumError("Cannot process data of track with id $trackId.")
