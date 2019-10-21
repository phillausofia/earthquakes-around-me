package com.earthquakesaroundme

import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.earthquakesaroundme.detail.DetailUtils
import com.earthquakesaroundme.network.EarthquakeIntensity
import com.earthquakesaroundme.network.Model.Earthquake
import com.earthquakesaroundme.overview.EarthquakeAdapter
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("layout_height")
fun setLayoutHeight(view: View, height: Int) {
    val layoutParams = view.layoutParams
    layoutParams.height = (height * DetailUtils.density!!).toInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Earthquake>?) {
    val adapter = recyclerView.adapter as EarthquakeAdapter
    adapter.addDataAndSubmitList(data)
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
fun TextView.bindMag(mag: Double?) {
    text = mag?.toString()
}

@BindingAdapter("setBackground")
fun ConstraintLayout.setBackgroundDependingOnIntensity(intensity: EarthquakeIntensity) {
    intensity.let {
        setBackgroundColor(when (it) {
            EarthquakeIntensity.NOT_EVEN_AN_EARTHQUAKE -> resources.getColor(R.color.color_not_even_an_earthquake)
            EarthquakeIntensity.LOW -> resources.getColor(R.color.color_low_earthquake)
            EarthquakeIntensity.MED -> resources.getColor(R.color.color_med_earthquake)
            else -> resources.getColor(R.color.color_high_earthquake)
        })
    }
}