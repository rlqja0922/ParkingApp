package com.example.parkinglrapp.Map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.parkinglrapp.R
import com.example.parkinglrapp.databinding.FragmentMapBinding
import com.example.parkinglrapp.main.MainActivity
import com.example.parkinglrapp.utills.GpsInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     lateinit var binding : FragmentMapBinding
    private var mMap: GoogleMap? = null


    private val callback = OnMapReadyCallback { googleMap ->

        val activity = requireActivity() as MainActivity
        val gps = GpsInfo(requireContext())

        if (gps.checkGPS) {
            val latitude = gps.latitude
            val longitude = gps.longitude
            // GPS 데이터 사용
        }
        val location = LatLng(gps.latitude, gps.longitude)
        // 마커 추가
        val markerOptions = MarkerOptions()
            .position(location)
            .title("현재 위치")
            .snippet("현재 위치") // 마커 정보 창에 표시할 내용
        // 카메라 이동 (줌 레벨 15)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        val marker = googleMap.addMarker(markerOptions)
        // 마커 클릭 리스너 설정
        googleMap.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker == marker) {
                // 마커 클릭 시 처리
                binding.materialCardView.visibility = View.VISIBLE
            }
            false // false를 반환하면 기본 동작(InfoWindow 표시)도 실행
        }

        // InfoWindow 클릭 리스너 설정 (선택 사항)
        googleMap.setOnInfoWindowClickListener { clickedMarker ->
            if (clickedMarker == marker) {
                // InfoWindow 클릭 시 처리
                Toast.makeText(context, "InfoWindow 클릭됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(callback)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}