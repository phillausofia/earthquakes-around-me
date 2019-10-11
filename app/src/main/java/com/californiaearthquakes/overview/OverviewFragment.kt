package com.californiaearthquakes.overview

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.californiaearthquakes.R
import com.californiaearthquakes.databinding.FragmentOverviewBinding
import com.californiaearthquakes.search_options.SearchOptions

class OverviewFragment: Fragment() {


    private val adapter: EarthquakeAdapter by lazy {
        EarthquakeAdapter()
    }

    private var searchOptions: SearchOptions? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        Log.i("onCreateView", "onCreateView method called")
        val binding: FragmentOverviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)

        binding.lifecycleOwner = this

        try {
            searchOptions = OverviewFragmentArgs.fromBundle(arguments!!).searchOptions
        } catch (e: Exception) {
        }

        val viewModelFactory = OverviewViewModelFactory(searchOptions)

        val viewModel = ViewModelProviders.of(this,
            viewModelFactory).get(OverviewViewModel::class.java)

        binding.viewModel = viewModel

        binding.earthquakesList.adapter = adapter

        //Add dividers to list
        binding.earthquakesList.addItemDecoration(DividerItemDecoration(this.context,
            DividerItemDecoration.VERTICAL))

        //When we retrieve a list of earthquakes, the progress bar is disabled
        viewModel.earthquakes.observe(this, Observer {
            list ->
            if (list.isNotEmpty()) {
                binding.mainProgressBar.visibility = View.GONE
            }
        })

        viewModel.isLoadingMoreResuls.observe(this, Observer {isLoading ->
            if (isLoading) {
                binding.scrollProgressBar.visibility = View.VISIBLE
            }
            else {
                binding.scrollProgressBar.visibility = View.GONE
            }
        })

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

                }
            }
        })
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_overflow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return NavigationUI.onNavDestinationSelected(item!!, view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }

}