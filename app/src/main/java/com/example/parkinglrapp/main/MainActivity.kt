package com.example.parkinglrapp.main

import android.content.Context
import android.content.Intent
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
import com.example.parkinglrapp.databinding.ActivityMain2Binding
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
    lateinit var binding: ActivityMain2Binding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 9001 // Firebase 인증에 사용할 코드
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.googleLoginImageview.setOnClickListener {

            signIn()
        }
        context = this
        gps = GpsInfo(this)
        if (gps.checkGPS){
            lat = gps.latitude.toLong()
            long = gps.longitude.toLong()
        }
        googleLogin()

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
        parkingViewModel.fetchParkingAddData(page = 0, lat = lat, long = long)  // 예시 좌표
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
            // UI 업데이트 또는 다른 화면으로 전환
        } else {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
        }
    }
}