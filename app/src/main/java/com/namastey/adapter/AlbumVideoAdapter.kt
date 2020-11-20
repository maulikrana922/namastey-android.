package com.namastey.adapter

import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnVideoClick
import com.namastey.model.VideoBean
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.row_album_video.view.*


class AlbumVideoAdapter(
    var videoList: ArrayList<VideoBean>,
    val activity: Activity,
    var onVideoClick: OnVideoClick,
    var sessionManager: SessionManager
) : RecyclerView.Adapter<AlbumVideoAdapter.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    val handlerVideo = Handler(activity.mainLooper)

    var isDisplayDetails = true
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_album_video, parent, false
        )
    )

    override fun getItemCount() = videoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val videoBean = videoList[position]
            handlerVideo.removeCallbacksAndMessages(null)

            if (!videoBean.video_url.isNullOrEmpty()) {

                postVideo.setVideoPath(videoBean.video_url)
                postVideo.requestFocus()
                postVideo.start()

//                postVideo.seekTo(1)

                postVideo.setOnPreparedListener { mp ->
                    //Start Playback
//                    ivVideoThumb.visibility = View.GONE

                    postVideo.start()
                    handlerVideo.postDelayed({
                        onVideoClick.onPostViewer(videoBean.id)
                    }, 5000)

                    //Loop Video
                    mp!!.isLooping = true
                }
            }

            if (isDisplayDetails)
                viewDetailsVideo.visibility = View.VISIBLE
            else
                viewDetailsVideo.visibility = View.GONE

            if (sessionManager.getUserId() == videoBean.user_id)
                tvFeedLike.text = activity.getString(R.string.edit)
            else
                tvFeedLike.text = activity.getString(R.string.like)

            tvFeedDesc.text = videoBean.description
            tvFeedView.text = videoBean.viewers.toString()
            if (videoBean.is_comment == 1) {
                tvCommentFeed.text = activity.getString(R.string.comments_off)
            } else {
                tvCommentFeed.text = videoBean.comments.toString().plus(" ")
                    .plus(activity.getString(R.string.comments))
                when {
                    videoBean.profile_pic.size >= 3 -> {
                        ivCommentFirst.visibility = View.VISIBLE
                        ivCommentSecond.visibility = View.VISIBLE
                        ivCommentThird.visibility = View.VISIBLE

                        GlideLib.loadImage(activity, ivCommentFirst, videoBean.profile_pic[0])
                        GlideLib.loadImage(activity, ivCommentSecond, videoBean.profile_pic[1])
                        GlideLib.loadImage(activity, ivCommentThird, videoBean.profile_pic[2])
                    }
                    videoBean.profile_pic.size == 2 -> {
                        ivCommentFirst.visibility = View.VISIBLE
                        ivCommentSecond.visibility = View.VISIBLE
                        ivCommentThird.visibility = View.GONE

                        GlideLib.loadImage(activity, ivCommentFirst, videoBean.profile_pic[0])
                        GlideLib.loadImage(activity, ivCommentSecond, videoBean.profile_pic[1])
                    }
                    videoBean.profile_pic.size == 1 -> {
                        ivCommentFirst.visibility = View.VISIBLE
                        ivCommentSecond.visibility = View.GONE
                        ivCommentThird.visibility = View.GONE
                        GlideLib.loadImage(activity, ivCommentFirst, videoBean.profile_pic[0])

                    }
                    else -> {
                        ivCommentFirst.visibility = View.GONE
                        ivCommentSecond.visibility = View.GONE
                        ivCommentThird.visibility = View.GONE
                    }
                }
            }


            tvFeedLike.setOnClickListener {
                if (sessionManager.getUserId() == videoBean.user_id) {
                    onVideoClick.onPostEdit(position, videoBean)
                } else {
                    onVideoClick.onClickLike(position, videoBean)
                }
            }
            mainViewHolder.setOnClickListener {
                isDisplayDetails = true
                viewDetailsVideo.visibility = View.VISIBLE
                onVideoClick.onVideoClick()
            }
            tvVideoUpNext.setOnClickListener {
                isDisplayDetails = false
                viewDetailsVideo.visibility = View.GONE
                onVideoClick.onUpnextClick(position)
            }

            if (videoBean.is_comment == 0) {
                tvCommentFeed.setOnClickListener {
                    onVideoClick.onCommentClick(videoBean.id)
                }
                ivCommentFirst.setOnClickListener {
                    onVideoClick.onCommentClick(videoBean.id)
                }
                ivCommentSecond.setOnClickListener {
                    onVideoClick.onCommentClick(videoBean.id)
                }
                ivCommentThird.setOnClickListener {
                    onVideoClick.onCommentClick(videoBean.id)
                }

            }
            tvFeedShare.setOnClickListener {
                onVideoClick.onShareClick(videoBean)
            }

        }

    }
}