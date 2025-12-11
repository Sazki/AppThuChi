package com.example.appcuoiky.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R

class CalendarAdapter(
    private var days: List<String>,
    private val onDayClick: (String) -> Unit // Callback khi click
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    // Lưu vị trí ngày đang được chọn (-1 là chưa chọn ngày nào)
    private var selectedPosition = -1

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val container: View = itemView // Layout bao ngoài để đổi màu nền
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]
        holder.tvDay.text = day

        if (day.isEmpty()) {
            holder.tvDay.visibility = View.INVISIBLE
            holder.container.setBackgroundColor(Color.TRANSPARENT)
            holder.itemView.setOnClickListener(null)
        } else {
            holder.tvDay.visibility = View.VISIBLE

            // Xử lý màu nền: Nếu đang chọn thì màu Xanh, không thì màu Trắng/Trong suốt
            if (position == selectedPosition) {
                holder.container.setBackgroundResource(R.drawable.bg_rounded_green) // Hoặc setBackgroundColor
                holder.tvDay.setTextColor(Color.WHITE)
            } else {
                holder.container.setBackgroundColor(Color.TRANSPARENT)
                holder.tvDay.setTextColor(Color.BLACK)
            }

            // Xử lý Click
            holder.itemView.setOnClickListener {
                // Cập nhật vị trí chọn cũ và mới để load lại giao diện
                val previousItem = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousItem)
                notifyItemChanged(selectedPosition)

                // Gửi ngày ra ngoài Fragment
                onDayClick(day)
            }
        }
    }

    override fun getItemCount(): Int = days.size

    fun updateData(newDays: List<String>) {
        days = newDays
        selectedPosition = -1 // Reset lựa chọn khi đổi tháng
        notifyDataSetChanged()
    }
}