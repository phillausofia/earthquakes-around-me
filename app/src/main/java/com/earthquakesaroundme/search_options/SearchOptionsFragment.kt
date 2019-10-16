package com.earthquakesaroundme.search_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.earthquakesaroundme.R
import com.earthquakesaroundme.databinding.FragmentSearchOptionsBinding
import java.text.SimpleDateFormat
import java.util.*


class SearchOptionsFragment: Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val binding = DataBindingUtil.inflate<FragmentSearchOptionsBinding>(inflater,
            R.layout.fragment_search_options, container, false)

        /*
        The search options for a parameter will appear only if the user wants to set that
        specific search parameter
         */
        makeViewVisibleDependingOnCheckBox(binding.numberPickerMinMagnitude, binding.checkBoxMinMag)
        makeViewVisibleDependingOnCheckBox(binding.numberPickerMaxMagnitude, binding.checkBoxMaxMag)
        makeViewVisibleDependingOnCheckBox(binding.orderBySearchOptionsRadioGroup, binding.checkBoxOrderBy)
        makeViewVisibleDependingOnCheckBox(binding.editTextMaxRadiusKm, binding.checkBoxMaxRadiusKm)
        makeViewVisibleDependingOnCheckBox(binding.editTextStartDate, binding.checkBoxStartDate)
        makeViewVisibleDependingOnCheckBox(binding.editTextEndDate, binding.checkBoxEndDate)


        //Initializing the number pickers
        binding.numberPickerMinMagnitude.initializeNumberPicker()
        binding.numberPickerMaxMagnitude.initializeNumberPicker()


        binding.buttonSearch.setOnClickListener { view ->
            val searchOptions = getSearchOptions(binding)
            if (inputDataIsCorrect(searchOptions.maxRadiusKm,
                    searchOptions.startTime,
                    searchOptions.endTime)) {
                view.findNavController()
                    .navigate(
                        SearchOptionsFragmentDirections
                            .actionSearchOptionsFragmentToOverviewFragment(searchOptions)
                    )
            }
        }


        return binding.root
    }

    private fun NumberPicker.initializeNumberPicker() {
        minValue = SearchOptionsUtils.EARTHQUAKE_MAGNITUDE_RANGE.MIN_MAGNITUDE
        maxValue = SearchOptionsUtils.EARTHQUAKE_MAGNITUDE_RANGE.MAX_MAGNITUDE
        wrapSelectorWheel = true
    }

    private fun getSearchOptions(
        binding: FragmentSearchOptionsBinding) : SearchOptions {

        val minMag = if (binding.checkBoxMinMag.isChecked)
            binding.numberPickerMinMagnitude.value else null
        val maxMag = if (binding.checkBoxMaxMag.isChecked)
            binding.numberPickerMaxMagnitude.value else null
        val orderBy = if (binding.checkBoxOrderBy.isChecked)
            getOrderBySearchOption(binding) else null
        val maxRadiusKm = if (binding.checkBoxMaxRadiusKm.isChecked)
            Integer.parseInt(binding.editTextMaxRadiusKm.text.toString()) else null
        val startTime = if (binding.checkBoxStartDate.isChecked)
            binding.editTextStartDate.text.toString().trim() else null
        val endTime = if (binding.checkBoxEndDate.isChecked)
            binding.editTextEndDate.text.toString().trim() else null
        return SearchOptions(minMag, maxMag, orderBy, maxRadiusKm, startTime, endTime)

    }

    private fun getOrderBySearchOption(binding: FragmentSearchOptionsBinding) =
        when(binding.orderBySearchOptionsRadioGroup.checkedRadioButtonId) {
            binding.timeAscRadioButton.id -> SearchOptionsUtils.UsgsApiOrderByValues.TIME_ASC
            binding.magDescRadioButton.id -> SearchOptionsUtils.UsgsApiOrderByValues.MAG
            binding.magAscRadioButton.id -> SearchOptionsUtils.UsgsApiOrderByValues.MAG_ASC
            else -> null
        }

    private fun makeViewVisibleDependingOnCheckBox(viewToBecameVisibile: View, checkBox: CheckBox) {
        checkBox.setOnClickListener {
            viewToBecameVisibile.apply {
                visibility = if (checkBox.isChecked) View.VISIBLE else View.GONE
            }
        }

    }

    private fun inputDataIsCorrect(maxRadiusKm: Int?, startTime: String?, endTime: String?): Boolean {
        if (maxRadiusKm != null) {
            if (maxRadiusKm !in 0..20_000) {
                Toast.makeText(this.context, "Radius should be between 0 and 20 000.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (startTime != null) {
            if (!isDateCorrect(startTime)) {
                Toast.makeText(this.context, "Start date invalid. Examples of correct dates: 2000-01-01, 2018-12-31.",
                    Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (endTime != null) {
            if (!isDateCorrect(endTime)) {
                Toast.makeText(this.context, "End date invalid. Examples of correct dates: 2000-01-01, 2018-12-31.",
                    Toast.LENGTH_SHORT).show()
                return false
            }
            val endDate = toDate(endTime)
            val startDate = if (startTime != null) toDate(startTime) else
                Date(Date().time - 30L * 24 * 3600 * 1000)
            if (!endDate.after(startDate)) {
                Toast.makeText(this.context, "End date should be after start date.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun isDateCorrect(dateString: String): Boolean {

        return try {
            toDate(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun toDate(dateString: String) : Date {
        try {
            return SimpleDateFormat("yyyy-MM-dd").apply {
                isLenient = false
            }.parse(dateString)
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
    }
}