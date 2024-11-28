package com.example.parkinglrapp.Search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinglrapp.Data.ParkingItem
import com.example.parkinglrapp.Data.SearchData
import com.example.parkinglrapp.List.MyItemRecyclerViewAdapter
import com.example.parkinglrapp.List.MyItemRecyclerViewAdapter2
import com.example.parkinglrapp.Map.MapFragment
import com.example.parkinglrapp.R
import com.example.parkinglrapp.RetrofitCall
import com.example.parkinglrapp.databinding.FragmentSearchBinding
import com.example.parkinglrapp.main.MainActivity
import com.example.parkinglrapp.utills.SharedStore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSearchBinding
    private lateinit var recyclerView: RecyclerView
    private var columnCount = 1
    lateinit var list : MutableList<SearchData>
    private val parkingViewModel: RetrofitCall by viewModels()


    private var searchHistory: MutableList<SearchData> = mutableListOf()

    private lateinit var recyclerViewAdapter: MyItemRecyclerViewAdapter2
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
        binding = FragmentSearchBinding.inflate(layoutInflater)
        val includedView = binding.searchList  // include 태그의 id

        val activity = requireActivity() as MainActivity

        list = activity.codes.zip(activity.regions){ code, add ->SearchData(code,add) }.toMutableList()
        // 포함된 레이아웃 내 RecyclerView에 접근
        recyclerView = includedView.list

        // RecyclerView 설정
        recyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }

        recyclerViewAdapter = MyItemRecyclerViewAdapter2(list) { selectedItem ->
            // 클릭 이벤트 처리
            replaceFragmentWithDetails(selectedItem)
        }
        recyclerView.adapter = recyclerViewAdapter

        binding.searchResultHistory.setOnClickListener {
            val fragment = SearchHistoryFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, fragment)
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }
    fun replaceFragmentWithDetails(seletedItem : SearchData){
        searchHistory = mutableListOf<SearchData>()
        searchHistory = context?.let { SharedStore().getSearchHistoryResModel(it) } as MutableList<SearchData>
        searchHistory.add(seletedItem)
        SharedStore().saveSearchHistoryResModel(context!!,searchHistory)


        val detailsFragment = SearchResultFragment.newInstance(seletedItem)

        val bundle = Bundle()
        bundle.putSerializable("item",(seletedItem))

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
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}