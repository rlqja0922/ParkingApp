package com.example.parkinglrapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.parkinglrapp.main.MainActivity
import java.util.Timer
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        val timer = Timer()
        timer.schedule(timerTask {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post {
                checkPermissions()  // 2초 후 실행
            }
        }, 2000)

    }
    fun startMain(){
        var intent : Intent = Intent(this@SplashActivity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
    private fun checkPermissions() {
        // 안드로이드 6.0 (API 23) 이상에서만 권한을 동적으로 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 권한이 허용되어 있는지 체크
            val permissionsNeeded = mutableListOf<String>()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.INTERNET)
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            // 필요한 권한이 있다면 요청
            if (permissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_CODE_PERMISSIONS)
            }else{
                startMain()
            }
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    for ((index, result) in grantResults.withIndex()) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            // 권한이 거부된 경우 처리
                            // 예: 권한이 거부되면 기능 제한 안내 메시지 표시
                            val deniedPermission = permissions[index]
                            println("권한이 거부됨: $deniedPermission")
                        }else{
                            startMain()
                        }
                    }
                }
            }
        }
    }
}