package com.example.appcuoiky.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R
import com.example.appcuoiky.model.Transaction
import java.text.DecimalFormat

class TransactionAdapter(
    private var transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val formatter = DecimalFormat("#,### đ")

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvCategory.text = if (transaction.content.isNotEmpty()) transaction.content else "Không có danh mục"
        holder.tvNote.text = if (transaction.note.isNotEmpty()) transaction.note else "Không có ghi chú"

        val amountText = formatter.format(transaction.amount)

        if (transaction.type == "CHI") {
            holder.tvAmount.setTextColor(Color.parseColor("#D32F2F"))
            holder.tvAmount.text = "-$amountText"
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#388E3C"))
            holder.tvAmount.text = "+$amountText"
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}