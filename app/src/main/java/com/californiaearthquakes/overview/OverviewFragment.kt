package com.californiaearthquakes.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.californiaearthquakes.databinding.FragmentOverviewBinding

class OverviewFragment: Fragment() {

    private val viewModel: OverviewViewModule by lazy {
        ViewModelProviders.of(this).get(OverviewViewModule::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentOverviewBinding.inflate(inflater)

        binding.setLifecycleOwner(this)

        return binding.root
    }
}