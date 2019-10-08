package com.californiaearthquakes

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.californiaearthquakes.network.Model.Earthquake
import com.californiaearthquakes.overview.EarthquakeAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Earthquake>?) {
    val adapter = recyclerView.adapter as EarthquakeAdapter
    adapter.submitList(data)
}

@BindingAdapter("place")
fun TextView.bindPlace(place: String) {
    text = place
}

@BindingAdapter("time")
fun TextView.bindTime(time: Long) {
    text = SimpleDateFormat("yyyy-MM-dd h:mm:ss a", Locale.US)
        .format(Date(time))
}

@BindingAdapter("mag")
fun TextView.bindMag(mag: Double) {
    text = mag.toString()
}