package com.example.parkinglrapp.Data

import android.os.Parcelable
import java.io.Serializable


// ParkingData.kt
data class ParkingDataResponse(
    val response: Response
)

data class Response(
    val header: Header,
    val body: Body
)

data class Header(
    val resultCode: String,
    val resultMsg: String,
    val type: String
)

data class Body(
    val items: List<ParkingItem>
)

data class ParkingItem(
    val prkplceNo: String,
    val prkplceNm: String,
    val prkplceSe: String,
    val prkplceType: String,
    val rdnmadr: String,
    val lnmadr: String,
    val prkcmprt: String,
    val feedingSe: String,
    val enforceSe: String,
    val operDay: String,
    val latitude: String,
    val longitude:String,
    val institutionNm:String,
    val parkingchrgeInfo:String
): Serializable