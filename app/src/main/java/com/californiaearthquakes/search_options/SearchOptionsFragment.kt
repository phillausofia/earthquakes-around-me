package com.californiaearthquakes.search_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.californiaearthquakes.R
import com.californiaearthquakes.databinding.FragmentSearchOptionsBinding



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


        binding.buttonSearch.setOnClickListener{view ->
            view.findNavController()
                .navigate(SearchOptionsFragmentDirections
                    .actionSearchOptionsFragmentToOverviewFragment(getSearchOptions(
                        binding
                    )))
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
        return SearchOptions(minMag, maxMag, orderBy, maxRadiusKm)

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
}