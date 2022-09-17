package io.github.frequencyanalyzer.spectralanalysis.extension

import java.lang.Math.sqrt

fun Map<Int, Double>.rms() = sqrt((1.0/size) * values.sumOf { it * it })