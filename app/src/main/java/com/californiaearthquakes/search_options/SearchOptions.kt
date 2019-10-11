package com.californiaearthquakes.search_options

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchOptions(val minMagnitude: Int,
                         val maxMagnitude: Int): Parcelable