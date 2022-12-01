package io.github.frequencyanalyzer.track.error

sealed class TrackDataError(override val message: String) : RuntimeException()

class TrackDataNotFoundException(trackId: Long) : TrackDataError("Cannot find data for track with id $trackId.")

class TrackDataPersistenceException(trackId: Long) : TrackDataError("Cannot persist data of track with id $trackId.")
