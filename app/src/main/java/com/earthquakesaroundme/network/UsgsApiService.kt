package com.earthquakesaroundme.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private val BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/"

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(120, TimeUnit.SECONDS)
    .readTimeout(119, TimeUnit.SECONDS)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface UsgsApiService {

    @GET("query")
    fun getEarthquakes(@Query("format") format: String,
                       @Query("latitude") latitude: Double,
                       @Query("longitude") longitude: Double,
                       @Query("maxradiuskm") maxradiuskm: Int,
                       @Query("minmagnitude") minmagnitude: Int?,
                       @Query("maxmagnitude") maxmagnitude: Int?,
                       @Query("orderby") orderby: String,
                       @Query("limit") limit: Int,
                       @Query("starttime") starttime: String?,
                       @Query("endtime") endtime: String?
    ) : Deferred<Model.Result>


}

object UsgsApi {
    val usgsApiService: UsgsApiService by lazy {
        retrofit.create(UsgsApiService::class.java)
    }
}
