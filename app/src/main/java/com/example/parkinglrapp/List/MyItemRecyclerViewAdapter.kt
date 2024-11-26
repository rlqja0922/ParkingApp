package com.example.parkinglrapp.List

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.parkinglrapp.Data.ParkingItem

import com.example.parkinglrapp.List.placeholder.PlaceholderContent.PlaceholderItem
import com.example.parkinglrapp.databinding.FragmentParkingItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private var values: MutableList<ParkingItem>,
    private val itemClickListener: (ParkingItem) -> Unit // 클릭 이벤트 콜백
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentParkingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values?.get(position)
        item?.let { holder.bind(it) }  // ViewHolder에 데이터 바인딩
    }
    // 리스트 갱신을 위한 메소드

    override fun getItemCount(): Int = values?.size ?: 0
    fun updateList(newValues: ArrayList<ParkingItem>) {
        values!!.clear()
        values!!.addAll(newValues)
        notifyDataSetChanged()  // RecyclerView 갱신
    }
    inner class ViewHolder(private val binding: FragmentParkingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ParkingItem) {
            binding.parkName.text = item.prkplceNm
            binding.parkContent.text = item.rdnmadr ?: item.lnmadr
            binding.parkDetail.text = item.operDay
            binding.root.setOnClickListener {
                itemClickListener(item)
            }
        }
        override fun toString(): String {
            return super.toString() + " "
        }
    }

}