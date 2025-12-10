package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.database.viewmodel.danhmuc
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.btl.R


class adapterdanhmuc (
    private var list: MutableList<danhmuc>,
    private val onSelected: (danhmuc) -> Unit
    ) : RecyclerView.Adapter<adapterdanhmuc.CategoryViewHolder>() {

        private var selectedIndex: Int = -1

        inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val layoutItem = itemView.findViewById<LinearLayout>(R.id.layoutItem)
            val imgIcon = itemView.findViewById<ImageView>(R.id.imgIcon)
            val tvName = itemView.findViewById<TextView>(R.id.tvName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_danhmuc, parent, false)
            return CategoryViewHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            val item = list[position]

            holder.tvName.text = item.name
            holder.imgIcon.setImageResource(item.iconRes)

            // đổi nền
            if (item.isSelected)
                holder.layoutItem.setBackgroundResource(R.drawable.border5)
            else
                holder.layoutItem.setBackgroundResource(R.drawable.border7)

            // sự kiện click
            holder.itemView.setOnClickListener {
                if (selectedIndex != -1) {
                    list[selectedIndex].isSelected = false
                    notifyItemChanged(selectedIndex)
                }

                item.isSelected = true
                selectedIndex = holder.adapterPosition
                notifyItemChanged(selectedIndex)

                onSelected(item)
            }
        }
    }

