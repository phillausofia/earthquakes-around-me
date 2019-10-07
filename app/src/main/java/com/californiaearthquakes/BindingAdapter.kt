package com.californiaearthquakes

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.californiaearthquakes.network.Model.Earthquake
import com.californiaearthquakes.overview.EarthquakeAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Earthquake>?) {
    val adapter = recyclerView.adapter as EarthquakeAdapter
    adapter.submitList(data)
}

@BindingAdapter("place")
fun bindPlace(txtView: TextView, place: String) {
    txtView.text = place
}

@BindingAdapter("time")
fun bindTime(txtView: TextView, time: String) {
    txtView.text = time
}

@BindingAdapter("mag")
fun bindMag(txtView: TextView, mag: String) {
    txtView.text = mag
}