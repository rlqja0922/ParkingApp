package com.example.parkinglrapp

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitCall {
    private val apiService = RetrofitApi.instance
    private val serviceKey = "HJlQAUW5QERUSqwXoOdBIIaec1t+pBVP8nQB8qIJTYxCNe2vWMi4jnxkSmT6AeLH7siS056E6DOIxRkjN4ILIA==" // 공공데이터포털에서 발급받은 서비스 키

    private fun fetchParkingData(page: Int, perPage: Int) {
        apiService.getParkingData(serviceKey, page, perPage,null,null,null).enqueue(object : Callback<ParkingDataResponse> {
            override fun onResponse(call: Call<ParkingDataResponse>, response: Response<ParkingDataResponse>) {
                if (response.isSuccessful) {
                    val parkingDataList = response.body()?.list ?: emptyList()
                    for (data in parkingDataList) {
                        Log.d("ParkingData", "주차장 이름: ${data.prkplceNm}, 도로명 주소: ${data.rdnmadr}")
                    }
                } else {
                    Log.e("ParkingData", "Response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ParkingDataResponse>, t: Throwable) {
                Log.e("ParkingData", "API 호출 실패: ${t.message}")
            }
        })
    }
}