package com.californiaearthquakes.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.californiaearthquakes.network.Model.Earthquake
import com.californiaearthquakes.network.UsgsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OverviewViewModule : ViewModel() {

    private val _earthquakes = MutableLiveData<List<Earthquake>>()
    val earthquakes : LiveData<List<Earthquake>>
        get() = _earthquakes

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getLatestTenEarthquakes()
    }
    private fun getLatestTenEarthquakes() {
        coroutineScope.launch {
            val getEarthquakesDeffered =
                UsgsApi.usgsApiService.getEarthquakes("geojson",
                    36.778259,
                    -119.417931,
                    500.0,
                    "time",
                    10)
            try {
                val result = getEarthquakesDeffered.await()
                _earthquakes.value = result.features.map { it.properties }
            } catch (e: Exception) {
                _earthquakes.value = ArrayList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}