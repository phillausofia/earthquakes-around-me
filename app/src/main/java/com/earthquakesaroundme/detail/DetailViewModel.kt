package com.earthquakesaroundme.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.earthquakesaroundme.network.Model.Earthquake
import java.text.SimpleDateFormat
import java.util.*

class DetailViewModel(earthquake: Earthquake) : ViewModel() {

    private val _selectedEarthquake = MutableLiveData<Earthquake>()
    val selectedEarthquake : LiveData<Earthquake>
        get() = _selectedEarthquake

    val adSize = DetailUtils.adSize


    val displayEarthquakeMag = Transformations.map(selectedEarthquake) {
        it.properties.mag.toString()
    }

    val displayEarthquakeTime = Transformations.map(selectedEarthquake) {
        SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z")
            .format(Date(it.properties.time))
    }

    val displayEarthquakeDepth = Transformations.map(selectedEarthquake) {
        "${it.geometry.coordinates[2]} km"
    }



    init {
        _selectedEarthquake.value = earthquake

    }

    fun getEarthquakeCoordinates() =
        Pair(_selectedEarthquake.value!!.geometry.coordinates[1],
            _selectedEarthquake.value!!.geometry.coordinates[0])

    fun getAdContainerHeight() = "${adSize!!.height}dp"
}