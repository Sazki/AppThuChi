package com.example.btl.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.btl.R
import com.example.btl.database.viewmodel.danhmucthu


class adapterdanhmucthu (
    private var list: MutableList<danhmucthu>,
    private val onSelected: (danhmucthu) -> Unit
) : RecyclerView.Adapter<adapterdanhmucthu.CategoryViewHolder>() {

    private var selectedIndex: Int = -1

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutItem = itemView.findViewById<LinearLayout>(R.id.layoutItem1)
        val imgIcon = itemView.findViewById<ImageView>(R.id.imgIcon1)
        val tvName = itemView.findViewById<TextView>(R.id.tvName1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_danhmucthu, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = list[position]

        holder.tvName.text = item.name1
        holder.imgIcon.setImageResource(item.iconRes1)

        // đổi nền
        if (item.isSelected1)
            holder.layoutItem.setBackgroundResource(R.drawable.border5)
        else
            holder.layoutItem.setBackgroundResource(R.drawable.border7)

        // sự kiện click
        holder.itemView.setOnClickListener {
            if (selectedIndex != -1) {
                list[selectedIndex].isSelected1 = false
                notifyItemChanged(selectedIndex)
            }

            item.isSelected1 = true
            selectedIndex = holder.adapterPosition
            notifyItemChanged(selectedIndex)

            onSelected(item)
        }
    }
}

