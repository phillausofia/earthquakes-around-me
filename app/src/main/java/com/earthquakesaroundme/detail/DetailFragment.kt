package com.earthquakesaroundme.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.earthquakesaroundme.R
import com.earthquakesaroundme.databinding.FragmentDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: DetailViewModel
    private lateinit var mapView: MapView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentDetailBinding>(inflater,
            R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this

        val earthquake = DetailFragmentArgs.fromBundle(arguments!!).earthquake

        val viewModelFactory = DetailViewModelFactory(earthquake)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(DetailViewModel::class.java)

        binding.viewModel = viewModel

        mapView = binding.earthquakeLocationMapView

        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)


        return binding.root

    }

    override fun onMapReady(p0: GoogleMap?) {
        val (lat, lng) = viewModel.getEarthquakeCoordinates()
        p0!!.addMarker(MarkerOptions().position(LatLng(lat, lng)).title(viewModel.
            selectedEarthquake.value!!.properties.place))
        p0.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}

