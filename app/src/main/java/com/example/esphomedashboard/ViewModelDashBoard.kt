package com.example.esphomedashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.time.Duration.Companion.milliseconds

class ViewModelDashBoard() : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState.asStateFlow()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApi: WeatherApiService = retrofit.create(WeatherApiService::class.java)


  /*  init {
        startWeatherPolling()
    }
*/
    private fun startWeatherPolling() {
        viewModelScope.launch {
            while (true) {
                loadWeather()
                // ждем 4 часа перед следующим запросом
                delay((4 * 60 * 60 * 1000L).milliseconds)
            }
        }
    }
    fun loadWeather() {
        viewModelScope.launch {
            try {
                val response = weatherApi.getForecast(
                    latitude = 54.981443,
                    longitude = 82.785188
                )
                _weatherState.value = response
            } catch (e: Exception) {
                println("Ошибка при загрузке погоды: ${e.localizedMessage}")
            }
        }
    }
}