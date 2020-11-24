package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.model.ChatMessage
import kotlinx.android.synthetic.main.row_message_received.view.*
import kotlinx.android.synthetic.main.row_message_send.view.*

class ChatAdapter(
    var activity: Activity,
    var userId : Long,
    var chatMsgList: ArrayList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RIGHT_CHAT = 1
    private val LEFT_CHAT = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LEFT_CHAT) {
            val layoutOne: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_message_received, parent, false)
            MessageReceiveViewHolder(layoutOne)
        } else {
            val layoutTwo: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_message_send, parent, false)
            MessageSendProfileViewHolder(layoutTwo)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(chatMsgList[position].sender == userId) {
            val messageSendProfileViewHolder = holder as MessageSendProfileViewHolder
            messageSendProfileViewHolder.bindSend(position)

        }else{
            val messageReceiveViewHolder = holder as MessageReceiveViewHolder
            messageReceiveViewHolder.bindReceived(position)
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (chatMsgList[position].sender == userId) {
            RIGHT_CHAT
        } else {
            LEFT_CHAT
        }

    }

    inner class MessageReceiveViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindReceived(position: Int) = with(itemView){

            val chatMessage = chatMsgList[position]
            llMessageReceived.visibility = View.VISIBLE
            tvMessageReceived.text = chatMessage.message
        }
//        val llMessageReceived: LinearLayout
//        val tvMessageReceived: TextView
//        val tvMessageReceivedTime: TextView
//        val frameImageReceived: FrameLayout
//        val ivImageReceived: ImageView
//        val tvImageReceivedTime: TextView
//        val llRecordingReceived: LinearLayout
//        val ivRecordReceived: ImageView
//        val tvReadableContentReceived: TextView
//        val tvRecordedTimeReceived: TextView
//        val tvRecordingTimeReceived: TextView
//
//        init {
//            llMessageReceived = itemView.findViewById(R.id.llMessageReceived)
//            tvMessageReceived = itemView.findViewById(R.id.tvMessageReceived)
//            tvMessageReceivedTime = itemView.findViewById(R.id.tvMessageReceivedTime)
//            frameImageReceived = itemView.findViewById(R.id.frameImageReceived)
//            ivImageReceived = itemView.findViewById(R.id.ivImageReceived)
//            tvImageReceivedTime = itemView.findViewById(R.id.tvImageReceivedTime)
//            llRecordingReceived = itemView.findViewById(R.id.llRecordingReceived)
//            ivRecordReceived = itemView.findViewById(R.id.ivRecordReceived)
//            tvReadableContentReceived = itemView.findViewById(R.id.tvReadableContentReceived)
//            tvRecordedTimeReceived = itemView.findViewById(R.id.tvRecordedTimeReceived)
//            tvRecordingTimeReceived = itemView.findViewById(R.id.tvRecordingTimeReceived)
//        }
    }

    inner class MessageSendProfileViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindSend(position: Int) = with(itemView){

            val chatMessage = chatMsgList[position]
            llMessageSend.visibility = View.VISIBLE
            tvMessageSend.text = chatMessage.message
        }
//        val llMessageSend: LinearLayout
//        val tvMessageSend: TextView
//        val tvMessageSendTime: TextView
//        val frameImageSend: FrameLayout
//        val ivImageSend: ImageView
//        val tvImageSendTime: TextView
//        val llRecordingSend: LinearLayout
//        val ivRecordSend: ImageView
//        val tvReadableContentSend: TextView
//        val tvRecordedTimeSend: TextView
//        val tvRecordingTimeSend: TextView
//
//        init {
//            llMessageSend = itemView.findViewById(R.id.llMessageSend)
//            tvMessageSend = itemView.findViewById(R.id.tvMessageSend)
//            tvMessageSendTime = itemView.findViewById(R.id.tvMessageSendTime)
//            frameImageSend = itemView.findViewById(R.id.frameImageSend)
//            ivImageSend = itemView.findViewById(R.id.ivImageSend)
//            tvImageSendTime = itemView.findViewById(R.id.tvImageSendTime)
//            llRecordingSend = itemView.findViewById(R.id.llRecordingSend)
//            ivRecordSend = itemView.findViewById(R.id.ivRecordSend)
//            tvReadableContentSend = itemView.findViewById(R.id.tvReadableContentSend)
//            tvRecordedTimeSend = itemView.findViewById(R.id.tvRecordedTimeSend)
//            tvRecordingTimeSend = itemView.findViewById(R.id.tvRecordingTimeSend)
//        }
    }

    override fun getItemCount() = chatMsgList.size
}