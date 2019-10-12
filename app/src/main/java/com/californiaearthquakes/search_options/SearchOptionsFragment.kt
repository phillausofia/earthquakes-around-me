package com.californiaearthquakes.search_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.checkBoxMinMag.setOnClickListener {
            binding.numberPickerMinMagnitude.apply {
                visibility = if (binding.checkBoxMinMag.isChecked) View.VISIBLE else
                    View.GONE
            }
        }
        binding.checkBoxMaxMag.setOnClickListener {
            binding.numberPickerMaxMagnitude.apply {
                visibility = if (binding.checkBoxMaxMag.isChecked) View.VISIBLE else
                    View.GONE
            }
        }
        binding.checkBoxOrderBy.setOnClickListener {
            binding.orderBySearchOptionsRadioGroup.apply {
                visibility = if (binding.checkBoxOrderBy.isChecked) View.VISIBLE else
                    View.GONE
            }
        }

        binding.numberPickerMinMagnitude.apply {
            minValue = 0
            maxValue = 10
            wrapSelectorWheel = true
        }

        binding.numberPickerMaxMagnitude.apply {
            minValue = 0
            maxValue = 10
            wrapSelectorWheel = true
        }

        binding.buttonSearch.setOnClickListener{view ->
            view.findNavController()
                .navigate(SearchOptionsFragmentDirections
                    .actionSearchOptionsFragmentToOverviewFragment(getSearchOptions(
                        binding
                    )))
        }


        return binding.root
    }

    private fun getSearchOptions(
        binding: FragmentSearchOptionsBinding) : SearchOptions {

        val minMag = if (binding.checkBoxMinMag.isChecked)
            binding.numberPickerMinMagnitude.value else null
        val maxMag = if (binding.checkBoxMaxMag.isChecked)
            binding.numberPickerMaxMagnitude.value else null
        val orderBy = if (binding.checkBoxOrderBy.isChecked)
            getOrderBySearchOptions(binding) else null
        return SearchOptions(minMag, maxMag, orderBy)

    }

    private fun getOrderBySearchOptions(binding: FragmentSearchOptionsBinding) =
        when(binding.orderBySearchOptionsRadioGroup.checkedRadioButtonId) {
            binding.timeDescRadioButton.id -> "time"
            binding.timeAscRadioButton.id -> "time-asc"
            binding.magDescRadioButton.id -> "magnitude"
            binding.magAscRadioButton.id -> "magnitude-asc"
            else -> null
        }

}