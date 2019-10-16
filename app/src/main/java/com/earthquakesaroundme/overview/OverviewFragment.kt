package com.earthquakesaroundme.overview

import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.earthquakesaroundme.R
import com.earthquakesaroundme.databinding.FragmentOverviewBinding
import com.earthquakesaroundme.search_options.SearchOptions

class OverviewFragment: Fragment() {


    private var searchOptions: SearchOptions? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

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

        //We save the initial padding value so we can use it latter
        val paddingBottom = binding.earthquakesList.paddingBottom

        viewModel.isLoadingMoreResuls.observe(this, Observer {isLoading ->
            if (isLoading) {
                binding.scrollProgressBar.visibility = View.VISIBLE
                binding.earthquakesList.setPadding(0, 0, 0,
                    paddingBottom) //takes in px, not dp
            }
            else {
                binding.scrollProgressBar.visibility = View.GONE
                binding.earthquakesList.updatePadding(0, 0, 0, 0)
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

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater?.inflate(R.menu.menu_overflow, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        return NavigationUI.onNavDestinationSelected(item!!, view!!.findNavController())
//                || super.onOptionsItemSelected(item)
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_overflow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                ||super.onOptionsItemSelected(item)
    }
}