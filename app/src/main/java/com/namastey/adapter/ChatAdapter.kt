package com.namastey.adapter

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.namastey.R
import com.namastey.listeners.OnChatMessageClick
import com.namastey.model.ChatMessage
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_message_received.view.*
import kotlinx.android.synthetic.main.row_message_send.view.*
import java.util.*

class ChatAdapter(
    var activity: Activity,
    var userId: Long,
    var chatMsgList: ArrayList<ChatMessage>,
    var onChatMessageClick: OnChatMessageClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RIGHT_CHAT = 1
    private val LEFT_CHAT = 2
    private var currentPlayingPosition = -1
    private var mediaPlayer: MediaPlayer? = null

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
        if (chatMsgList[position].sender == userId) {
            val messageSendProfileViewHolder = holder as MessageSendProfileViewHolder
            messageSendProfileViewHolder.bindSend(position)
        } else {
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

    inner class MessageReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindReceived(position: Int) = with(itemView) {

            val chatMessage = chatMsgList[position]
            Log.e("ChatAdapter", "MessageReceiveViewHolder: ReceiverId: \t ${chatMessage.receiver}")
            Log.e("ChatAdapter", "MessageReceiveViewHolder: SenderId: \t ${chatMessage.sender}")

            if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_IMAGE && chatMessage.url.isNotEmpty()) {
                visibleReceiveMessage(
                    flImageReceived,
                    llMessageReceived,
                    llRecordingReceived,
                    flImageReceived
                )
                GlideLib.loadImage(activity, ivImageReceived, chatMessage.url)
                tvImageReceivedTime.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
            } else if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_VOICE && chatMessage.url.isNotEmpty()) {
                visibleReceiveMessage(
                    flImageReceived,
                    llMessageReceived,
                    llRecordingReceived,
                    llRecordingReceived
                )
                tvRecordingTimeReceived.text =
                    Utils.convertTimestampToChatFormat(chatMessage.timestamp)
                tvRecordedDurationReceived.text = Utils.getMediaDuration(chatMessage.url)
            } else {
                visibleSendMessage(
                    flImageReceived,
                    llMessageReceived,
                    llRecordingReceived,
                    llMessageReceived
                )
                // tvMessageReceived.text = chatMessage.message
                Utils.setHtmlText(tvMessageReceived, chatMessage.message)
                tvMessageReceived.setOnClickListener {
                    onChatMessageClick.onChatMessageClick(chatMessage.message, position)
                }
                tvMessageReceivedTime.text =
                    Utils.convertTimestampToMessageFormat(chatMessage.timestamp)

            }

            if (currentPlayingPosition != -1) {
                if (position == currentPlayingPosition) {
                    updatePlayingView(ivRecordReceived)
                } else {
                    updateNonPlayingView(ivRecordReceived)
                }
            }
            ivImageReceived.setOnClickListener {
                onChatMessageClick.onChatImageClick(chatMessage.url)
            }
            ivRecordReceived.setOnClickListener {
                if (currentPlayingPosition != -1)
                    notifyItemChanged(currentPlayingPosition)

                if (adapterPosition != currentPlayingPosition) {
                    currentPlayingPosition = adapterPosition
                    if (mediaPlayer != null) {
                        updateNonPlayingView(ivRecordReceived)
                        mediaPlayer!!.release()
                    }
                    ivRecordReceived.setImageResource(R.drawable.ic_pause)
                    startMediaPlayer(chatMessage.url, adapterPosition, ivRecordReceived)
                } else {
                    if (mediaPlayer != null) {
                        if (mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.pause()
                        } else {
                            mediaPlayer!!.start()
                        }
                    }
                }
            }
        }
    }

    inner class MessageSendProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindSend(position: Int) = with(itemView) {

            val chatMessage = chatMsgList[position]
            Log.e(
                "ChatAdapter",
                "MessageSendProfileViewHolder: ReceiverId: \t ${chatMessage.receiver}"
            )
            Log.e("ChatAdapter", "MessageSendProfileViewHolder: SenderId: \t ${chatMessage.sender}")
            if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_IMAGE && chatMessage.url.isNotEmpty()) {
                visibleSendMessage(flImageSend, llMessageSend, llRecordingSend, flImageSend)
                GlideLib.loadImage(activity, ivImageSend, chatMessage.url)
                tvImageSendTime.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
            } else if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_VOICE && chatMessage.url.isNotEmpty()) {
                visibleSendMessage(flImageSend, llMessageSend, llRecordingSend, llRecordingSend)
                tvRecordingTimeSend.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
                tvRecordedDurationSend.text = Utils.getMediaDuration(chatMessage.url)
            } else {
                visibleSendMessage(flImageSend, llMessageSend, llRecordingSend, llMessageSend)
                tvMessageSend.text = chatMessage.message
                Utils.setHtmlText(tvMessageSend,chatMessage.message)
                tvMessageSend.setOnClickListener {
                    onChatMessageClick.onChatMessageClick(chatMessage.message, position)
                }
                tvMessageSendTime.text = Utils.convertTimestampToMessageFormat(chatMessage.timestamp)
            }

            if (currentPlayingPosition != -1) {
                if (position == currentPlayingPosition) {
                    updatePlayingView(ivRecordSend)
                } else {
                    updateNonPlayingView(ivRecordSend)
                }
            }

            ivImageSend.setOnClickListener {
                onChatMessageClick.onChatImageClick(chatMessage.url)
            }
            ivRecordSend.setOnClickListener {
                if (currentPlayingPosition != -1)
                    notifyItemChanged(currentPlayingPosition)

                if (adapterPosition != currentPlayingPosition) {
                    currentPlayingPosition = adapterPosition
                    if (mediaPlayer != null) {
                        updateNonPlayingView(ivRecordSend)
                        mediaPlayer!!.release()
                    }
                    ivRecordSend.setImageResource(R.drawable.ic_pause)
                    startMediaPlayer(chatMessage.url, adapterPosition, ivRecordSend)
                } else {
                    if (mediaPlayer != null) {
                        if (mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.pause()
                        } else {
                            mediaPlayer!!.start()
                        }
                    }
                }
            }
        }
    }

    private fun startMediaPlayer(
        audioUrl: String,
        adapterPosition: Int,
        ivRecordSend: ImageView
    ) {
        val audioUri = Uri.parse(audioUrl)
        mediaPlayer = MediaPlayer.create(getApplicationContext(), audioUri)
        mediaPlayer!!.setOnCompletionListener {
            releaseMediaPlayer(
                currentPlayingPosition,
                ivRecordSend
            )
        }
        mediaPlayer!!.start()
    }

    private fun releaseMediaPlayer(
        adapterPosition: Int,
        ivRecord: ImageView
    ) {
        updateNonPlayingView(ivRecord)

        mediaPlayer!!.release()
        mediaPlayer = null
        currentPlayingPosition = -1
    }

    private fun updateNonPlayingView(ivRecord: ImageView) {
        ivRecord.setImageResource(R.drawable.ic_play)
    }

    private fun updatePlayingView(ivRecord: ImageView) {
        if (mediaPlayer!!.isPlaying) {
            ivRecord.setImageResource(R.drawable.ic_pause)
        } else {
            ivRecord.setImageResource(R.drawable.ic_play)
        }
    }

    /**
     * Visible particular view of send message type
     */
    private fun visibleSendMessage(
        flImageSend: FrameLayout,
        llMessageSend: LinearLayout,
        llRecordingSend: LinearLayout,
        view: View
    ) {
        flImageSend.visibility = View.GONE
        llMessageSend.visibility = View.GONE
        llRecordingSend.visibility = View.GONE

        view.visibility = View.VISIBLE
    }

    /**
     * Visible particular view of receive message type
     */
    private fun visibleReceiveMessage(
        flImageReceived: FrameLayout,
        llMessageReceived: LinearLayout,
        llRecordingReceived: LinearLayout,
        view: View
    ) {
        flImageReceived.visibility = View.GONE
        llMessageReceived.visibility = View.GONE
        llRecordingReceived.visibility = View.GONE

        view.visibility = View.VISIBLE
    }

    override fun getItemCount() = chatMsgList.size
}