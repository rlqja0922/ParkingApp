package com.example.parkinglrapp.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinglrapp.Data.ParkingItem
import com.example.parkinglrapp.List.MyItemRecyclerViewAdapter
import com.example.parkinglrapp.Map.MapFragment
import com.example.parkinglrapp.R
import com.example.parkinglrapp.databinding.FragmentMainBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var columnCount = 1
    private lateinit var binding:FragmentMainBinding
    private lateinit var recyclerView: RecyclerView

    private lateinit var recyclerViewAdapter: MyItemRecyclerViewAdapter
    private var dataList: MutableList<ParkingItem> = mutableListOf()
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
        binding = FragmentMainBinding.inflate(inflater, container,false)

        dataList = (arguments?.getSerializable("list") as? ArrayList<ParkingItem>)!!
        val includedView = binding.include  // include 태그의 id

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



        return binding.root  // fragment의 root view를 반환


    }
    fun updateParkingList(newList: List<ParkingItem>) {
        dataList.clear() // 기존 데이터 삭제
        dataList.addAll(newList) // 새 데이터 추가
        recyclerViewAdapter.notifyDataSetChanged()
    }
    private fun replaceFragmentWithDetails(selectedItem: ParkingItem) {
        if(selectedItem.latitude.isEmpty()){
            Toast.makeText(context,"공공기관에서 주소를 제공하지 않는 주차장 입니다.", Toast.LENGTH_SHORT).show()
        }else{
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}