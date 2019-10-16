package com.californiaearthquakes.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


enum class EarthquakeIntensity{
    NOT_EVEN_AN_EARTHQUAKE, LOW, MED, HIGH
}

object Model {

    data class Result(
        val features: List<Feature>
        )

    data class Feature(
        val properties: Earthquake
    )
    @Parcelize
    data class Earthquake(
        val mag: Double,
        val place: String,
        val time: Long,
        val magType: String,
        val title: String
    ): Parcelable {
        val intensity = when (mag) {
            in -10.0..1.99 -> EarthquakeIntensity.NOT_EVEN_AN_EARTHQUAKE
            in 2.0..3.99 -> EarthquakeIntensity.LOW
            in 4.0..5.99 -> EarthquakeIntensity.MED
            else -> EarthquakeIntensity.HIGH
        }
    }
}