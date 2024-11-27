package com.example.parkinglrapp.List

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.parkinglrapp.Data.ParkingItem
import com.example.parkinglrapp.Data.SearchData
import com.example.parkinglrapp.R

import com.example.parkinglrapp.List.placeholder.PlaceholderContent.PlaceholderItem
import com.example.parkinglrapp.databinding.FragmentParkingItemBinding
import com.example.parkinglrapp.databinding.FragmentSearchListBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter2(
    private val values: List<SearchData>,
    private val itemClickListener: (SearchData) -> Unit // 클릭 이벤트 콜백
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = FragmentSearchListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values?.get(position)
        item?.let { holder.bind(it) }  // ViewHolder에 데이터 바인딩
    }

    override fun getItemCount(): Int = values.size ?: 0

    inner class ViewHolder(val binding: FragmentSearchListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchData) {
            binding.parkName.text = item.add
            binding.root.setOnClickListener {
                itemClickListener(item)
            }
        }
    }

}