package com.californiaearthquakes.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.californiaearthquakes.R
import com.californiaearthquakes.databinding.FragmentOverviewBinding

class OverviewFragment: Fragment() {

    private val viewModel: OverviewViewModule by lazy {
        ViewModelProviders.of(this).get(OverviewViewModule::class.java)
    }

    private val adapter: EarthquakeAdapter by lazy {
        EarthquakeAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentOverviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        binding.earthquakesList.adapter = adapter

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


        val context = this.context

        binding.earthquakesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val layoutManager = binding.earthquakesList.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    val scrollThreshold = 2
                    if (!(viewModel.isLoadingMoreResuls.value!!) && totalItemCount <= lastVisibleItemPosition + 1 + scrollThreshold) {
                        viewModel.getMoreResults()
                    }
                    Toast.makeText(context, "${totalItemCount} ${lastVisibleItemPosition} ${viewModel.resultsLimit}", Toast.LENGTH_SHORT).show()

                }
            }
        })

        return binding.root
    }
}