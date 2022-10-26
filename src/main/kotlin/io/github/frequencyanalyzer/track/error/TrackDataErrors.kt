package io.github.frequencyanalyzer.track.error

sealed class TrackDataError(override val message: String) : RuntimeException()

class TrackDataNotFoundException(trackId: Long) : TrackDataError("Cannot find data for track with id $trackId.")

class TrackDataProcessingException(trackId: Long) : TrackDataError("Cannot process data of track with id $trackId.")
