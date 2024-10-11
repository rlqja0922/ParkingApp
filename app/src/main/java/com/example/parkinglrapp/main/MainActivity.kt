package com.example.parkinglrapp.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.parkinglrapp.R
import com.example.parkinglrapp.RetrofitApi
import com.example.parkinglrapp.RetrofitCall
import com.example.parkinglrapp.utills.GpsInfo
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    lateinit var gps : GpsInfo
    var lat : Long? = null
    var long : Long? = null
    lateinit var context : Context
    private val parkingViewModel: RetrofitCall by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        context = this
        gps = GpsInfo(this)
        if (gps.checkGPS){
            lat = gps.latitude.toLong()
            long = gps.longitude.toLong()
        }

    }

    override fun onResume() {
        super.onResume()
        if (gps.checkNetwork){
            if (lat != null && long != null) {
                parkingApiGetAdd(lat!!, long!!)
            }else{
                Toast.makeText(context,"GPS값이 없습니다. 확인해주세요.",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun parkingApiGetAdd(lat : Long,long : Long){
        parkingViewModel.parkingData.observe(this, Observer { parkingDataList ->
            // UI 업데이트 예시: 로그 출력
            for (data in parkingDataList) {
                Log.d("ParkingData", "주차장 이름: ${data.prkplceNm}, 도로명 주소: ${data.rdnmadr}")
            }
        })

        // 에러 메시지 관찰
        parkingViewModel.error.observe(this, Observer { errorMessage ->
            Log.e("ParkingData", errorMessage)
        })

        // 주차장 데이터 가져오기
        parkingViewModel.fetchParkingAddData(page = 0, lat = lat, long = long)  // 예시 좌표
    }
}