package io.github.frequencyanalyzer.track.model

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TrackRepository : ReactiveCrudRepository<Track, Long>
