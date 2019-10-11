package com.californiaearthquakes.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.californiaearthquakes.search_options.SearchOptions
import java.lang.IllegalArgumentException

class OverviewViewModelFactory(
    private val searchOptions: SearchOptions?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            return OverviewViewModel(searchOptions) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}