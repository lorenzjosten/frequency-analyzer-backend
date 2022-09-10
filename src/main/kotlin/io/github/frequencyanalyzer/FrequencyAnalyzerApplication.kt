package io.github.frequencyanalyzer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FrequencyAnalyzerApplication

fun main(args: Array<String>) {
	runApplication<FrequencyAnalyzerApplication>(*args)
}
