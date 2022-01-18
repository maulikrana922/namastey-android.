package com.namastey.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
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
import java.util.concurrent.TimeUnit


class ChatAdapter(
    var activity: Activity,
    var userId: Long,
    var chatMsgList: ArrayList<ChatMessage>,
    var onChatMessageClick: OnChatMessageClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RIGHT_CHAT = 1
    private val LEFT_CHAT = 2
    private var currentPlayingPosition = -1
    private lateinit var mediaPlayer: MediaPlayer

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
            messageReceiveViewHolder.bindReceived(position, holder)
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
        fun bindReceived(position: Int, holder: RecyclerView.ViewHolder) = with(itemView) {
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
                    Utils.convertTimestampToMessageFormat(chatMessage.timestamp)
                tvRecordedDurationReceived.text = chatMessage.duration
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
                    updatePlayingView(itemView.ivRecordReceived)
                } else {
                    updateNonPlayingView(itemView.ivRecordReceived)
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
                    if (::mediaPlayer.isInitialized) {
                        updateNonPlayingView(itemView.ivRecordReceived)
                        mediaPlayer.release()
                    }

                    ivRecordReceived.setImageResource(R.drawable.ic_pause_white)
                    startMediaPlayer(
                        chatMessage.url,
                        adapterPosition,
                        ivRecordReceived,
                        song_seekbar_received
                    )

                    tvRecordedDurationReceived.text = "00:00"
                    Log.e("MAX", mediaPlayer.duration.toString())
                    song_seekbar_received.max = mediaPlayer.duration


                    song_seekbar_received.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            try {
                                tvRecordedDurationReceived.text =
                                    getDurestion(mediaPlayer.currentPosition.toLong()+1)
                                Log.e("Progress", progress.toString())
                            } catch (e: java.lang.Exception) {
                                tvRecordedDurationReceived.text =chatMessage.duration
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    })

                } else {
                    if (::mediaPlayer.isInitialized) {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                        } else {
                            mediaPlayer.start()
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
                tvRecordingTimeSend.text =
                    Utils.convertTimestampToMessageFormat(chatMessage.timestamp)
                tvRecordedDurationSend.text = chatMessage.duration
            } else {
                visibleSendMessage(flImageSend, llMessageSend, llRecordingSend, llMessageSend)
                tvMessageSend.text = chatMessage.message
                Utils.setHtmlText(tvMessageSend, chatMessage.message)
                tvMessageSend.setOnClickListener {
                    onChatMessageClick.onChatMessageClick(chatMessage.message, position)
                }
                tvMessageSendTime.text =
                    Utils.convertTimestampToMessageFormat(chatMessage.timestamp)
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
            song_seekbar_send.thumbTintList = ColorStateList.valueOf(Color.parseColor("#F30D46"))
            song_seekbar_send.progressBackgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#595959"))
            song_seekbar_send.progressTintList = ColorStateList.valueOf(Color.parseColor("#F30D46"))

            ivRecordSend.setOnClickListener {
                if (currentPlayingPosition != -1)
                    notifyItemChanged(currentPlayingPosition)

                if (adapterPosition != currentPlayingPosition) {
                    currentPlayingPosition = adapterPosition
                    if (::mediaPlayer.isInitialized) {
                        updateNonPlayingView(ivRecordSend)
                        mediaPlayer.release()
                    }
                    ivRecordSend.setImageResource(R.drawable.ic_pause_white)
                    startMediaPlayer(
                        chatMessage.url,
                        adapterPosition,
                        ivRecordSend,
                        song_seekbar_send
                    )

                    tvRecordedDurationSend.text = "00:00"
                    Log.e("MAX", mediaPlayer.duration.toString())
                    song_seekbar_send.max = mediaPlayer.duration

                    song_seekbar_send.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            try {
                                tvRecordedDurationSend.text =
                                    getDurestion(mediaPlayer.currentPosition.toLong()+1)
                                Log.e("Progress", progress.toString())
                            } catch (e: java.lang.Exception) {
                                tvRecordedDurationSend.text =chatMessage.duration
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar) {

                        }
                    })

                    /*  val mHandler = Handler(Looper.getMainLooper())
                      activity.runOnUiThread(object : Runnable {
                          override fun run() {
                              try {
                                  val mCurrentPosition: Int =
                                      mediaPlayer.currentPosition
                                  song_seekbar_send.progress = mCurrentPosition
                              } catch (e: Exception) {
                                  song_seekbar_send.progress = 0
                              }
                              mHandler.postDelayed(this, 100)
                          }
                      })*/
                } else {
                    if (::mediaPlayer.isInitialized) {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                        } else {
                            mediaPlayer.start()
                        }
                    }
                }
            }
        }
    }

    private fun startMediaPlayer(
        audioUrl: String,
        adapterPosition: Int,
        ivRecordSend: ImageView, seekBar: SeekBar
    ) {
        if (audioUrl.isNotEmpty()) {
            try {
                val audioUri = Uri.parse(audioUrl)
                mediaPlayer = MediaPlayer.create(getApplicationContext(), audioUri)
                mediaPlayer.setOnCompletionListener {
                    releaseMediaPlayer(
                        currentPlayingPosition,
                        ivRecordSend
                    )
                }
                mediaPlayer.start()
                onChatMessageClick.onItemViewClick(mediaPlayer)
                val mHandler = Handler(Looper.getMainLooper())
                activity.runOnUiThread(object : Runnable {
                    override fun run() {
                        try {
                            val mCurrentPosition: Int =
                                mediaPlayer.currentPosition
                            if (currentPlayingPosition != -1) {
                                if (adapterPosition == currentPlayingPosition) {
                                    seekBar.progress = mCurrentPosition
                                } else {

                                }
                            }

                        } catch (e: Exception) {
                            seekBar.progress = 0
                        }
                        mHandler.postDelayed(this, 100)
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun releaseMediaPlayer(
        adapterPosition: Int,
        ivRecord: ImageView
    ) {
        updateNonPlayingView(ivRecord)

        mediaPlayer.release()
        //mediaPlayer = null
        currentPlayingPosition = -1
    }

    private fun updateNonPlayingView(ivRecord: ImageView) {
        ivRecord.setImageResource(R.drawable.ic_play_white)
    }

    private fun updatePlayingView(ivRecord: ImageView) {
        if (mediaPlayer.isPlaying) {
            ivRecord.setImageResource(R.drawable.ic_pause_white)
        } else {
            ivRecord.setImageResource(R.drawable.ic_play_white)
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


    private fun getDurestion(duration: Long): String {
        return java.lang.String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            duration
                        )
                    )
        )
    }

}