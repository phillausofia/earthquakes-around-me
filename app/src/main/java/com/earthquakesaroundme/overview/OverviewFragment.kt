package com.earthquakesaroundme.overview

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

class OverviewFragment: Fragment() {

    private val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 666
    private var searchOptions: SearchOptions? = null
    private lateinit var binding: FragmentOverviewBinding
    private lateinit var viewModel: OverviewViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)

        binding.lifecycleOwner = this

        try {
            searchOptions = OverviewFragmentArgs.fromBundle(arguments!!).searchOptions
        } catch (e: Exception) {

        }


        val viewModelFactory = OverviewViewModelFactory(searchOptions, application)

        viewModel = ViewModelProviders.of(this,
            viewModelFactory).get(OverviewViewModel::class.java)

        binding.viewModel = viewModel

        //Checking if we have permission to access user's location
        if (ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            //Request the permission
            requestPermissions(arrayOf(
                Manifest.permission
                    .ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_COARSE_LOCATION)
        } else {
            viewModel.getUserLocation()
        }


        val adapter = EarthquakeAdapter(EarthquakeAdapter.OnClickListener {
            viewModel.displayEarthquakeDetails(it)
        })

        binding.earthquakesList.adapter = adapter



        //Add dividers to list
        binding.earthquakesList.addItemDecoration(DividerItemDecoration(this.context,
            DividerItemDecoration.VERTICAL))

        //If we have the user's location then we can get the list of earthquakes
        viewModel.foundUserLocation.observe(this, Observer { isLocationFound ->
            if (isLocationFound) {
                viewModel.getLatestEarthquakes()
            }
            else {
                binding.apply {
                    mainProgressBar.visibility = View.GONE
                    emptyView.text = "We couldn't find your location."
                    emptyView.visibility = View.VISIBLE
                }
            }
        })

        //When we retrieve a list of earthquakes, the progress bar is disabled
        viewModel.earthquakes.observe(this, Observer {
            list ->
            if (list.isNotEmpty()) {
                binding.mainProgressBar.visibility = View.GONE
                binding.earthquakesList.visibility = View.VISIBLE
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
                viewModel.displayEarthquakeDetailsCompleted()
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


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        Log.i("onRequestPermResult", "request code: ${requestCode}")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_COARSE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED)) {
                    viewModel.getUserLocation()
                } else {
                    binding.mainProgressBar.visibility = View.GONE
                    binding.emptyView.apply {
                        text = "Permission denied. We need your location to show the earthquakes."
                        visibility = View.VISIBLE
                    }
                }
                return
            }
            else -> {}
        }
    }
}