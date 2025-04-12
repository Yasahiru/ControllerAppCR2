package com.club.controllerappcr2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class dataAdapter(private val dataList: MutableList<String>) : RecyclerView.Adapter<dataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataTextView: TextView = itemView.findViewById(R.id.dataTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataTextView.text = dataList[position]
    }

    override fun getItemCount(): Int = dataList.size

    fun addData(newData: String) {
        dataList.add(newData)
        notifyItemInserted(dataList.size - 1)
    }
}
