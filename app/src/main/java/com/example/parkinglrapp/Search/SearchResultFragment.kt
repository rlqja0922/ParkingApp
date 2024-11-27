package com.example.parkinglrapp.Search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinglrapp.Data.ParkingItem
import com.example.parkinglrapp.Data.SearchData
import com.example.parkinglrapp.List.MyItemRecyclerViewAdapter
import com.example.parkinglrapp.Map.MapFragment
import com.example.parkinglrapp.R
import com.example.parkinglrapp.RetrofitCall
import com.example.parkinglrapp.databinding.FragmentMainBinding
import com.example.parkinglrapp.databinding.FragmentSearchResultBinding
import com.example.parkinglrapp.main.MainFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val parkingViewModel: RetrofitCall by viewModels()
    private lateinit var binding : FragmentSearchResultBinding

    private var columnCount = 1
    private lateinit var recyclerView: RecyclerView

    private lateinit var recyclerViewAdapter: MyItemRecyclerViewAdapter
    private var dataList: MutableList<ParkingItem> = mutableListOf()
    private lateinit var searchdata : SearchData
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
        binding  = FragmentSearchResultBinding.inflate(layoutInflater)
        searchdata = arguments!!.getSerializable("item") as SearchData
        binding.searchHint.text = searchdata.add
        val includedView = binding.searchResultList  // include 태그의 id

        // 포함된 레이아웃 내 RecyclerView에 접근
        recyclerView = includedView.list

        // RecyclerView 설정
        recyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }

        recyclerViewAdapter = MyItemRecyclerViewAdapter(dataList) { selectedItem ->
            // 클릭 이벤트 처리
            replaceFragmentWithDetails(selectedItem)
        }
        recyclerView.adapter = recyclerViewAdapter

        parkingApiGetAdd(searchdata.add,searchdata.code)
        return binding.root
    }

    fun updateParkingList(newList: List<ParkingItem>) {
        dataList.clear() // 기존 데이터 삭제
        dataList.addAll(newList) // 새 데이터 추가
        recyclerViewAdapter.notifyDataSetChanged()
    }
    fun parkingApiGetAdd(data : String, code : String){
        parkingViewModel.parkingData.observe(this, Observer { parkingDataList ->
            // UI 업데이트 예시: 로그 출력
            for (data in parkingDataList) {
                Log.d("ParkingData", "주차장 이름: ${data.prkplceNm}, 도로명 주소: ${data.rdnmadr}")
            }
            (parkingDataList as ArrayList<ParkingItem>?)?.let { updateParkingList(it) }

        })

        // 에러 메시지 관찰
        parkingViewModel.error.observe(this, Observer { errorMessage ->
            Log.e("ParkingData", errorMessage)
        })


        parkingViewModel.fetchParkingDataName(page = 1,code)
    }
    private fun replaceFragmentWithDetails(selectedItem: ParkingItem) {
        val detailsFragment = MapFragment.newInstance(selectedItem)

        val bundle = Bundle()
        bundle.putSerializable("list",ArrayList(dataList))
        bundle.putSerializable("item",(selectedItem))

        detailsFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_view, detailsFragment)
            .addToBackStack(null)
            .commit()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: SearchData) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}