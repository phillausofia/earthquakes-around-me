package com.earthquakesaroundme.search_options

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchOptions(val minMagnitude: Int?,
                         val maxMagnitude: Int?,
                         val orderBy: String?,
                         val maxRadiusKm: Int?,
                         val startTime: String?,
                         val endTime: String?): Parcelable