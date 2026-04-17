package com.example.mystudyapp.document.Document_Activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mystudyapp.R
import com.example.mystudyapp.document.Entity.Documents

class TaiLieuAdapter(
    private var taiLieuList: MutableList<Documents>,
    private val onFileClick: (Documents) -> Unit
) : RecyclerView.Adapter<TaiLieuAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tai_lieu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taiLieu = taiLieuList[position]
        holder.tenTaiLieu.text = taiLieu.tenFile

        holder.chechBox.isChecked = selectedItems.contains(taiLieu.id)
        holder.chechBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedItems.add(taiLieu.id)
            else selectedItems.remove(taiLieu.id)
        }
        holder.itemView.setOnClickListener { onFileClick(taiLieu) }
    }

    override fun getItemCount(): Int = taiLieuList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenTaiLieu: TextView = itemView.findViewById(R.id.linkurl)
        val chechBox: CheckBox = itemView.findViewById(R.id.ChoiceDelete)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSelectedItems() {
        taiLieuList = taiLieuList.filter { !selectedItems.contains(it.id) }.toMutableList()
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItemIds(): List<Int> {
        return selectedItems.toList()
    }
}

