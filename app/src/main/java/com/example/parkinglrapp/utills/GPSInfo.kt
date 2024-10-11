package com.example.parkinglrapp.utills

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.Locale

class GpsInfo(private val mContext: Context) :
    LocationListener {
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 0
    private val MIN_TIME_BW_UPDATES: Long = 0
    protected var locationManager: LocationManager? = null
    var checkGPS = false
    var isGPSEnabled = false
    var checkNetwork = false
    var loc: Location? = null
    var latitude = 0.0
    var longitude = 0.0

    init {
        location
    }

    // 권한이 없는 경우 위치 요청을 하지 않음
    val location: Location?
        get() {
            try {
                if (ContextCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    // 권한이 없는 경우 위치 요청을 하지 않음
                    return null
                }
                locationManager =
                    mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                checkGPS = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                checkNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (!checkGPS && !checkNetwork) {
                }
                if (checkNetwork) {
                    try {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("Network", "Network")
                        if (locationManager != null) {
                            loc =
                                locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        }
                        if (loc != null) {
                            latitude = loc!!.latitude
                            longitude = loc!!.longitude
                        }
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
                if (checkGPS) {
                    if (loc == null) {
                        try {
                            locationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                            )
                            Log.d("GPS Enabled", "GPS Enabled")
                            if (locationManager != null) {
                                loc =
                                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (loc != null) {
                                    latitude = loc!!.latitude
                                    longitude = loc!!.longitude
                                }
                            }
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return loc
        }

    fun getLongitude(): Double {
        if (loc != null) {
            longitude = loc!!.longitude
        }
        return longitude
    }

    fun getLatitude(): Double {
        if (loc != null) {
            latitude = loc!!.latitude
        }
        return latitude
    }

    val isGpsEnabled: Boolean
        get() {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            return isGPSEnabled
        }
    val isGpsNetworkEnabled: Boolean
        get() {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            checkNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return isGPSEnabled && checkNetwork
        }

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this)
        }
    }

    override fun onLocationChanged(location: Location) {
        loc = location
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(provider: String) {
        Log.d("gps","gps enabled")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("gps","gps Disabled")
    }

    fun getCountryAndCity(latitude: Double, longitude: Double): Array<String?> {
        val geocoder: Geocoder
        geocoder = Geocoder(mContext, Locale.getDefault())
        val strings = arrayOf<String?>("대한민국", "서울특별시", "대한민국 서울특별시")
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {
                val address = addresses[0]
                // 나라와 도시 정보 얻기
                val country = address.countryName
                var city = address.locality
                if (city == null) {
                    city = address.subAdminArea // 도시에 가까운 행정 구역
                    if (city == null) {
                        city = address.adminArea // 더 넓은 지역, 예를 들어 주나 도
                    }
                }
                val all = address.getAddressLine(0)
                strings[0] = country
                strings[1] = city
                strings[2] = all
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return strings
        }
        return strings
    }
}