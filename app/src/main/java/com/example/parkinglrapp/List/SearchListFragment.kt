package com.example.parkinglrapp.List

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.parkinglrapp.Data.ParkingItem
import com.example.parkinglrapp.Data.SearchData
import com.example.parkinglrapp.R
import com.example.parkinglrapp.List.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class SearchListFragment : Fragment() {

    private var columnCount = 1

    private lateinit var recyclerView: RecyclerView
    lateinit var list : List<SearchData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_list_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter2(list) { selectedItem ->
                    // 클릭 이벤트 처리
                    replaceFragmentWithDetails(selectedItem)
                }
            }
        }
        return view
    }
    fun replaceFragmentWithDetails(seletedItem : SearchData){

    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            SearchListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}