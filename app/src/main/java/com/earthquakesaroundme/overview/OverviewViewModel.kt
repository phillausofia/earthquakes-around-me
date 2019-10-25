package com.earthquakesaroundme.overview

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earthquakesaroundme.network.Model.Earthquake
import com.earthquakesaroundme.network.UsgsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.earthquakesaroundme.search_options.SearchOptions
import com.google.android.gms.location.LocationServices
import java.util.*


class OverviewViewModel(private val searchOptions: SearchOptions?,
                         private val application: Application) : ViewModel() {

    private val _earthquakes = MutableLiveData<List<Earthquake>>()
    val earthquakes : LiveData<List<Earthquake>>
        get() = _earthquakes

    private val _isLoadingMoreResults = MutableLiveData<Boolean>(false)
    val isLoadingMoreResuls : LiveData<Boolean>
        get() = _isLoadingMoreResults

    private val _earthquakeToNavigateTo = MutableLiveData<Earthquake>()
    val earthquakeToNavigateTo : LiveData<Earthquake>
        get() = _earthquakeToNavigateTo

    private val _foundUserLocation = MutableLiveData<Boolean>()
    val foundUserLocation : LiveData<Boolean>
        get() = _foundUserLocation

    private val _getLastEarthquakesExceptionMessage = MutableLiveData<String>()
    val getLastEarthquakesException: LiveData<String>
        get() = _getLastEarthquakesExceptionMessage

    private var resultsLimit = OverviewUtils.INITIAL_VALUES.RESULS_LIMIT

    private var minMagnitude: Int? = null

    private var maxMagnitude: Int? = null

    private var startTime: String? = null

    private var endTime: String? = null

    private var orderBy: String = OverviewUtils.INITIAL_VALUES.ORDER_BY

    private var maxRadiusKm = OverviewUtils.INITIAL_VALUES.MAX_RADIUS_KM

    private var latitude = OverviewUtils.FINAL_CONSTANTS.LATITUDE

    private var longitude = OverviewUtils.FINAL_CONSTANTS.LONGITUDE

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)



    fun getLatestEarthquakes() {
        checkSearchOptions()
        coroutineScope.launch {
            val getEarthquakesDeferred =
                UsgsApi.usgsApiService.getEarthquakes(OverviewUtils.FINAL_CONSTANTS.RESULTS_FORMAT,
                    latitude,
                    longitude,
                    maxRadiusKm,
                    minMagnitude,
                    maxMagnitude,
                    orderBy,
                    resultsLimit, startTime, endTime)
            try {
                val result = getEarthquakesDeferred.await()
                _earthquakes.value = result.features
                _isLoadingMoreResults.value = false

            } catch (e: Exception) {
                _getLastEarthquakesExceptionMessage.value = e.message
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

    fun displayEarthquakeDetailsCompleted() {
        _earthquakeToNavigateTo.value = null
    }

    fun getUserLocation() {
        if (OverviewUtils.userLocation == null) {
            val fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(application)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                OverviewUtils.userLocation = it
                latitude = OverviewUtils.userLocation!!.latitude
                longitude = OverviewUtils.userLocation!!.longitude
                _foundUserLocation.value = true
            }
                .addOnFailureListener {
                    _foundUserLocation.value = false
                }
        } else {
            latitude = OverviewUtils.userLocation!!.latitude
            longitude = OverviewUtils.userLocation!!.longitude
            _foundUserLocation.value = true
        }
    }

    private fun checkSearchOptions() {
        searchOptions?.let { searchOp ->
            searchOp.minMagnitude?.let { minMagnitude = it}
            searchOp.maxMagnitude?.let { maxMagnitude = it}
            searchOp.orderBy?.let { orderBy = it }
            searchOp.maxRadiusKm?.let { maxRadiusKm = it}
            searchOp.startTime?.let { startTime = it}
            searchOp.endTime?.let { endTime = it}
        }
    }


}