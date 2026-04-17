package com.example.mystudyapp.school_schedule

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mystudyapp.R

class CalendarViewHolder(itemView: View, private val onItemListener: CalendarAdapter.OnItemListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
    private val doIndicator: View = itemView.findViewById(R.id.dot_indicator)
    private val cellRoot: View = itemView.findViewById(R.id.cellRoot)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        onItemListener.onItemClick(adapterPosition, dayOfMonth.text.toString())
    }

    fun setDotVisibility(isVisible: Boolean) {
        doIndicator.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun setHeaderRowStyle(isHeader: Boolean) {
        if (isHeader) {
            cellRoot.setBackgroundResource(R.drawable.tkb_calendar_header_bg)
            TextViewCompat.setTextAppearance(dayOfMonth, R.style.TkbCalendarHeaderText)
            dayOfMonth.setTextColor(ContextCompat.getColor(itemView.context, R.color.tkb_muted))
            doIndicator.visibility = View.GONE
        } else {
            cellRoot.setBackgroundResource(R.drawable.tkb_calendar_cell_bg)
            TextViewCompat.setTextAppearance(dayOfMonth, R.style.TkbCalendarDayText)
            dayOfMonth.setTextColor(ContextCompat.getColor(itemView.context, R.color.tkb_primary))
        }
    }
}