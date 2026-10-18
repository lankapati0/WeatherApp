package com.example.weatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query


data class GeocodingResponse(
    val results: List<GeocodingResult>?
)

data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?
)

interface GeocodingApiService{

    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") cityName:String,
        @Query("count") count:Int = 5,
        @Query("language") language:String = "en"
    ):GeocodingResponse
}
