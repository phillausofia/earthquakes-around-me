package com.californiaearthquakes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.californiaearthquakes.network.Model
import com.californiaearthquakes.network.UsgsApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        coroutineScope.launch {
            val getEarthquakesDeffered =
                UsgsApi.usgsApiService.getLastTenEarthquakes("geojson",
                    36.778259,
                    -119.417931,
                    500.0,
                    "time",
                    10)
            try {
                val listResult = getEarthquakesDeffered.await()
                main_text_view.text = listResult.features[5].properties.place
            } catch (e: Exception) {
                main_text_view.text = e.message
            }
        }

    }


}
