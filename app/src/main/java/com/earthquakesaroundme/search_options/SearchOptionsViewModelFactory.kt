package com.earthquakesaroundme.search_options

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class SearchOptionsViewModelFactory(
    private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchOptionsViewModel::class.java)) {
            return SearchOptionsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}