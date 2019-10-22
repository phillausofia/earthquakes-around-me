package com.earthquakesaroundme.search_options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class SearchOptionsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchOptionsViewModel::class.java)) {
            return SearchOptionsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}