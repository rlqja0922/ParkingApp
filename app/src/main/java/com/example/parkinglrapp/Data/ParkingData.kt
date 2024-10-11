package com.example.parkinglrapp.Data


// ParkingData.kt
data class ParkingDataResponse(
    val list: List<ParkingData>
)

data class ParkingData(
    val prkplceNo: String,           // 주차장 고유 번호
    val prkplceNm: String,           // 주차장 이름
    val prkplceSe: String,           // 주차장 종류 (ex: 공영)
    val prkplceType: String,         // 주차장 형태 (ex: 노외)
    val rdnmadr: String?,            // 도로명 주소 (nullable)
    val lnmadr: String?,             // 지번 주소 (nullable)
    val prkcmprt: String,            // 주차 가능 대수
    val feedingSe: String,           // 급지 구분 (ex: 2)
    val enforceSe: String,           // 시행 여부 (ex: 미시행)
    val operDay: String,             // 운영 요일 (ex: 평일+토요일+공휴일)
    val weekdayOperOpenHhmm: String, // 평일 운영 시작 시간 (ex: 00:00)
    val weekdayOperColseHhmm: String,// 평일 운영 종료 시간 (ex: 23:59)
    val satOperOperOpenHhmm: String, // 토요일 운영 시작 시간 (ex: 00:00)
    val satOperCloseHhmm: String,    // 토요일 운영 종료 시간 (ex: 23:59)
    val holidayOperOpenHhmm: String, // 공휴일 운영 시작 시간 (ex: 00:00)
    val holidayCloseOpenHhmm: String,// 공휴일 운영 종료 시간 (ex: 23:59)
    val parkingchrgeInfo: String,    // 주차 요금 정보 (ex: 무료)
    val basicTime: String,           // 기본 시간 (ex: 0)
    val basicCharge: String?,        // 기본 요금 (nullable)
    val addUnitTime: String?,        // 추가 시간 단위 (nullable)
    val addUnitCharge: String?,      // 추가 요금 단위 (nullable)
    val dayCmmtktAdjTime: String?,   // 1일 주차권 조정 시간 (nullable)
    val dayCmmtkt: String?,          // 1일 주차권 요금 (nullable)
    val monthCmmtkt: String?,        // 월 정기 주차권 요금 (nullable)
    val metpay: String?,             // 결제 방식 (nullable)
    val spcmnt: String?,             // 특이 사항 (nullable)
    val institutionNm: String,       // 관리 기관 이름
    val phoneNumber: String,         // 전화번호
    val latitude: String,            // 위도
    val longitude: String,           // 경도
    val pwdbsPpkZoneYn: String?,     // 장애인 주차 구역 여부 (nullable)
    val referenceDate: String,       // 기준 일자
    val insttCode: String            // 기관 코드
)