package com.earthquakesaroundme.overview

import android.app.Application
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.earthquakesaroundme.search_options.SearchOptions
import java.lang.IllegalArgumentException

class OverviewViewModelFactory(
    private val searchOptions: SearchOptions?,
    private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(searchOptions, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}