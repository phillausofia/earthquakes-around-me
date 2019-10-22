package com.earthquakesaroundme.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.earthquakesaroundme.R
import com.earthquakesaroundme.Utils
import com.earthquakesaroundme.databinding.FragmentDetailBinding
import com.earthquakesaroundme.detail.DetailUtils.adSize
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: DetailViewModel
    private lateinit var mapView: MapView
    private lateinit var adView: AdView


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val binding = DataBindingUtil.inflate<FragmentDetailBinding>(inflater,
            R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this

        adView = AdView(context!!)

        if (binding.linearLayoutAd.isEmpty()) {
            adSize?.let {
                binding.linearLayoutAd.addView(adView)
                Log.i("onCreateView", "loading the Banner")
                loadBanner()
                loadAd()
            }
            adView.adListener = object: AdListener() {
                override fun onAdClosed() {
                    loadAd()
                }
            }
        }


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

    private fun loadBanner() {
        adView.adUnitId = Utils.ADAPTIVE_BANNER_AD_UNIT_ID

        adView.adSize = adSize
        adView.visibility = View.VISIBLE

    }

    private fun loadAd() {
        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val adRequest = AdRequest
            .Builder()
            .addTestDevice("5D768934B1FE279BA20FDDAAE2951F1F").build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
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

