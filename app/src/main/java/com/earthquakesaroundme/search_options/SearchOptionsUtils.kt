package com.earthquakesaroundme.search_options

object SearchOptionsUtils {

    object EARTHQUAKE_MAGNITUDE_RANGE{

        val MIN_MAGNITUDE = 0
        val MAX_MAGNITUDE = 10
    }

    object UsgsApiOrderByValues {

        val TIME_ASC = "time-asc"
        val MAG = "magnitude"
        val MAG_ASC = "magnitude-asc"
    }
}