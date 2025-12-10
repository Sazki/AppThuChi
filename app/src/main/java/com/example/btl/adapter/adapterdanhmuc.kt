package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.R
import com.example.btl.database.viewmodel.danhmuc

class adapterdanhmuc(
    private var list: MutableList<danhmuc>,
    private val onSelected: (danhmuc) -> Unit,
    private val onAddClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM = 1
    private val TYPE_ADD = 2

    private var selectedIndex: Int = -1

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutItem: LinearLayout = itemView.findViewById(R.id.layoutItem)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnAdd: TextView = itemView.findViewById(R.id.btnAdd)
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) TYPE_ADD else TYPE_ITEM
    }

    override fun getItemCount(): Int = list.size + 1   // +1 cho nút Thêm


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_danhmuc, parent, false)
            CategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add, parent, false)
            AddViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CategoryViewHolder && position < list.size) {
            val item = list[position]

            holder.tvName.text = item.name
            holder.imgIcon.setImageResource(item.iconRes)

            // đổi background khi chọn
            if (item.isSelected)
                holder.layoutItem.setBackgroundResource(R.drawable.border5)
            else
                holder.layoutItem.setBackgroundResource(R.drawable.border7)

            holder.itemView.setOnClickListener {
                // bỏ chọn cũ
                if (selectedIndex != -1) {
                    list[selectedIndex].isSelected = false
                    notifyItemChanged(selectedIndex)
                }

                // chọn mới
                item.isSelected = true
                selectedIndex = holder.adapterPosition
                notifyItemChanged(selectedIndex)

                onSelected(item)
            }
        }

        // ----- NÚT THÊM -----
        if (holder is AddViewHolder) {
            holder.btnAdd.setOnClickListener {
                onAddClicked()
            }
        }
    }

    // -------------------- HÀM THÊM DANH MỤC --------------------

    fun addCategory(newItem: danhmuc) {
        list.add(newItem)
        notifyItemInserted(list.size - 1)
    }
}


