package com.example.parkinglrapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.parkinglrapp.Data.ParkingData
import com.example.parkinglrapp.Data.ParkingDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitCall : ViewModel() {

    // 주차장 데이터를 담을 MutableLiveData
    private val _parkingData = MutableLiveData<List<ParkingData>>()
    val parkingData: LiveData<List<ParkingData>> get() = _parkingData

    // API 호출에 실패했을 때의 에러 메시지를 담을 MutableLiveData
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val apiService = RetrofitApi.instance
    private val serviceKey = "HJlQAUW5QERUSqwXoOdBIIaec1t+pBVP8nQB8qIJTYxCNe2vWMi4jnxkSmT6AeLH7siS056E6DOIxRkjN4ILIA==" // 공공데이터포털에서 발급받은 서비스 키
    private val perPage = 20

    private fun fetchParkingData(page: Int) {
        apiService.getParkingData(serviceKey, page, perPage,null,null,null).enqueue(object : Callback<ParkingDataResponse> {
            override fun onResponse(call: Call<ParkingDataResponse>, response: Response<ParkingDataResponse>) {
                if (response.isSuccessful) {
                    val parkingDataList = response.body()?.list ?: emptyList()
                    _parkingData.postValue(parkingDataList)  // 성공 시 LiveData에 값 저장
                    for (data in parkingDataList) {
                        Log.d("ParkingData", "주차장 이름: ${data.prkplceNm}, 도로명 주소: ${data.rdnmadr}")
                    }
                } else {
                    _error.postValue("Response failed with code: ${response.code()}")
                    Log.e("ParkingData", "Response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ParkingDataResponse>, t: Throwable) {
                Log.e("ParkingData", "API 호출 실패: ${t.message}")
                _error.postValue("API 호출 실패: ${t.message}")
            }
        })
    }
    fun fetchParkingAddData(page: Int,lat : Long, long: Long) {
        apiService.getParkingData(serviceKey, page, perPage,lat,long,null).enqueue(object : Callback<ParkingDataResponse> {
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