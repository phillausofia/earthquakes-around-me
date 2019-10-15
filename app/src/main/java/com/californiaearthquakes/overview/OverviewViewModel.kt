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
import com.californiaearthquakes.search_options.SearchOptions


class OverviewViewModel(searchOptions: SearchOptions?) : ViewModel() {

    private val _earthquakes = MutableLiveData<List<Earthquake>>()
    val earthquakes : LiveData<List<Earthquake>>
        get() = _earthquakes

    private val _isLoadingMoreResults = MutableLiveData<Boolean>(false)
    val isLoadingMoreResuls : LiveData<Boolean>
        get() = _isLoadingMoreResults

    private val _earthquakeToNavigateTo = MutableLiveData<Earthquake>()
    val earthquakeToNavigateTo : LiveData<Earthquake>
        get() = _earthquakeToNavigateTo

    private var resultsLimit = Utils.INITIAL_VALUES.RESULS_LIMIT

    private var minMagnitude: Int? = null

    private var maxMagnitude: Int? = null

    private var startTime: String? = null

    private var endTime: String? = null

    private var orderBy: String = Utils.INITIAL_VALUES.ORDER_BY

    private var maxRadiusKm = Utils.INITIAL_VALUES.MAX_RADIUS_KM

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        if (searchOptions != null) {
            minMagnitude = searchOptions.minMagnitude
            maxMagnitude = searchOptions.maxMagnitude
            searchOptions.orderBy?.let { orderBy = it }
            searchOptions.maxRadiusKm?.let { maxRadiusKm = it}
            searchOptions.startTime?.let { startTime = it}
            searchOptions.endTime?.let { endTime = it}
        }
        getLatestEarthquakes()
    }
    private fun getLatestEarthquakes() {
        coroutineScope.launch {
            val getEarthquakesDeferred =
                UsgsApi.usgsApiService.getEarthquakes(Utils.FINAL_CONSTANTS.RESULTS_FORMAT,
                    Utils.FINAL_CONSTANTS.LATITUDE,
                    Utils.FINAL_CONSTANTS.LONGITUDE,
                    maxRadiusKm,
                    minMagnitude,
                    maxMagnitude,
                    orderBy,
                    resultsLimit, startTime, endTime)
            try {
                val result = getEarthquakesDeferred.await()
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

    fun displayEarthquakeDetails(earthquakeToNavigateTo: Earthquake) {
        _earthquakeToNavigateTo.value = earthquakeToNavigateTo
    }

    fun displayEartquakeDetailsCompleted() {
        _earthquakeToNavigateTo.value = null
    }
}