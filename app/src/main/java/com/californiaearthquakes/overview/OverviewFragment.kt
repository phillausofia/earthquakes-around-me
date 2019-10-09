package com.californiaearthquakes.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.californiaearthquakes.R
import com.californiaearthquakes.databinding.FragmentOverviewBinding

class OverviewFragment: Fragment() {

    private val viewModel: OverviewViewModule by lazy {
        ViewModelProviders.of(this).get(OverviewViewModule::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentOverviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        binding.earthquakesList.adapter = EarthquakeAdapter()

        //Add dividers to list
        binding.earthquakesList.addItemDecoration(DividerItemDecoration(this.context,
            DividerItemDecoration.VERTICAL))

        //When we retrieve a list of earthquakes, the progress bar is disabled
        viewModel.earthquakes.observe(this, Observer {
            list ->
            if (list.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
            }
        })

        return binding.root
    }
}