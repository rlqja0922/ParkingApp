package com.example.parkinglrapp.Map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.parkinglrapp.Data.ParkingItem
import com.example.parkinglrapp.R
import com.example.parkinglrapp.databinding.FragmentMapBinding
import com.example.parkinglrapp.main.MainActivity
import com.example.parkinglrapp.utills.GpsInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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
    private var googleMap: GoogleMap? = null
    private var data:  List<ParkingItem>? = null // 외부 데이터를 저장
    private val markers = mutableListOf<Marker>() // 추가된 마커를 관리하기 위한 리스트
    var selectedItem : ParkingItem? = null

    private val callback = OnMapReadyCallback { map ->

        googleMap = map
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
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20f))

        val marker = googleMap!!.addMarker(markerOptions)
        // 마커 클릭 리스너 설정
        googleMap!!.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker == marker) {
                // 마커 클릭 시 처리
                binding.materialCardView.visibility = View.GONE
            }
            false // false를 반환하면 기본 동작(InfoWindow 표시)도 실행
        }

        // InfoWindow 클릭 리스너 설정 (선택 사항)
        googleMap!!.setOnInfoWindowClickListener { clickedMarker ->
            if (clickedMarker == marker) {
                // InfoWindow 클릭 시 처리
                Toast.makeText(context, "InfoWindow 클릭됨", Toast.LENGTH_SHORT).show()
            }
        }

        data?.let { updateMapWithLocation(it) } // 데이터가 있으면 바로 지도 업데이트
        selectedItem?.let { moveCamera(it) }
    }
    // 외부 데이터를 설정하고 지도 업데이트
    fun updateData(newList: List<ParkingItem>) {
        data = newList
        googleMap?.let { updateMapWithLocation(newList) }
    }
    private fun updateMapWithLocation(locationList: List<ParkingItem>) {
        // 마커 추가

        googleMap?.apply {
            for (item in locationList) {
                if (item.latitude.isEmpty()) {
                    continue
                }

                val location = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(item.prkplceNm)
                    .snippet(item.prkplceSe)

                val marker = addMarker(markerOptions)

                // 마커에 item 정보를 tag로 저장
                marker?.tag = item

                marker?.let { markers.add(it) } // 추가된 마커를 리스트에 저장
            }

            // 마커 클릭 리스너 설정
            setOnMarkerClickListener { clickedMarker ->
                // 클릭된 마커의 tag에서 ParkingItem 가져오기
                val item = clickedMarker.tag as? ParkingItem
                if (item != null) {
                    // 마커 클릭 시 해당 item 정보를 UI에 표시
                    binding.materialCardView.visibility = View.VISIBLE
                    binding.parkCardName.text = item.prkplceNm
                    binding.parkCardLocation.text =  item.rdnmadr.ifEmpty { item.lnmadr }
                    binding.parkCardLocation2.text =  item.latitude +"."+ item.longitude
                    binding.parkCardInfo1.text = item.prkplceType
                    binding.parkCardInfo2.text = item.parkingchrgeInfo
                    binding.parkCardInfo3.text = item.operDay
                } else {
                    // tag에 ParkingItem이 없는 경우 UI 숨김 처리
                    binding.materialCardView.visibility = View.GONE
                }
                false // InfoWindow 기본 동작을 유지하려면 false 반환
            }


            // 마커 클릭 리스너 설정


            setOnInfoWindowClickListener { clickedMarker ->
                Toast.makeText(context, "InfoWindow 클릭됨", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun moveCamera(item : ParkingItem){
        googleMap?.apply {

            val location = LatLng(item.latitude.toDouble(), item.longitude.toDouble())

            val markerOptions = MarkerOptions()
                .position(location)
                .title(item.prkplceNm)
                .snippet(item.prkplceSe)

            val marker = googleMap!!.addMarker(markerOptions)

            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20f))


            binding.materialCardView.visibility = View.VISIBLE
            binding.parkCardName.text = item.prkplceNm
            binding.parkCardLocation.text =  item.rdnmadr.ifEmpty { item.lnmadr }
            binding.parkCardLocation2.text =  item.latitude +"."+ item.longitude
            binding.parkCardInfo1.text = item.prkplceType
            binding.parkCardInfo2.text = item.parkingchrgeInfo
            binding.parkCardInfo3.text = item.operDay
            // 마커 클릭 리스너 설정
            setOnMarkerClickListener { clickedMarker ->
                // 클릭된 마커의 tag에서 ParkingItem 가져오기
                val item = clickedMarker.tag as? ParkingItem
                if (item != null) {
                    // 마커 클릭 시 해당 item 정보를 UI에 표시
                    binding.materialCardView.visibility = View.VISIBLE
                    binding.parkCardName.text = item.prkplceNm
                    binding.parkCardLocation.text =  item.rdnmadr.ifEmpty { item.lnmadr }
                    binding.parkCardLocation2.text =  item.latitude +"."+ item.longitude
                    binding.parkCardInfo1.text = item.prkplceType
                    binding.parkCardInfo2.text = item.parkingchrgeInfo
                    binding.parkCardInfo3.text = item.operDay
                } else {
                    // tag에 ParkingItem이 없는 경우 UI 숨김 처리
                    binding.materialCardView.visibility = View.GONE
                }
                false // InfoWindow 기본 동작을 유지하려면 false 반환
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
        data = (arguments?.getSerializable("list") as? ArrayList<ParkingItem>)

        // 전달받은 데이터 가져오기
        selectedItem = arguments?.getSerializable("item") as? ParkingItem

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
        fun newInstance(selectedItem: ParkingItem): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putSerializable("item", selectedItem)
            fragment.arguments = args
            return fragment
        }
    }
}