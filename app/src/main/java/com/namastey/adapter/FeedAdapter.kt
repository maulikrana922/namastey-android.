package com.namastey.adapter


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.namastey.R
import com.namastey.activity.ProfileActivity
import com.namastey.listeners.OnFeedItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.*
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.row_feed.view.*
import me.tankery.lib.circularseekbar.CircularSeekBar
import me.tankery.lib.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener


class FeedAdapter(
    var feedList: ArrayList<DashboardBean>,
    val activity: Activity,
    var onFeedItemClick: OnFeedItemClick,
    var sessionManager: SessionManager
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    private val TAG = "FeedAdapter"

    val handlerVideo = Handler(activity.mainLooper)
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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


                /*  postVideo.setVideoPath(dashboardBean.video_url)
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
                  }*/

                initializePlayer(itemView, dashboardBean.video_url)
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

            tvFeedShare.text = dashboardBean.share.toString()

            tvFeedShare.setOnClickListener {
                onFeedItemClick.onItemClick(position, dashboardBean)
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

            ivFeedBoost.setOnClickListener {
                onFeedItemClick.onFeedBoost(dashboardBean.user_id)
            }

            if (dashboardBean.is_liked_you == 1 && dashboardBean.is_like != 1) {
                animationVideoLike.visibility = View.VISIBLE
            } else {
                animationVideoLike.visibility = View.GONE
            }

            Log.e(
                "FeedAdapter",
                "KEY_BOOST_ME: \t ${SessionManager(context).getBooleanValue(Constants.KEY_BOOST_ME)}"
            )

            if (SessionManager(context).getBooleanValue(Constants.KEY_BOOST_ME)) {
                animationBoost.visibility = View.VISIBLE
                circularSeekBar.visibility = View.VISIBLE
                tvFeedBoost.visibility = View.VISIBLE
                ivFeedBoost.setImageDrawable(resources.getDrawable(R.drawable.ic_boost_brown))
                /*val drawable: Drawable = resources.getDrawable(R.drawable.ic_boost).mutate()
                drawable.setColorFilter(resources.getColor(R.color.color_instagram), PorterDuff.Mode.SRC_ATOP)
                tvFeedBoost.setCompoundDrawables(null, resources.getDrawable(R.drawable.ic_boost_brown), null, null)*/
            } else {
                animationBoost.visibility = View.GONE
                circularSeekBar.visibility = View.GONE
                tvFeedBoost.visibility = View.VISIBLE
                ivFeedBoost.setImageDrawable(resources.getDrawable(R.drawable.ic_boost))
                /*val drawable: Drawable = resources.getDrawable(R.drawable.ic_boost).mutate()
                drawable.setColorFilter(resources.getColor(R.color.colorLightGrayText), PorterDuff.Mode.SRC_ATOP)
                tvFeedBoost.setCompoundDrawables(null, resources.getDrawable(R.drawable.ic_boost), null, null)*/
            }

            boostAnimationProgress(itemView)

            //startStop(itemView)
            /* progressBarBoost.progress = i
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
             (mCountDownTimer as CountDownTimer).start()*/
        }
    }

    private fun boostAnimationProgress(itemView: View) {
        val seekBar = itemView.findViewById(R.id.circularSeekBar) as CircularSeekBar
        itemView.circularSeekBar.setOnSeekBarChangeListener(object :
            OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar,
                progress: Float,
                fromUser: Boolean
            ) {
                val message = String.format(
                    "Progress changed to %.2f, fromUser %s",
                    progress,
                    fromUser
                )
                var p = progress
                p /= 100
                Log.e("FeedAdapter", message)
                Log.e("FeedAdapter", "progress: \t$progress")
                Log.e("FeedAdapter", "p: \t$p")
                Log.e("FeedAdapter", "fromUser: \t$fromUser")
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar) {
                Log.e("FeedAdapter", "onStopTrackingTouch")
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar) {
                Log.e("FeedAdapter", "onStartTrackingTouch")
            }
        })
    }

    private fun initializePlayer(itemView: View, videoUrl: String) {

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(activity)

        mediaDataSourceFactory =
            DefaultDataSourceFactory(activity, Util.getUserAgent(activity, "mediaPlayerSample"))

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(Uri.parse(videoUrl))

        simpleExoPlayer.prepare(mediaSource, false, false)
        simpleExoPlayer.playWhenReady = true

        itemView.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        itemView.playerView.player = simpleExoPlayer
        itemView.playerView.requestFocus()

        simpleExoPlayer.addListener(object : EventListener {
//            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
//                Log.e(
//                    TAG,
//                    "onPlaybackParametersChanged: playbackParameters: ${playbackParameters!!.speed}"
//                )
//            }

            override fun onSeekProcessed() {
                Log.e(TAG, "onSeekProcessed: ")
            }

//            override fun onTracksChanged(
//                trackGroups: TrackGroupArray?,
//                trackSelections: TrackSelectionArray?
//            ) {
//                Log.e(TAG, "onTracksChanged: trackGroups: $trackGroups")
//                Log.e(TAG, "onTracksChanged: trackSelections: $trackSelections")
//            }

//            override fun onPlayerError(error: ExoPlaybackException?) {
//                Log.e(TAG, "onPlayerError: error: ${error!!.message}")
//                Log.e(TAG, "onPlayerError: error: ${error.stackTrace}")
//            }

            // * 4 playbackState exists
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.e(TAG, "onPlayerError: playWhenReady: $playWhenReady")
                Log.e(TAG, "onPlayerError: playbackState: $playbackState")
                when (playbackState) {
                    STATE_BUFFERING -> {
                        // progressBar.visibility = View.VISIBLE
                        Log.e(TAG, "onPlayerStateChanged - STATE_BUFFERING")
                        //toast("onPlayerStateChanged - STATE_BUFFERING")
                    }
                    STATE_READY -> {
                        //  progressBar.visibility = View.INVISIBLE
                        Log.e(TAG, "onPlayerStateChanged - STATE_READY")
                        // toast("onPlayerStateChanged - STATE_READY")
                    }
                    STATE_IDLE -> {
                        Log.e(TAG, "onPlayerStateChanged - STATE_IDLE")
                        // toast("onPlayerStateChanged - STATE_IDLE")
                    }
                    STATE_ENDED -> {
                        simpleExoPlayer.seekTo(0);
                        Log.e(TAG, "onPlayerStateChanged - STATE_ENDED")
                        //toast("onPlayerStateChanged - STATE_ENDED")
                    }
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.e(TAG, "onLoadingChanged: isLoading: $isLoading")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.e(TAG, "onPositionDiscontinuity: reason: $reason")
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.e(TAG, "onRepeatModeChanged:\t repeatMode: $repeatMode ")
                // Toast.makeText(activity, "repeat mode changed", Toast.LENGTH_SHORT).show()
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.e(
                    TAG,
                    "onShuffleModeEnabledChanged: : \t shuffleModeEnabled: $shuffleModeEnabled "
                )
            }

//            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
//                Log.e(TAG, "onTimelineChanged: timeline: $timeline")
//                Log.e(TAG, "onTimelineChanged: reason: $reason")
//            }
        })
    }

    public fun releasePlayer() {
        simpleExoPlayer.release()
    }

}