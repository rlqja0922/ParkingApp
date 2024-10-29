package com.example.parkinglrapp

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
object RetrofitApi {

    private const val BASE_URL = "http://api.data.go.kr/openapi/" // 실제 API URL

    val instance: RetrofitInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitInterface::class.java)
    }

}