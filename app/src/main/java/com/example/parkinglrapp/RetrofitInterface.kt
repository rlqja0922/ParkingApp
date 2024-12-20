package com.example.parkinglrapp

import com.example.parkinglrapp.Data.ParkingDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("tn_pubr_prkplce_info_api")
    fun getParkingData(
        @Query("serviceKey") serviceKey: String, // 공공데이터포털에서 발급된 API 키
        @Query("pageNo") page: Int,                // 페이지 번호
        @Query("numOfRows") perPage: Int,           // 한 페이지에 보여줄 데이터 수
        @Query("latitude") latitude: Long?,      //위도
        @Query("longitude") longitude: Long?,     //경도
        @Query("instt_code") instt_code: String?, //제공기관코드(ex: 경상남도 창녕군청)
        @Query("type")type:String = "JSON"
    ): Call<ParkingDataResponse>
}