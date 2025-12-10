package com.example.appcuoiky.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R

class CalendarAdapter(
    private var days: List<String>
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        holder.tvDay.text = day

        // Nếu ô trống (không phải ngày) thì ẩn đi
        if (day.isEmpty()) {
            holder.tvDay.visibility = View.INVISIBLE
        } else {
            holder.tvDay.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = days.size

    // Hàm cập nhật dữ liệu mới khi chuyển tháng
    fun updateData(newDays: List<String>) {
        days = newDays
        notifyDataSetChanged()
    }
}