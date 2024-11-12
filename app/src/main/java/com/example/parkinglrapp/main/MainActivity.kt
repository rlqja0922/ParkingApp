package com.example.parkinglrapp.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.parkinglrapp.R
import com.example.parkinglrapp.RetrofitApi
import com.example.parkinglrapp.RetrofitCall
import com.example.parkinglrapp.utills.GpsInfo
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.parkinglrapp.Account.AccountFragment
import com.example.parkinglrapp.Account.MypageFragment
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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_view, fragment) // FrameLayout의 ID와 Fragment를 연결
                    .addToBackStack(null) // 뒤로 가기 스택에 추가
                    .commit()
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

                }
            }
        }


    }
    private fun showFragment() {
        // Fragment 인스턴스 생성
        val fragment = MainFragment()

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
                parkingApiGetAdd(lat!!, long!!)
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
        parkingViewModel.fetchParkingAddData(page = 1, lat = lat, long = long)  // 예시 좌표
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
            finish()
        }
    }
}