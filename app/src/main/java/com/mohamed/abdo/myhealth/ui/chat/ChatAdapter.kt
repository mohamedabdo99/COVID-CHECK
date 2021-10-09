package com.mohamed.abdo.myhealth.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mohamed.abdo.myhealth.R
import com.mohamed.abdo.myhealth.pojo.MessageModel
import com.mohamed.abdo.myhealth.utils.Constants.RECEIVE_ID
import com.mohamed.abdo.myhealth.utils.Constants.SEND_ID

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    var messageList = mutableListOf<MessageModel>()

    fun setMessageList(messageModel: MessageModel){
        this.messageList.add(messageModel)
        notifyItemInserted(messageList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item,parent,false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int)
    {
        val currentMessage = messageList[position]

        when(currentMessage.id)
        {
            SEND_ID->{
                holder.tvMessage.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                holder.tvBotMessage.visibility = View.GONE
            }
            RECEIVE_ID->{
               holder.tvBotMessage.apply {
                   text = currentMessage.message
                   visibility = View.VISIBLE
               }
                holder.tvMessage.visibility = View.GONE
            }

        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvMessage : TextView
        lateinit var tvBotMessage : TextView
    init {
        tvMessage = itemView.findViewById(R.id.tv_message)
        tvBotMessage = itemView.findViewById(R.id.tv_bot_message)
            itemView.setOnClickListener { v->
                messageList.removeAt(bindingAdapterPosition)
                notifyItemRemoved(bindingAdapterPosition)
            }
    }
    }

}