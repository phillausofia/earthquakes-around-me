package com.californiaearthquakes.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.californiaearthquakes.network.Model.Earthquake
import java.text.SimpleDateFormat
import java.util.*

class DetailViewModel(earthquake: Earthquake) : ViewModel() {

    private val _selectedEarthquake = MutableLiveData<Earthquake>()
    val selectedEarthquake : LiveData<Earthquake>
        get() = _selectedEarthquake


    val displayEarthquakeMag = Transformations.map(selectedEarthquake) {
        it.mag.toString()
    }

    val displayEarthquakeTime = Transformations.map(selectedEarthquake) {
        SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Date(it.time))
    }

    init {
        _selectedEarthquake.value = earthquake
    }
}