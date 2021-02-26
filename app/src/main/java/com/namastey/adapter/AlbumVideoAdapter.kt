package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.appcompat.widget.SocialTextView
import com.namastey.R
import com.namastey.activity.ProfileViewActivity
import com.namastey.listeners.OnVideoClick
import com.namastey.model.VideoBean
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager


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
        holder.bind(position, activity)
    }

    inner class ViewHolder(@param:NonNull private val parent: View) :
        RecyclerView.ViewHolder(parent) {

        /* inner class ViewHolder(itemView: View) :
             RecyclerView.ViewHolder(itemView) {*/

        lateinit var viewDetailsVideo: ConstraintLayout
        lateinit var mainViewHolder: ConstraintLayout
        lateinit var mediaContainer: FrameLayout

        lateinit var mediaCoverImage: ImageView
        lateinit var ivCommentFirst: ImageView
        lateinit var ivCommentSecond: ImageView
        lateinit var ivCommentThird: ImageView

        lateinit var tvFeedLike: TextView
        lateinit var tvFeedShare: TextView
        lateinit var tvFeedDesc: SocialTextView
        lateinit var tvFeedView: TextView
        lateinit var tvCommentFeed: TextView
        lateinit var tvVideoUpNext: TextView


        init {
            viewDetailsVideo = parent.findViewById(R.id.viewDetailsVideo)
            mainViewHolder = parent.findViewById(R.id.mainViewHolder)
            mediaContainer = parent.findViewById(R.id.mediaContainer)

            mediaCoverImage = parent.findViewById(R.id.ivMediaCoverImage)
            ivCommentFirst = parent.findViewById(R.id.ivCommentFirst)
            ivCommentSecond = parent.findViewById(R.id.ivCommentSecond)
            ivCommentThird = parent.findViewById(R.id.ivCommentThird)

            tvFeedLike = parent.findViewById(R.id.tvFeedLike)
            tvFeedShare = parent.findViewById(R.id.tvFeedShare)
            tvFeedDesc = parent.findViewById(R.id.tvFeedDesc)
            tvFeedView = parent.findViewById(R.id.tvFeedView)
            tvCommentFeed = parent.findViewById(R.id.tvCommentFeed)
            tvVideoUpNext = parent.findViewById(R.id.tvVideoUpNext)

        }

        fun bind(position: Int, context: Context) /*= with(itemView) */ {
            parent!!.tag = this

            val videoBean = videoList[position]
            handlerVideo.removeCallbacksAndMessages(null)

           /* if (!videoBean.video_url.isNullOrEmpty()) {

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
            }*/

            if (!videoBean.video_url.isNullOrEmpty()) {

                if (videoBean.cover_image_url != null && videoBean.cover_image_url != "") {
                    GlideLib.loadImage(activity, mediaCoverImage, videoBean.cover_image_url)
                }

                Log.e("AlbumVideoAdapter", "CoverImageUrl: \t ${videoBean.cover_image_url}")
                Log.e("AlbumVideoAdapter", "VideoUrl: \t ${videoBean.video_url}")

                handlerVideo.postDelayed({
                    onVideoClick.onPostViewer(videoBean.id)
                }, 5000)
            }

            Log.e("AlbumVideoAdapter", "isDisplayDetails: \t $isDisplayDetails")
            if (isDisplayDetails)
                viewDetailsVideo.visibility = View.VISIBLE
            else
                viewDetailsVideo.visibility = View.GONE

            if (sessionManager.getUserId() == videoBean.user_id) {
                tvFeedLike.text = activity.getString(R.string.edit)
            } else {
                //tvFeedLike.text = activity.getString(R.string.like)
                if (videoBean.is_like == 1)
                    tvFeedLike.text = activity.getString(R.string.liked)
                else
                    tvFeedLike.text = activity.getString(R.string.like)
            }

            /*if (videoBean.is_like == 1)
                tvFeedLike.text = activity.getString(R.string.liked)
            else
                tvFeedLike.text = activity.getString(R.string.like)*/
            tvFeedShare.text = videoBean.share.toString()
            tvFeedDesc.text = videoBean.description
            tvFeedDesc.mentionColor = ContextCompat.getColor(context, R.color.colorBlueLight)
            tvFeedDesc.setOnMentionClickListener { view, text ->
                Log.e("AlbumVideoAdapter", "setOnMentionClickListener: $text")
                Log.e("AlbumVideoAdapter", "setOnMentionClickListener: ${view.mentions}")
                val intent = Intent(context, ProfileViewActivity::class.java)
                intent.putExtra(Constants.USERNAME, text.toString())
                context.startActivity(intent)
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
            }

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
                    //  onVideoClick.onClickLike(position, videoBean)
                    if (videoBean.is_like == 1)
                        onVideoClick.onClickLike(position, videoBean, 0)
                    else
                        onVideoClick.onClickLike(position, videoBean, 1)
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
                onVideoClick.onShareClick(position, videoBean)
            }

        }

    }
}