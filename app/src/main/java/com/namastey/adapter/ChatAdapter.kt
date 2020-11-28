package com.namastey.adapter

import android.app.Activity
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.namastey.R
import com.namastey.model.ChatMessage
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_message_received.view.*
import kotlinx.android.synthetic.main.row_message_send.view.*
import java.util.concurrent.TimeUnit

class ChatAdapter(
    var activity: Activity,
    var userId : Long,
    var chatMsgList: ArrayList<ChatMessage>
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
            if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_IMAGE && chatMessage.url.isNotEmpty()){
                visibleReceiveMessage(flImageReceived,llMessageReceived,llRecordingReceived,flImageReceived)
                GlideLib.loadImage(activity,ivImageReceived,chatMessage.url)
                tvImageReceivedTime.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
            }else if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_VOICE && chatMessage.url.isNotEmpty()){
                visibleReceiveMessage(flImageReceived,llMessageReceived,llRecordingReceived,llRecordingReceived)
                tvRecordingTimeReceived.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
                tvRecordedDurationReceived.text = getMediaDuration(chatMessage.url)
            }else {
                visibleSendMessage(flImageReceived,llMessageReceived,llRecordingReceived,llMessageReceived)
                tvMessageReceived.text = chatMessage.message
                tvMessageReceivedTime.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
            }

            if (currentPlayingPosition != -1) {
                if (position == currentPlayingPosition) {
                    updatePlayingView(ivRecordReceived)
                } else {
                    updateNonPlayingView(ivRecordReceived)
                }
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
                    startMediaPlayer(chatMessage.url,adapterPosition,ivRecordReceived)
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

    inner class MessageSendProfileViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindSend(position: Int) = with(itemView){

            val chatMessage = chatMsgList[position]
            if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_IMAGE && chatMessage.url.isNotEmpty()){
                visibleSendMessage(flImageSend,llMessageSend,llRecordingSend,flImageSend)
                GlideLib.loadImage(activity,ivImageSend,chatMessage.url)
                tvImageSendTime.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
            }else if (chatMessage.message == Constants.FirebaseConstant.MSG_TYPE_VOICE && chatMessage.url.isNotEmpty()){
                visibleSendMessage(flImageSend,llMessageSend,llRecordingSend,llRecordingSend)
                tvRecordingTimeSend.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
                tvRecordedDurationSend.text = getMediaDuration(chatMessage.url)
            }else {
                visibleSendMessage(flImageSend,llMessageSend,llRecordingSend,llMessageSend)
                tvMessageSend.text = chatMessage.message
                tvMessageSendTime.text = Utils.convertTimestampToChatFormat(chatMessage.timestamp)
            }

            if (currentPlayingPosition != -1) {
                if (position == currentPlayingPosition) {
                    updatePlayingView(ivRecordSend)
                } else {
                    updateNonPlayingView(ivRecordSend)
                }
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
                    startMediaPlayer(chatMessage.url,adapterPosition,ivRecordSend)
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
        mediaPlayer!!.setOnCompletionListener { releaseMediaPlayer(currentPlayingPosition,ivRecordSend) }
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

    /**
     * Get duration of audio from firebase url
     */
    fun getMediaDuration(voiceUrl: String): String {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(voiceUrl)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            String.format("%02d:%02d ",
                duration?.toLong()?.let { TimeUnit.MILLISECONDS.toMinutes(it) },
                duration?.toLong()?.let { TimeUnit.MILLISECONDS.toSeconds(it) }
                    ?.minus(TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong())))
            )
        }catch (e: Exception){
            e.printStackTrace()
            "00:00"
        }
//        return duration?.toLongOrNull() ?: 0
    }
}