package com.californiaearthquakes.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.californiaearthquakes.network.Model.Earthquake

class DetailViewModel(earthquake: Earthquake) : ViewModel() {

    private val _selectedEarthquake = MutableLiveData<Earthquake>()
    val selectedEarthquake : LiveData<Earthquake>
        get() = _selectedEarthquake


    val displayEarthquakeMag = Transformations.map(selectedEarthquake) {
        it.mag.toString()
    }

    init {
        _selectedEarthquake.value = earthquake
    }
}