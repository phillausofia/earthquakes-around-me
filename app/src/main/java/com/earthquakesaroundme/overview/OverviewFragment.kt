package com.earthquakesaroundme.overview

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthquakesaroundme.R
import com.earthquakesaroundme.databinding.FragmentOverviewBinding
import com.earthquakesaroundme.search_options.SearchOptions
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*

class OverviewFragment: Fragment() {


    private var searchOptions: SearchOptions? = null
    private var radiusCenterPointLocation: Location? = null
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: FragmentOverviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)

        binding.lifecycleOwner = this

        try {
            searchOptions = OverviewFragmentArgs.fromBundle(arguments!!).searchOptions
        } catch (e: Exception) {
        }

        if (radiusCenterPointLocation == null ) {
            coroutineScope.launch {
                radiusCenterPointLocation =
                    async{ getCurrentLocation()}.await()
            }
        }
        Log.i("onCreateView", "We got location")
        val viewModelFactory = OverviewViewModelFactory(searchOptions, radiusCenterPointLocation)

        val viewModel = ViewModelProviders.of(this,
            viewModelFactory).get(OverviewViewModel::class.java)

        binding.viewModel = viewModel

        val adapter = EarthquakeAdapter(EarthquakeAdapter.OnClickListener {
            viewModel.displayEarthquakeDetails(it)
        })

        binding.earthquakesList.adapter = adapter



        //Add dividers to list
        binding.earthquakesList.addItemDecoration(DividerItemDecoration(this.context,
            DividerItemDecoration.VERTICAL))

        //When we retrieve a list of earthquakes, the progress bar is disabled
        viewModel.earthquakes.observe(this, Observer {
            list ->
            if (list.isNotEmpty()) {
                binding.mainProgressBar.visibility = View.GONE
            } else {
                binding.apply {
                    earthquakesList.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    mainProgressBar.visibility = View.GONE
                }
            }
        })


        viewModel.isLoadingMoreResuls.observe(this, Observer {isLoading ->
            if (isLoading) {
                adapter.insertProgressView()
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


        viewModel.earthquakeToNavigateTo.observe(this, Observer {
            if (it != null) {

                this.findNavController().navigate(OverviewFragmentDirections.actionShowEarthquakeDetails(it))
                viewModel.displayEartquakeDetailsCompleted()
            }
        })


        setHasOptionsMenu(true)



        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_overflow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                ||super.onOptionsItemSelected(item)
    }


    suspend fun getCurrentLocation(): Location? {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        return withContext(Dispatchers.Main) {
            val task = fusedLocationProviderClient.lastLocation
            while (!task.isSuccessful) {
                delay(1000)
            }
            Log.i("return location", "${task.result!!.latitude}")
            return@withContext task.result
        }
    }




}