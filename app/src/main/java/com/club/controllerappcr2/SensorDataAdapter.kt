package com.club.controllerappcr2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.club.controllerappcr2.model.SensorData

class SensorDataAdapter(private val dataList: MutableList<SensorData>) :
    RecyclerView.Adapter<SensorDataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataText: TextView = itemView.findViewById(R.id.dataText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataText.text = dataList[position].value
    }

    fun addData(newData: SensorData) {
        dataList.add(0, newData) // add at top
        notifyItemInserted(0)
    }
}
