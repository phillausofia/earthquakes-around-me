package com.californiaearthquakes.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.californiaearthquakes.R
import com.californiaearthquakes.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentDetailBinding>(inflater,
            R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this

        val earthquake = DetailFragmentArgs.fromBundle(arguments!!).earthquake

        val viewModelFactory = DetailViewModelFactory(earthquake)

        binding.textViewEarthquakeSummary.text = earthquake.place

        return binding.root

    }
}