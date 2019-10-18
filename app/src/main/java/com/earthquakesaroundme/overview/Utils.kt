package com.earthquakesaroundme.overview

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object Utils {

    var userLocation: Location? = null

    object FINAL_CONSTANTS {

        val RESULTS_FORMAT = "geojson"
        val LATITUDE = 36.778259
        val LONGITUDE = -119.417931
    }

    object INITIAL_VALUES {
        val MAX_RADIUS_KM = 500
        val ORDER_BY = "time"
        val RESULS_LIMIT = 10
    }

    object FusedLocationProvider {
        private var fusedLocationProviderClient: FusedLocationProviderClient? = null
        private var location : MutableLiveData<Location> = MutableLiveData()

        fun getInstance(appContext : Context) : FusedLocationProviderClient {
            if (fusedLocationProviderClient == null) {
                fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(appContext)
            }
            return fusedLocationProviderClient!!
        }

        fun getLocation() : LiveData<Location> {
            fusedLocationProviderClient!!.lastLocation
                .addOnSuccessListener { loc: Location ->
                    location.value = loc
                }
            return location
        }

    }

}