package com.example.parkinglrapp.Account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinglrapp.Data.SearchData
import com.example.parkinglrapp.List.MyItemRecyclerViewAdapter2
import com.example.parkinglrapp.R
import com.example.parkinglrapp.Search.SearchHistoryFragment
import com.example.parkinglrapp.databinding.FragmentAccountBinding
import com.example.parkinglrapp.databinding.FragmentMypageBinding
import com.example.parkinglrapp.main.MainActivity
import com.example.parkinglrapp.utills.SharedStore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MypageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MypageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentMypageBinding
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
        binding = FragmentMypageBinding.inflate(inflater, container,false)
        binding.profileName.text = SharedStore().getSharePrefrerenceStringData(context,SharedStore().NICKNAME)
        binding.myPlace.text = findMostFrequentValue(SharedStore().getSharePrefrerenceListData(context!!,SharedStore().PLACE))

        binding.logoutView.setOnClickListener {

            val parentActivity = activity as? MainActivity
            parentActivity!!.googleLogout()
        }
        val includedView = binding.list  // include 태그의 id

        val activity = requireActivity() as MainActivity

        // 포함된 레이아웃 내 RecyclerView에 접근
        recyclerView = includedView.list

        list = mutableListOf<SearchData>()
        list = SharedStore().getSearchHistoryResModel(context!!) as MutableList<SearchData>


        // RecyclerView 설정
        recyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }

        recyclerViewAdapter = MyItemRecyclerViewAdapter2(list) { selectedItem ->
            // 클릭 이벤트 처리
        }
        recyclerView.adapter = recyclerViewAdapter
        binding.mySearchlistIv.setOnClickListener {
            val fragment = SearchHistoryFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_view, fragment)
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }
    fun findMostFrequentValue(strings: List<String>): String? {
        if (strings.isEmpty()) return null // 리스트가 비어 있으면 null 반환

        // 빈도 계산
        val frequencyMap = strings.groupingBy { it }.eachCount()

        // 가장 많은 빈도를 가진 값을 찾기
        return frequencyMap.maxByOrNull { it.value }?.key
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MypageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MypageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}