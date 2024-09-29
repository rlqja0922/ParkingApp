package com.example.parkinglrapp


// ParkingData.kt
data class ParkingDataResponse(
    val list: List<ParkingData>
)

data class ParkingData(
    val parkingName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val parkingSpaces: Int
)