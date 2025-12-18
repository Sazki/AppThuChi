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


    private var selectedPosition = -1

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val container: View = itemView
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


            if (position == selectedPosition) {
                holder.container.setBackgroundResource(R.drawable.bg_rounded_green)
                holder.tvDay.setTextColor(Color.WHITE)
            } else {
                holder.container.setBackgroundColor(Color.TRANSPARENT)
                holder.tvDay.setTextColor(Color.BLACK)
            }


            holder.itemView.setOnClickListener {
                val previousItem = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousItem)
                notifyItemChanged(selectedPosition)
                onDayClick(day)
            }
        }
    }

    override fun getItemCount(): Int = days.size

    fun updateData(newDays: List<String>) {
        days = newDays
        selectedPosition = -1
        notifyDataSetChanged()
    }
}