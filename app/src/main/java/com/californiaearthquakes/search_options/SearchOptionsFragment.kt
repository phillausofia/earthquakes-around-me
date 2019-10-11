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
                    .actionSearchOptionsFragmentToOverviewFragment(SearchOptions(
                        binding.numberPickerMinMagnitude.value,
                        binding.numberPickerMaxMagnitude.value
                    )))
        }

        return binding.root
    }

}