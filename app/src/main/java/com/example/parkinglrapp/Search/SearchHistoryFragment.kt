package com.example.parkinglrapp.Search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinglrapp.Data.SearchData
import com.example.parkinglrapp.List.MyItemRecyclerViewAdapter2
import com.example.parkinglrapp.R
import com.example.parkinglrapp.databinding.FragmentSearchHistoryBinding
import com.example.parkinglrapp.main.MainActivity
import com.example.parkinglrapp.utills.SharedStore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSearchHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private var columnCount = 1
    lateinit var list : MutableList<SearchData>

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
        binding = FragmentSearchHistoryBinding.inflate(inflater, container,false)
        val includedView = binding.searchHistoryList  // include 태그의 id

        val activity = requireActivity() as MainActivity

        // 포함된 레이아웃 내 RecyclerView에 접근
        recyclerView = includedView.list

        list = mutableListOf<SearchData>()
        list = SharedStore().getSearchHistoryResModel(context!!) as MutableList<SearchData>

        binding.searchHistoryDeleteview.setOnClickListener {
            showAlertDialog()
        }
        if (list.isEmpty()){
            binding.searchHistoryText.visibility = View.VISIBLE
            binding.searchHistoryList.list.visibility = View.GONE
        }else{
            binding.searchHistoryText.visibility = View.GONE
            binding.searchHistoryList.list.visibility = View.VISIBLE
        }

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


        return binding.root
    }
    private fun showAlertDialog() {
        // AlertDialog 생성
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("알림") // 제목 설정
        builder.setMessage("정말로 삭제 하시겠습니까?") // 메시지 설정

        // "확인" 버튼 추가
        builder.setPositiveButton("확인") { dialog, _ ->
            // "확인" 버튼 클릭 시 동작
            dialog.dismiss() // 다이얼로그 닫기

            list.clear()
            binding.searchHistoryText.visibility = View.VISIBLE
            binding.searchHistoryList.list.visibility = View.GONE
        }

        // "취소" 버튼 추가
        builder.setNegativeButton("취소") { dialog, _ ->
            // "취소" 버튼 클릭 시 동작
            dialog.dismiss() // 다이얼로그 닫기
        }

        // 다이얼로그 표시
        builder.show()
    }
    fun replaceFragmentWithDetails(seletedItem : SearchData){
        val detailsFragment = SearchResultFragment.newInstance(seletedItem)

        val bundle = Bundle()
        bundle.putSerializable("item",(seletedItem))

        detailsFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_view, detailsFragment)
            .addToBackStack(null)
            .commit()
    }

}