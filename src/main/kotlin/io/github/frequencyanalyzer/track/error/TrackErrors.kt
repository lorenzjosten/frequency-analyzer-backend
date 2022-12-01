package io.github.frequencyanalyzer.track.error

sealed class TrackError(override val message: String) : RuntimeException()

class TrackNotFoundException(id: Long) : TrackError("Cannot find track with id $id.")

class TrackPersistenceException(name: String) : TrackError("Cannot persist track $name.")
