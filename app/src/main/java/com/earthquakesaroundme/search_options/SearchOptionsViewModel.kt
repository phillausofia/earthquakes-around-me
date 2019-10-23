package com.earthquakesaroundme.search_options

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchOptionsViewModel(): ViewModel() {

    private val _isCheckBoxMinMagChecked = MutableLiveData<Boolean>()
    val isCheckBoxMinMagChecked: LiveData<Boolean>
        get() = _isCheckBoxMinMagChecked

    private val _isCheckBoxMaxMagChecked = MutableLiveData<Boolean>()
    val isCheckBoxMaxMagChecked: LiveData<Boolean>
        get() = _isCheckBoxMaxMagChecked

    private val _isCheckBoxOrderByChecked = MutableLiveData<Boolean>()
    val isCheckBoxOrderByChecked: LiveData<Boolean>
        get() = _isCheckBoxOrderByChecked

    private val _isCheckBoxRadiusChecked = MutableLiveData<Boolean>()
    val isCheckBoxRadiusChecked: LiveData<Boolean>
        get() = _isCheckBoxRadiusChecked

    private val _isCheckBoxStartDateChecked = MutableLiveData<Boolean>()
    val isCheckBoxStartDateChecked: LiveData<Boolean>
        get() = _isCheckBoxStartDateChecked

    private val _isCheckBoxEndDateChecked = MutableLiveData<Boolean>()
    val isCheckBoxEndDateChecked: LiveData<Boolean>
        get() = _isCheckBoxEndDateChecked

    private val _minMag = MutableLiveData<Int?>()
    val minMag: LiveData<Int?>
        get() = _minMag

    private val _maxMag = MutableLiveData<Int?>()
    val maxMag: LiveData<Int?>
        get() = _maxMag

    private val _expandDescriptionText = MutableLiveData<Boolean>(false)
    val expandDescriptionText: LiveData<Boolean>
        get() = _expandDescriptionText

    val setExpandDescriptionText = { value: Boolean -> _expandDescriptionText.value = value}

    val setMinMag = { value: Int? -> _minMag.value = value}

    val setMaxMag = { value: Int? -> _maxMag.value = value}


    val setIsCheckBoxRadiusChecked = { value: Boolean -> _isCheckBoxRadiusChecked.value = value }

    val setIsCheckBoxMinMagChecked = { value: Boolean ->_isCheckBoxMinMagChecked.value = value }

    val setIsCheckBoxMaxMagChecked = { value: Boolean -> _isCheckBoxMaxMagChecked.value = value }

    val setIsCheckBoxOrderByChecked = { value: Boolean -> _isCheckBoxOrderByChecked.value = value }

    val setIsCheckBoxStartDateChecked = { value: Boolean -> _isCheckBoxStartDateChecked.value = value }

    val setIsCheckBoxEndDateChecked = { value: Boolean -> _isCheckBoxEndDateChecked.value = value }
}