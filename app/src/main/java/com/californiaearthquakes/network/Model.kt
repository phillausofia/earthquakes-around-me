package com.californiaearthquakes.network


object Model {

    data class Result(
        val features: Array<Feature>
        )

    data class Feature(
        val properties: Earthquake
    )

    data class Earthquake(
        val mag: Double,
        val place: String,
        val time: Long
    )
}