package com.californiaearthquakes.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.californiaearthquakes.databinding.FragmentOverviewBinding

class OverviewFragment: Fragment() {

    private val viewModel: OverviewViewModule by lazy {
        ViewModelProviders.of(this).get(OverviewViewModule::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentOverviewBinding.inflate(inflater)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        binding.earthquakesList.adapter = EarthquakeAdapter()

        //Add dividers to list
        binding.earthquakesList.addItemDecoration(DividerItemDecoration(this.context,
            DividerItemDecoration.VERTICAL))
        return binding.root
    }
}