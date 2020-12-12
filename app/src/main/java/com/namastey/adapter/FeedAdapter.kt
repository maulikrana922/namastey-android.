package com.namastey.adapter

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.activity.ProfileActivity
import com.namastey.listeners.OnFeedItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.row_feed.view.*

class FeedAdapter(
    var feedList: ArrayList<DashboardBean>,
    val activity: Activity,
    var onFeedItemClick: OnFeedItemClick,
    var sessionManager: SessionManager
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    val handlerVideo = Handler(activity.mainLooper)

    var mCountDownTimer: CountDownTimer? = null
    var i = 0
//    private var timeCountInMilliSeconds: Long = 1 * 60000.toLong()
//
//    private enum class TimerStatus {
//        STARTED, STOPPED
//    }
//
//    private var timerStatus = TimerStatus.STOPPED
//    private var countDownTimer: CountDownTimer? = null

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
        RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) = with(itemView) {
            val dashboardBean = feedList[position]

            handlerVideo.removeCallbacksAndMessages(null)

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

                    handlerVideo.postDelayed({
                        onFeedItemClick.onPostViewer(dashboardBean.id)
                    }, 5000)

                    //Loop Video
                    mp!!.isLooping = true
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

//            if (dashboardBean.profile_url.isNotEmpty())
            GlideLib.loadImage(activity, ivFeedProfile, dashboardBean.profile_url)

            if (dashboardBean.is_like == 1)
                tvFeedLike.text = activity.getString(R.string.liked)
            else
                tvFeedLike.text = activity.getString(R.string.like)

            tvFeedJob.text = dashboardBean.job

            tvFeedDesc.text = dashboardBean.description
            tvFeedView.text = dashboardBean.viewers.toString()

            if (dashboardBean.is_follow == 1) {
                ivFeedFollow.setImageResource(R.drawable.ic_add_right)
            } else {
                ivFeedFollow.setImageResource(R.drawable.ic_add_follow_from_profile)
            }
            // Need to change as per api response
            ivFeedFollow.setOnClickListener {
                if (sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                    var isFollow = 0
                    val msg: String
                    val btnText: String
                    if (dashboardBean.is_follow == 1) {
                        isFollow = 0
                        msg = resources.getString(R.string.msg_remove_post)
                        btnText = resources.getString(R.string.remove)
                    } else {
                        isFollow = 1
                        msg = resources.getString(R.string.msg_send_follow_request)
                        btnText = resources.getString(R.string.send)
                    }
                    object : CustomCommonAlertDialog(
                        activity,
                        dashboardBean.username,
                        msg,
                        dashboardBean.profile_url,
                        btnText,
                        resources.getString(R.string.cancel)
                    ) {
                        override fun onBtnClick(id: Int) {
                            when (id) {
                                btnAlertOk.id -> {
                                    onFeedItemClick.onClickFollow(
                                        position,
                                        dashboardBean,
                                        isFollow
                                    )
                                }
                            }
                        }
                    }.show()
                } else {

                    object : CustomAlertDialog(
                        activity,
                        activity.getString(R.string.complete_profile),
                        activity.getString(R.string.ok),
                        activity.getString(R.string.cancel)
                    ) {
                        override fun onBtnClick(id: Int) {
                            when (id) {
                                btnPos.id -> {
                                    activity.startActivity(
                                        Intent(
                                            activity,
                                            ProfileActivity::class.java
                                        )
                                    )
                                }
                                btnNeg.id -> {
                                    dismiss()
                                }
                            }
                        }
                    }.show()
                }
            }

            tvFeedShare.setOnClickListener {
                onFeedItemClick.onItemClick(dashboardBean)
            }

            // if (dashboardBean.is_comment == 0 && !sessionManager.isGuestUser()) {
            if (dashboardBean.is_comment == 0) {// && !sessionManager.isGuestUser()) {
                tvCommentFeed.setOnClickListener {
                    onFeedItemClick.onCommentClick(position, dashboardBean.id)
                }
                ivCommentFirst.setOnClickListener {
                    onFeedItemClick.onCommentClick(position, dashboardBean.id)
                }
                ivCommentSecond.setOnClickListener {
                    onFeedItemClick.onCommentClick(position, dashboardBean.id)
                }
                ivCommentThird.setOnClickListener {
                    onFeedItemClick.onCommentClick(position, dashboardBean.id)
                }

            }

            ivFeedProfile.setOnClickListener {
                onFeedItemClick.onUserProfileClick(dashboardBean)
            }
            tvFeedName.setOnClickListener {
                onFeedItemClick.onUserProfileClick(dashboardBean)
            }
            tvFeedJob.setOnClickListener {
                onFeedItemClick.onUserProfileClick(dashboardBean)
            }
            tvFeedLike.setOnClickListener {
                if (dashboardBean.is_like == 1)
                    onFeedItemClick.onProfileLikeClick(position, dashboardBean, 0)
                else
                    onFeedItemClick.onProfileLikeClick(position, dashboardBean, 1)
            }
            tvFeedBoost.setOnClickListener {
                onFeedItemClick.onFeedBoost(dashboardBean.user_id)
            }

            if (dashboardBean.is_like == 0) {
                animationVideoLike.visibility = View.VISIBLE
            } else {
                animationVideoLike.visibility = View.GONE
            }

            //startStop(itemView)
            progressBarBoost.progress = i
            mCountDownTimer = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                   // Log.e("FeedAdapter", "Tick of Progress $i$millisUntilFinished")
                    i++
                    progressBarBoost.progress = i * 100 / (30000 / 1000)
                }

                override fun onFinish() {
                    i++
                    progressBarBoost.progress = 1000
                }
            }
            (mCountDownTimer as CountDownTimer).start()
        }
    }

    /*private fun startStop(itemView: View) {
        if (timerStatus === TimerStatus.STOPPED) {
            setTimerValues()
            setProgressBarValues(itemView)
            timerStatus = TimerStatus.STARTED
            startCountDownTimer(itemView)
        } else {
            timerStatus = TimerStatus.STOPPED
            stopCountDownTimer()
        }
    }

    private fun setTimerValues() {
        val time = 1
        timeCountInMilliSeconds = time * 30 * 1000.toLong()
    }

    private fun startCountDownTimer(itemView: View) {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                itemView.progressBarBoost.progress = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                setProgressBarValues(itemView)
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED
            }
        }.start()
        // countDownTimer.start()
    }

    private fun stopCountDownTimer() {
        countDownTimer!!.cancel()
    }

    private fun setProgressBarValues(itemView: View) {
        itemView.progressBarBoost.max = timeCountInMilliSeconds.toInt() / 1000
        itemView.progressBarBoost.progress = timeCountInMilliSeconds.toInt() / 1000
    }*/

}