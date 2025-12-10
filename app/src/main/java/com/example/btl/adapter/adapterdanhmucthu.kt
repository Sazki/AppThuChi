package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
import com.example.btl.database.viewmodel.danhmucthu

class adapterdanhmucthu(
    private var list: MutableList<danhmucthu>,
    private val onSelected: (danhmucthu) -> Unit,
    private val onAddClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM = 1
    private val TYPE_ADD = 2

    private var selectedIndex: Int = -1

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutItem: LinearLayout = itemView.findViewById(R.id.layoutItem1)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon1)
        val tvName: TextView = itemView.findViewById(R.id.tvName1)
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnAdd: TextView = itemView.findViewById(R.id.btnAdd1)
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_ADD else TYPE_ITEM
    }

    override fun getItemCount(): Int = list.size + 1   // +1 cho nút Thêm


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_danhmucthu, parent, false)
            CategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_addthu, parent, false)
            AddViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CategoryViewHolder && position < list.size) {
            val item = list[position]

            holder.tvName.text = item.name1
            holder.imgIcon.setImageResource(item.iconRes1)

            // đổi background khi chọn
            if (item.isSelected1)
                holder.layoutItem.setBackgroundResource(R.drawable.border5)
            else
                holder.layoutItem.setBackgroundResource(R.drawable.border7)

            holder.itemView.setOnClickListener {
                // bỏ chọn cũ
                if (selectedIndex != -1) {
                    list[selectedIndex].isSelected1 = false
                    notifyItemChanged(selectedIndex)
                }

                // chọn mới
                item.isSelected1 = true
                selectedIndex = holder.adapterPosition
                notifyItemChanged(selectedIndex)

                onSelected(item)
            }
        }

        if (holder is AddViewHolder) {
            holder.btnAdd.setOnClickListener {
                onAddClicked()
            }
        }
    }

    fun addCategory(newItem: danhmucthu) {
        list.add(newItem)
        notifyItemInserted(list.size - 1)
    }
}


