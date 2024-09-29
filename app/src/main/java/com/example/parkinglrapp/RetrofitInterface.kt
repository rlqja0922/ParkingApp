package com.example.parkinglrapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("tn_pubr_prkplce_info_api")
    fun getParkingData(
        @Query("serviceKey") serviceKey: String, // 공공데이터포털에서 발급된 API 키
        @Query("page") page: Int,                // 페이지 번호
        @Query("perPage") perPage: Int           // 한 페이지에 보여줄 데이터 수
    ): Call<ParkingDataResponse>
}