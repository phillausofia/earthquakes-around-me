package com.californiaearthquakes.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


object Model {

    data class Result(
        val features: List<Feature>
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