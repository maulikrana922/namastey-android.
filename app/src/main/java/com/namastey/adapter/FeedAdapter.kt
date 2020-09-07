package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnFeedItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.CustomCommonAlertDialog
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.row_feed.view.*

class FeedAdapter(
    var feedList: ArrayList<DashboardBean>,
    val activity: Activity,
    var onFeedItemClick: OnFeedItemClick,
    var sessionManager: SessionManager
) : androidx.recyclerview.widget.RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_feed, parent, false
        )
    )

    override fun getItemCount() = feedList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val dashboardBean = feedList[position]

            if (!dashboardBean.video_url.isNullOrEmpty()) {
//                postVideo.setVideoPath(dashboardBean.video_url)
//                val uri = Uri.parse("https://testyourapp.online/namasteyapp/public/uploads/post_video/c24de581af92c8a9e32a47a84a255d45.mp4")
//            val mediaController = MediaController(activity)
//            mediaController.setAnchorView(postVideo)
//            postVideo.setMediaController(mediaController)

//                var uri = Uri.parse("android.resource://" + activity.packageName + "/" + R.raw.signupvideo);

//            postVideo.setVideoURI(Uri.parse("http://testyourapp.online/namasteyapp/public/uploads/post_video/c24de581af92c8a9e32a47a84a255d45.mp4"))

//                postVideo.setVideoURI(Uri.parse(dashboardBean.video_url))
                postVideo.setVideoPath(dashboardBean.video_url)
                postVideo.requestFocus()
                postVideo.start()


                postVideo.setOnPreparedListener { mp ->
                    //Start Playback
                    postVideo.start()
                    //Loop Video
                    mp!!.isLooping = true;
                }
            }

            if (dashboardBean.is_comment == 1) {
                tvCommentFeed.text = activity.getString(R.string.comments_off)
            } else {
                tvCommentFeed.text = dashboardBean.comments.toString().plus(" ")
                    .plus(activity.getString(R.string.comments))
            }

            when {
                dashboardBean.is_comment == 1 -> {
                    ivCommentFirst.visibility = View.GONE
                    ivCommentSecond.visibility = View.GONE
                    ivCommentThird.visibility = View.GONE
                }
                dashboardBean.profile_pic.size >= 3 -> {
                    ivCommentFirst.visibility = View.VISIBLE
                    ivCommentSecond.visibility = View.VISIBLE
                    ivCommentThird.visibility = View.VISIBLE

                    GlideLib.loadImage(activity, ivCommentFirst, dashboardBean.profile_pic[0])
                    GlideLib.loadImage(activity, ivCommentSecond, dashboardBean.profile_pic[1])
                    GlideLib.loadImage(activity, ivCommentThird, dashboardBean.profile_pic[2])
                }
                dashboardBean.profile_pic.size == 2 -> {
                    ivCommentFirst.visibility = View.VISIBLE
                    ivCommentSecond.visibility = View.VISIBLE
                    ivCommentThird.visibility = View.GONE

                    GlideLib.loadImage(activity, ivCommentFirst, dashboardBean.profile_pic[0])
                    GlideLib.loadImage(activity, ivCommentSecond, dashboardBean.profile_pic[1])
                }
                dashboardBean.profile_pic.size == 1 -> {
                    ivCommentFirst.visibility = View.VISIBLE
                    ivCommentSecond.visibility = View.GONE
                    ivCommentThird.visibility = View.GONE
                    GlideLib.loadImage(activity, ivCommentFirst, dashboardBean.profile_pic[0])

                }
                else -> {
                    ivCommentFirst.visibility = View.GONE
                    ivCommentSecond.visibility = View.GONE
                    ivCommentThird.visibility = View.GONE
                }
            }
            if (dashboardBean.username.isNotEmpty())
                tvFeedName.text = dashboardBean.username

            if (dashboardBean.profile_url.isNotEmpty())
                GlideLib.loadImage(activity, ivFeedProfile, dashboardBean.profile_url)

//            if (dashboardBean.is_like == 1)
//                tvFeedLike.text = activity.getString(R.string.dislike)
//            else
//                tvFeedLike.text = activity.getString(R.string.like)

            tvFeedJob.text = dashboardBean.job

            tvFeedDesc.text = dashboardBean.description
            tvFeedView.text = dashboardBean.viewers.toString()

            GlideLib.loadImageUrlRound(activity, ivCommentFirst, "")
            GlideLib.loadImageUrlRound(activity, ivCommentSecond, "")
            GlideLib.loadImageUrlRound(activity, ivCommentThird, "")
            GlideLib.loadImageUrlRound(activity, ivCommentFourth, "")

            if (dashboardBean.is_follow == 1) {
                ivFeedFollow.setImageResource(R.drawable.ic_add_right)
            } else {
                ivFeedFollow.setImageResource(R.drawable.ic_add_follow_from_profile)
            }
            // Need to change as per api response
//            ivFeedFollow.tag = R.drawable.ic_add_follow_from_profile
            ivFeedFollow.setOnClickListener {
                var isFollow = 0
                val msg: String
                val btnText: String
                if (dashboardBean.is_follow == 1) {
                    isFollow = 0
                    msg = resources.getString(R.string.msg_remove_post)
                    btnText = resources.getString(R.string.remove)
//                    ivFeedFollow.setImageResource(R.drawable.ic_add_follow_from_profile)
                } else {
                    isFollow = 1
                    msg = resources.getString(R.string.msg_send_follow_request)
                    btnText = resources.getString(R.string.send)
//                    ivFeedFollow.setImageResource(R.drawable.ic_add_right)
                }
                object : CustomCommonAlertDialog(
                    activity,
                    dashboardBean.username,
                    msg,
                    dashboardBean.profile_url,
                    btnText
                ) {
                    override fun onBtnClick(id: Int) {
                        when (id) {
                            btnAlertOk.id -> {
                                onFeedItemClick.onClickFollow(
                                    position,
                                    dashboardBean.user_id,
                                    isFollow
                                )
                            }
                        }
                    }
                }.show()

            }

            tvFeedShare.setOnClickListener {
                onFeedItemClick.onItemClick(dashboardBean)
            }

            tvCommentFeed.setOnClickListener {
                if (dashboardBean.is_comment == 0 && !sessionManager.isGuestUser())
                    onFeedItemClick.onCommentClick(dashboardBean.id)
            }

            ivFeedProfile.setOnClickListener {
                onFeedItemClick.onUserProfileClick(dashboardBean.user_id)
            }
//            tvFeedLike.setOnClickListener {
//                if (dashboardBean.is_like == 1)
//                    onFeedItemClick.onProfileLikeClick(position, dashboardBean.user_id, 0)
//                else
//                    onFeedItemClick.onProfileLikeClick(position, dashboardBean.user_id, 1)
//            }
        }

    }
}