package com.californiaearthquakes.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.californiaearthquakes.network.Model
import java.lang.IllegalArgumentException

class DetailViewModelFactory(
    private val earthquake: Model.Earthquake
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(earthquake) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}