package com.example.parkinglrapp.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.parkinglrapp.R
import com.example.parkinglrapp.RetrofitCall
import com.example.parkinglrapp.utills.GpsInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.parkinglrapp.Account.MypageFragment
import com.example.parkinglrapp.Data.ParkingItem
import com.example.parkinglrapp.Map.MapFragment
import com.example.parkinglrapp.Search.SearchFragment
import com.example.parkinglrapp.databinding.ActivityMainBinding
import com.example.parkinglrapp.utills.SharedStore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    lateinit var gps : GpsInfo
    var lat : Long? = null
    var long : Long? = null
    lateinit var context : Context
    private val parkingViewModel: RetrofitCall by viewModels()
    lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001 // Firebase 인증에 사용할 코드
    var my_place = ""
    var my_code =""

    var parkingData: List<ParkingItem>? = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        gps = GpsInfo(this)
        if (gps.checkGPS){
            lat = gps.latitude.toLong()
            long = gps.longitude.toLong()
        }
        my_place = findClosestRegion(gps.getCountryAndCity(gps.latitude,gps.longitude)[1].toString(),regions,context)
        if (my_place !== "지역을 찾을 수 없습니다."&& my_place !== ""){

            parkingApiGetAdd(lat!!, long!!)
            if (SharedStore().getSharePrefrerenceBooleanData(context, SharedStore().LOGINYN)){
                var my_place_list : MutableList<String> =
                    (SharedStore().getSharePrefrerenceListData(context,SharedStore().PLACE) as? List<String>)
                        ?.toMutableList() // 불변 리스트를 MutableList로 변환
                        ?: mutableListOf() // 빈 리스트 처리
                my_place_list.add(my_place)
                SharedStore().putSharePrefrerenceListData(context,SharedStore().PLACE,my_place_list)
            }
        }
        showFragment()
        googleLogin()
        binding.menuCard.setOnClickListener {
            if (binding.menuCardView.visibility == View.VISIBLE){
                binding.menuCardView.visibility = View.GONE
            }else{
                binding.menuCardView.visibility = View.VISIBLE
            }
        }
        binding.menuLayout1.menuSearch.setOnClickListener {
            if (SharedStore().getSharePrefrerenceBooleanData(context,SharedStore().LOGINYN)){
                binding.menuCard.visibility=View.GONE
                binding.menuCardView.visibility=View.GONE
                val fragment = SearchFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_view, fragment) // FrameLayout의 ID와 Fragment를 연결
                    .addToBackStack(null) // 뒤로 가기 스택에 추가
                    .commit()
            }else{
                signIn()
            }
        }
        binding.menuLayout1.menuMap.setOnClickListener {
            if (SharedStore().getSharePrefrerenceBooleanData(context,SharedStore().LOGINYN)){
                binding.menuCard.visibility=View.GONE
                binding.menuCardView.visibility=View.GONE
                val fragment = MapFragment()
                val bundle = Bundle()
                bundle.putSerializable("list",ArrayList(parkingData))

                fragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_view, fragment) // FrameLayout의 ID와 Fragment를 연결
                    .addToBackStack(null) // 뒤로 가기 스택에 추가
                    .commit()
                fragment.updateData(ArrayList(parkingData))
            }else{
                signIn()
            }
        }

        binding.menuLayout1.menuAccount.setOnClickListener {
            if (SharedStore().getSharePrefrerenceBooleanData(context,SharedStore().LOGINYN)){
                binding.menuCard.visibility=View.GONE
                binding.menuCardView.visibility=View.GONE
                val fragment = MypageFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_view, fragment) // FrameLayout의 ID와 Fragment를 연결
                    .addToBackStack(null) // 뒤로 가기 스택에 추가
                    .commit()
            }else{
                signIn()
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            // 최상단 Fragment 클래스 확인
            val topFragmentClass = getTopFragmentClass()
            topFragmentClass?.let {
                if (it.simpleName=="MainFragment"){
                    binding.titleView.root.visibility=View.GONE
                    binding.menuCard.visibility=View.VISIBLE
                    binding.menuCardView.visibility=View.GONE

                }else if (it.simpleName=="SearchFragment"){
                    binding.titleView.root.visibility=View.VISIBLE
                    binding.titleView.defTitle.visibility = View.VISIBLE
                    binding.titleView.backTitle.visibility = View.GONE
                    binding.titleView.defTitleText.text=getString(R.string.search)
                    binding.titleView.defTitleIv.setImageDrawable(getDrawable(R.drawable.search_alt))
                    binding.menuCard.visibility=View.GONE
                    binding.menuCardView.visibility=View.GONE

                }else if (it.simpleName=="MapFragment"){
                    binding.titleView.root.visibility=View.VISIBLE
                    binding.titleView.defTitle.visibility = View.VISIBLE
                    binding.titleView.backTitle.visibility = View.GONE
                    binding.titleView.defTitleText.text=getString(R.string.map_title)
                    binding.titleView.defTitleIv.setImageDrawable(getDrawable(R.drawable.map))
                    binding.menuCard.visibility=View.GONE
                    binding.menuCardView.visibility=View.GONE

                }else if (it.simpleName=="MypageFragment"){
                    binding.titleView.root.visibility=View.VISIBLE
                    binding.titleView.defTitle.visibility = View.VISIBLE
                    binding.titleView.backTitle.visibility = View.GONE
                    binding.titleView.defTitleText.text=getString(R.string.account)
                    binding.titleView.defTitleIv.setImageDrawable(getDrawable(R.drawable.user_alt_light))
                    binding.menuCard.visibility=View.GONE
                    binding.menuCardView.visibility=View.GONE

                }else if (it.simpleName=="SearchResultFragment"){
                    binding.titleView.root.visibility=View.VISIBLE
                    binding.titleView.backTitle.visibility = View.VISIBLE
                    binding.titleView.defTitle.visibility = View.GONE
                    binding.titleView.backTitleTv.text=getString(R.string.search_results)
                    binding.menuCard.visibility=View.GONE
                    binding.menuCardView.visibility=View.GONE

                }else if (it.simpleName=="SearchHistoryFragment"){
                    binding.titleView.root.visibility=View.VISIBLE
                    binding.titleView.backTitle.visibility = View.VISIBLE
                    binding.titleView.defTitle.visibility = View.GONE
                    binding.titleView.backTitleTv.text=getString(R.string.search_list)
                    binding.menuCard.visibility=View.GONE
                    binding.menuCardView.visibility=View.GONE

                }
            }
        }
        if(SharedStore().getSharePrefrerenceBooleanData(context,SharedStore().LOGINYN)){
            binding.menuLayout1.mypageText.text = getString(R.string.profile)
        }else{
            binding.menuLayout1.mypageText.text = getString(R.string.account)
        }
        binding.titleView.backTitleIv.setOnClickListener {
            onBackPressed()
        }


    }
    private fun showFragment() {
        // Fragment 인스턴스 생성
        val fragment = MainFragment()// 데이터를 Fragment로 전달
        val bundle = Bundle()
        bundle.putSerializable("list",ArrayList(parkingData))

        fragment.arguments = bundle


        // FragmentTransaction을 사용해 FrameLayout에 Fragment를 추가
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_view, fragment) // FrameLayout의 ID와 Fragment를 연결
            .addToBackStack("main") // 뒤로 가기 스택에 추가
            .commit()
    }

    override fun onResume() {
        super.onResume()
        if (gps.checkNetwork){
            if (lat != null && long != null) {
//                parkingApiGetAdd(lat!!, long!!)
            }else{
                Toast.makeText(context,"GPS값이 없습니다. 확인해주세요.",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun googleLogin(){
        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        // Google Sign-In 옵션 구성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Firebase에서 제공하는 웹 클라이언트 ID
            .requestEmail()
            .build()

        // Google Sign-In 클라이언트 초기화
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // 기존에 로그인된 사용자가 있는지 확인
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }
    fun googleLogout(){
        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()

        // Google Sign-In 옵션 구성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Firebase에서 제공하는 웹 클라이언트 ID
            .requestEmail()
            .build()

        // Google Sign-In 클라이언트 초기화
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // 기존에 로그인된 사용자가 있는지 확인
        val currentUser = auth.currentUser
        if (currentUser != null) {
            googleSignInClient.signOut()

            // Fragment 인스턴스 생성
            val fragment = MainFragment()// 데이터를 Fragment로 전달
            val bundle = Bundle()
            bundle.putSerializable("list",ArrayList(parkingData))

            fragment.arguments = bundle


            // FragmentTransaction을 사용해 FrameLayout에 Fragment를 추가
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, fragment) // FrameLayout의 ID와 Fragment를 연결
                .addToBackStack("main") // 뒤로 가기 스택에 추가
                .commit()
            SharedStore().putSharePrefrerenceBooleanData(context,SharedStore().LOGINYN,false)
            Toast.makeText(context,"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show()
        }
    }
    fun parkingApiGetAdd(lat : Long,long : Long){
        parkingViewModel.parkingData.observe(this, Observer { parkingDataList ->
            // UI 업데이트 예시: 로그 출력
            for (data in parkingDataList) {
                Log.d("ParkingData", "주차장 이름: ${data.prkplceNm}, 도로명 주소: ${data.rdnmadr}")
            }
            parkingData = parkingDataList as ArrayList<ParkingItem>?

            val fragment = supportFragmentManager.findFragmentById(R.id.main_fragment_view) as? MainFragment
            fragment?.updateParkingList(parkingData!!)
        })

        // 에러 메시지 관찰
        parkingViewModel.error.observe(this, Observer { errorMessage ->
            Log.e("ParkingData", errorMessage)
        })

        // 주차장 데이터 가져오기

        // 두 리스트의 크기가 동일한 경우
        val locationWithCodes = regions.zip(codes).toMap()

        val specificCode = locationWithCodes[my_place]
        if (specificCode != null) {
            parkingViewModel.fetchParkingDataName(page = 1,specificCode)
        }  // 예시 좌표
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("MainActivity", "Google sign in failed", e)
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Logged in as: ${user.displayName}", Toast.LENGTH_SHORT).show()
            SharedStore().putSharePrefrerenceBooleanData(context,SharedStore().LOGINYN,true)
            SharedStore().putSharePrefrerenceStringData(context,SharedStore().NICKNAME,user.displayName)
            SharedStore().putSharePrefrerenceStringData(context,SharedStore().IMG,user.photoUrl.toString())
            // UI 업데이트 또는 다른 화면으로 전환
        } else {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            SharedStore().putSharePrefrerenceBooleanData(context,SharedStore().LOGINYN,false)
        }
    }
    fun getTopFragmentClass(): Class<*>? {
        // backStackEntryCount가 0보다 크면 백스택에 Fragment가 있음
        if (supportFragmentManager.backStackEntryCount > 0) {
            // 최상단 Fragment의 백스택 엔트리
            val topFragment = supportFragmentManager.fragments.lastOrNull()
            // 최상단 Fragment가 존재하면 그 클래스 반환
            return topFragment?.javaClass
        }
        return null
    }
    override fun onBackPressed() {
        // 현재 백스택에 쌓인 Fragment 개수를 확인
        if (supportFragmentManager.backStackEntryCount > 1) {
            // 백스택에 여러 개의 Fragment가 있다면 popBackStack으로 뒤로 가기
            supportFragmentManager.popBackStack()
        } else {
            // 백스택에 mainFragment 하나만 남은 경우 Activity 종료
            showAlertDialog()
        }
    }
    private fun showAlertDialog() {
        // AlertDialog 생성
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("알림") // 제목 설정
        builder.setMessage("정말로 종료하시겠습니까?") // 메시지 설정

        // "확인" 버튼 추가
        builder.setPositiveButton("확인") { dialog, _ ->
            // "확인" 버튼 클릭 시 동작
            dialog.dismiss() // 다이얼로그 닫기
            finish()
        }

        // "취소" 버튼 추가
        builder.setNegativeButton("취소") { dialog, _ ->
            // "취소" 버튼 클릭 시 동작
            dialog.dismiss() // 다이얼로그 닫기
        }

        // 다이얼로그 표시
        builder.show()
    }
    fun findClosestRegion(userInput: String, regions: List<String>, context: Context): String {
        val closestRegion = regions.minByOrNull {
            val distance = levenshtein(userInput, it)
            distance
        }
        return closestRegion ?: "지역을 찾을 수 없습니다."
    }
    fun levenshtein(str1: String, str2: String): Int {
        val lenStr1 = str1.length
        val lenStr2 = str2.length

        // 문자열 길이에 맞춰 DP 테이블을 초기화
        val dp = Array(lenStr1 + 1) { IntArray(lenStr2 + 1) }

        for (i in 0..lenStr1) {
            dp[i][0] = i // 첫 번째 열 초기화
        }
        for (j in 0..lenStr2) {
            dp[0][j] = j // 첫 번째 행 초기화
        }

        for (i in 1..lenStr1) {
            for (j in 1..lenStr2) {
                val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,  // 삭제
                    dp[i][j - 1] + 1,  // 삽입
                    dp[i - 1][j - 1] + cost // 교체
                )
            }
        }

        return dp[lenStr1][lenStr2]
    }
    val codes = listOf(
        "5410000", "3780000", "4030000", "3220000", "3050000", "3910000", "5100000", "6300000",
        "3000000", "4201000", "B553081", "3530000", "3620000", "B551294", "5190000", "3330000",
        "4820000", "4231000", "4580000", "4540000", "5020000", "B553452", "3340000", "6520000",
        "6510000", "4711000", "5370000", "5350000", "4050000", "B553695", "4850000", "5000000",
        "4620000", "5570000", "3440000", "4450000", "B552554", "4890000", "5070000", "5170000",
        "5470000", "5390000", "3270000", "3250000", "3860000", "4920000", "5080000", "3040000",
        "4281000", "3670000", "4940000", "3540000", "5540000", "4721000", "6310000", "B551450",
        "3720000", "5090000", "4771000", "3550000", "4970000", "5430000", "B552865", "B551447",
        "3650000", "4510000", "3350000", "4251000", "3730000", "4010000", "5240000", "B551295",
        "5130000", "3260000", "4880000", "3470000", "3460000", "3060000", "4990000", "3930000",
        "4160000", "3170000", "4400000", "3590000", "5010000", "4191000", "4331000", "4181000",
        "5310000", "4671000", "6270000", "4301000", "5440000", "5141000", "4271000", "4590000",
        "5160000", "3010000", "4500000", "3660000", "4341000", "4211000", "4550000", "B553745",
        "4080000", "3940000", "B552094", "3630000", "5590000", "5380000", "3430000", "3150000",
        "B553774", "3480000", "4530000", "5480000", "4390000", "4840000", "3290000", "3830000",
        "3580000", "4321000", "5680000", "3280000", "B551293", "5200000", "3240000", "3390000",
        "B552833", "5250000", "4261000", "3360000", "4610000", "B552147", "3980000", "5050000",
        "B553013", "5120000", "5230000", "3070000", "B553554", "4860000", "3710000", "3080000",
        "5670000", "5150000", "3410000", "3230000", "4020000", "B553949", "5220000", "3320000",
        "4060000", "4221000", "3490000", "B551282", "4420000", "3450000", "B551898", "4430000",
        "3210000", "6280000", "5110000", "3570000", "B555076", "4480000", "5460000", "B552974",
        "5690000", "4781000", "4040000", "5340000", "B553906", "3100000", "5420000", "4600000",
        "4691000", "5710000", "3380000", "3310000", "B553560", "3680000", "4291000", "4741000",
        "4460000", "B551271", "3560000", "4681000", "4470000", "3180000", "4980000", "3140000",
        "3110000", "3190000", "3600000", "3400000", "5260000", "3520000", "3500000", "4570000",
        "3820000", "3900000", "4800000", "5330000", "3970000", "4440000", "4090000", "5450000",
        "5060000", "3020000", "4870000", "4960000", "5210000", "4311000", "3120000", "4490000",
        "5180000", "3090000", "4910000", "4701000", "3700000", "3990000", "4241000", "5400000",
        "4520000", "3300000", "5530000", "4000000", "4751000", "4900000", "4351000", "4930000",
        "5580000", "4641000", "4950000", "4140000", "4830000", "3610000", "5700000", "3420000",
        "4810000", "4170000", "5600000", "4791000", "3370000", "3740000", "4070000", "3200000",
        "4761000", "4731000", "5360000", "B551894", "3640000", "3160000", "3920000"
    )
    val regions = listOf(
        "경상남도 창녕군",
        "경기도 성남시",
        "경기도 의왕시",
        "서울특별시 강남구",
        "서울특별시 동대문구",
        "경기도 평택시",
        "경상북도 영천시",
        "대전광역시",
        "서울특별시 종로구",
        "강원특별자치도 강릉시",
        "이천시시설관리공단",
        "인천광역시 남동구",
        "광주광역시 북구",
        "인천광역시부평구시설관리공단",
        "경상북도 청도군",
        "부산광역시 해운대구",
        "전라남도 순천시",
        "강원특별자치도 속초시",
        "충청남도 서천군",
        "충청남도 논산시",
        "경상북도 포항시",
        "아산시시설관리공단",
        "부산광역시 사하구",
        "제주특별자치도 서귀포시",
        "제주특별자치도 제주시",
        "전북특별자치도 김제시",
        "경상남도 거제시",
        "경상남도 김해시",
        "경기도 용인시",
        "세종특별자치시시설관리공단",
        "전라남도 담양군",
        "전라남도 진도군",
        "충청남도 태안군",
        "충청북도 증평군",
        "대구광역시 남구",
        "충청북도 진천군",
        "기장군도시관리공단",
        "전라남도 보성군",
        "경상북도 안동시",
        "경상북도 영양군",
        "경상남도 거창군",
        "경상남도 의령군",
        "부산광역시 동구",
        "부산광역시 중구",
        "경기도 부천시",
        "전라남도 강진군",
        "경상북도 구미시",
        "서울특별시 광진구",
        "강원특별자치도 평창군",
        "대전광역시 유성구",
        "전라남도 영암군",
        "인천광역시 부평구",
        "경기도 광주시",
        "전북특별자치도 완주군",
        "울산광역시",
        "청도공영사업공사",
        "울산광역시 북구",
        "경상북도 영주시",
        "전북특별자치도 순창군",
        "인천광역시 계양구",
        "전라남도 영광군",
        "경상남도 남해군",
        "포항시시설관리공단",
        "오산시시설관리공단",
        "대전광역시 중구",
        "충청남도 보령시",
        "부산광역시 금정구",
        "강원특별자치도 홍천군",
        "울산광역시 울주군",
        "경기도 시흥시",
        "경상북도 봉화군",
        "인천광역시계양구시설관리공단",
        "경상북도 경산시",
        "부산광역시 서구",
        "전라남도 고흥군",
        "대구광역시 달서구",
        "대구광역시 수성구",
        "서울특별시 중랑구",
        "전라남도 완도군",
        "경기도 안산시",
        "경기도 가평군",
        "서울특별시 금천구",
        "충청북도 제천시",
        "광주광역시 동구",
        "전라남도 신안군",
        "강원특별자치도 원주시",
        "강원특별자치도 인제군",
        "강원특별자치도 춘천시",
        "경상남도 진주시",
        "전북특별자치도 군산시",
        "대구광역시",
        "강원특별자치도 철원군",
        "경상남도 하동군",
        "대구광역시 군위군",
        "강원특별자치도 영월군",
        "충청남도 청양군",
        "경상북도 청송군",
        "서울특별시 중구",
        "충청남도 공주시",
        "대전광역시 서구",
        "강원특별자치도 고성군",
        "강원특별자치도 동해시",
        "충청남도 금산군",
        "경주시시설관리공단",
        "경기도 안성시",
        "경기도 고양시",
        "남양주도시공사",
        "광주광역시 광산구",
        "경기도 양주시",
        "경상남도 양산시",
        "대구광역시 서구",
        "서울특별시 강서구",
        "서울시설공단",
        "대구광역시 달성군",
        "충청남도 서산시",
        "경상남도 합천군",
        "충청북도 충주시",
        "전라남도 광양시",
        "부산광역시 부산진구",
        "경기도 안양시",
        "인천광역시 옹진군",
        "강원특별자치도 양구군",
        "충청남도 당진시",
        "부산광역시 영도구",
        "인천광역시남동구도시관리공단",
        "경상북도 고령군",
        "서울특별시 강동구",
        "부산광역시 사상구",
        "거제해양관광개발공사",
        "경상북도 울진군",
        "강원특별자치도 횡성군",
        "부산광역시 강서구",
        "충청남도 예산군",
        "울주군시설관리공단",
        "경기도 구리시",
        "경상북도 경주시",
        "울산광역시중구도시관리공단",
        "경상북도 문경시",
        "경상북도 예천군",
        "서울특별시 성북구",
        "인천관광공사",
        "전라남도 곡성군",
        "울산광역시 동구",
        "서울특별시 강북구",
        "경상남도 창원시",
        "경상북도 의성군",
        "대구광역시 중구",
        "서울특별시 송파구",
        "경기도 군포시",
        "인천광역시미추홀구시설관리공단",
        "경상북도 칠곡군",
        "부산광역시 북구",
        "경기도 파주시",
        "강원특별자치도 태백시",
        "인천광역시 중구",
        "강북구도시관리공단",
        "충청북도 보은군",
        "대구광역시 북구",
        "서울특별시금천구시설관리공단",
        "충청북도 옥천군",
        "서울특별시 서초구",
        "인천광역시",
        "경상북도 상주시",
        "인천광역시 강화군",
        "구미도시공사",
        "충청북도 단양군",
        "경상남도 함양군",
        "부산관광공사",
        "세종특별자치시",
        "전북특별자치도 고창군",
        "경기도 하남시",
        "경상남도 사천시",
        "인천시설공단",
        "서울특별시 노원구",
        "경상남도 고성군",
        "충청남도 홍성군",
        "전북특별자치도 정읍시",
        "충청북도 청주시",
        "부산광역시 수영구",
        "부산광역시 남구",
        "울산시설공단",
        "대전광역시 대덕구",
        "강원특별자치도 정선군",
        "전북특별자치도 무주군",
        "충청북도 괴산군",
        "광주광역시도시공사",
        "인천광역시 서구",
        "전북특별자치도 익산시",
        "충청북도 음성군",
        "서울특별시 영등포구",
        "전라남도 장성군",
        "서울특별시 양천구",
        "서울특별시 은평구",
        "서울특별시 동작구",
        "전라남도 여수시",
        "전라남도 나주시",
        "경상북도 영덕군",
        "부산광역시 사상구",
        "경기도 시흥시",
        "경기도 광명시",
        "대구광역시",
        "경기도 고양시",
        "경기도 오산시",
        "전북특별자치도 정읍시",
        "충청북도 청주시",
        "부산광역시 수영구",
        "부산광역시 남구",
        "울산시설공단",
        "대전광역시 대덕구",
        "강원특별자치도 정선군",
        "전북특별자치도 무주군",
        "충청북도 괴산군",
        "광주광역시도시공사",
        "인천광역시 서구",
        "전북특별자치도 익산시",
        "충청북도 음성군",
        "서울특별시 영등포구",
        "전라남도 장성군",
        "서울특별시 양천구",
        "서울특별시 은평구",
        "서울특별시 동작구",
        "광주광역시 서구",
        "부산광역시 기장군",
        "경상북도 울릉군",
        "인천광역시 연수구",
        "인천광역시 동구",
        "충청남도 부여군",
        "경기도 의정부시",
        "경기도 광명시",
        "전라남도 목포시",
        "경상남도 통영시",
        "경기도 과천시",
        "충청북도 영동군",
        "경기도 김포시",
        "경상남도 산청군",
        "경상북도 김천시",
        "서울특별시 용산구",
        "전라남도 구례군",
        "전라남도 함평군",
        "경상북도 성주군",
        "강원특별자치도 화천군",
        "서울특별시 서대문구",
        "충청남도 천안시",
        "경상북도 영덕군",
        "서울특별시 도봉구",
        "전라남도 장흥군",
        "전북특별자치도 남원시",
        "울산광역시 남구",
        "경기도 남양주시",
        "강원특별자치도 삼척시",
        "경상남도 함안군",
        "충청남도 아산시",
        "부산광역시 동래구",
        "경기도 화성시",
        "경기도 오산시",
        "전북특별자치도 장수군",
        "전라남도 화순군",
        "강원특별자치도 양양군",
        "전라남도 해남군",
        "충청남도 계룡시",
        "전북특별자치도 전주시",
        "전라남도 무안군",
        "경기도 연천군",
        "전라남도 나주시",
        "광주광역시 남구",
        "경기도 여주시",
        "대구광역시 동구",
        "전라남도 여수시",
        "경기도 양평군",
        "경기도 포천시",
        "전북특별자치도 부안군",
        "부산광역시 연제구",
        "경기도 수원시",
        "경기도 이천시",
        "서울특별시 관악구",
        "전북특별자치도 임실군",
        "전북특별자치도 진안군",
        "경상남도 밀양시",
        "인천교통공사",
        "대전광역시 동구",
        "서울특별시 구로구",
        "경기도 동두천시"
    )
}