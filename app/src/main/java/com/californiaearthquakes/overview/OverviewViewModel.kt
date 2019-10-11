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
import android.os.Bundle
import com.californiaearthquakes.search_options.SearchOptions


class OverviewViewModel(private val searchOptions: SearchOptions?) : ViewModel() {

    private val _earthquakes = MutableLiveData<List<Earthquake>>()
    val earthquakes : LiveData<List<Earthquake>>
        get() = _earthquakes

    private val _isLoadingMoreResults = MutableLiveData<Boolean>(false)
    val isLoadingMoreResuls : LiveData<Boolean>
        get() = _isLoadingMoreResults

    private var resultsLimit = 10

    private var minMagnitude: Int? = null

    private var maxMagnitude: Int? = null

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        if (searchOptions != null) {
            minMagnitude = searchOptions.minMagnitude
            maxMagnitude = searchOptions.maxMagnitude
        }
        getLatestEarthquakes()
    }
    private fun getLatestEarthquakes() {
        coroutineScope.launch {
            val getEarthquakesDeffered =
                UsgsApi.usgsApiService.getEarthquakes(Util.RESULTS_FORMAT,
                    Util.LATITUDE,
                    Util.LONGITUDE,
                    Util.MAX_RADIUS_KM,
                    minMagnitude,
                    maxMagnitude,
                    Util.ORDER_BY,
                    resultsLimit)
            try {
                val result = getEarthquakesDeffered.await()
                _earthquakes.value = result.features.map { it.properties }
                _isLoadingMoreResults.value = false

            } catch (e: Exception) {
                _earthquakes.value = ArrayList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun increaseResultsLimitByTen() {
        resultsLimit += 10
    }

    fun getMoreResults() {
        _isLoadingMoreResults.value = true
        increaseResultsLimitByTen()
        getLatestEarthquakes()

    }
}