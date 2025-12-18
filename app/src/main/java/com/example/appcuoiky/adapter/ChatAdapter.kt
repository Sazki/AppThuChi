package com.example.appcuoiky.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R
import com.example.appcuoiky.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun getMessages() = messages.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutUserMessage: LinearLayout = itemView.findViewById(R.id.layoutUserMessage)
        private val layoutAiMessage: LinearLayout = itemView.findViewById(R.id.layoutAiMessage)
        private val textUserMessage: TextView = itemView.findViewById(R.id.textUserMessage)
        private val textAiMessage: TextView = itemView.findViewById(R.id.textAiMessage)
        private val textTimestamp: TextView = itemView.findViewById(R.id.textTimestamp)

        fun bind(message: ChatMessage) {
            if (message.isUser) {
                layoutUserMessage.visibility = View.VISIBLE
                layoutAiMessage.visibility = View.GONE
                textUserMessage.text = message.message
            } else {
                layoutUserMessage.visibility = View.GONE
                layoutAiMessage.visibility = View.VISIBLE
                textAiMessage.text = message.message
                textTimestamp.text = timeFormat.format(Date(message.timestamp))
            }
        }
    }
}