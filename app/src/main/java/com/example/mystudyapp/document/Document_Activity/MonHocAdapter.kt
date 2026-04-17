package com.example.mystudyapp.document.Document_Activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mystudyapp.R
import com.example.mystudyapp.document.Entity.Subjects

class MonHocAdapter(
    monHocList: List<Subjects>,
    private val onClick: (Subjects) -> Unit
) : RecyclerView.Adapter<MonHocAdapter.ViewHolder>() {

    private var listMonHocChange = monHocList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mon_hoc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monHoc = listMonHocChange[position]
        holder.tenMonHoc.text = monHoc.tenMonHoc
        holder.ngayThem.text = monHoc.ngayThem
        holder.itemView.setOnClickListener { onClick(monHoc) }
    }

    override fun getItemCount(): Int = listMonHocChange.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenMonHoc: TextView = itemView.findViewById(R.id.tenTaiLieu)
        val ngayThem: TextView = itemView.findViewById(R.id.ngayThem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterList: List<Subjects>) {
        listMonHocChange = filterList
        notifyDataSetChanged()
    }
}

