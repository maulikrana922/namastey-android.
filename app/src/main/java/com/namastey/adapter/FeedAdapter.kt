package com.namastey.adapter


import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.hendraanggrian.appcompat.widget.SocialTextView
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.fragment.SignUpFragment
import com.namastey.listeners.OnFeedItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.row_feed.view.*
import me.tankery.lib.circularseekbar.CircularSeekBar
import me.tankery.lib.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener
import java.util.*


class FeedAdapter(
    var feedList: ArrayList<DashboardBean>,
    val activity: Activity,
    var onFeedItemClick: OnFeedItemClick,
    var sessionManager: SessionManager
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private val TAG = "FeedAdapter"

    val handlerVideo = Handler(activity.mainLooper)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = FeedViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_feed, parent, false
        )
    )

    override fun getItemCount() = feedList.size

    override fun onBindViewHolder(holderFeed: FeedViewHolder, position: Int) {
        holderFeed.bind(position, activity)
    }

    inner class FeedViewHolder(@param:NonNull private val parent: View) :
        RecyclerView.ViewHolder(parent) {

        lateinit var mediaContainer: FrameLayout

        lateinit var mediaCoverImage: ImageView

        lateinit var ivCommentFirst: ImageView
        lateinit var ivCommentSecond: ImageView
        lateinit var ivCommentThird: ImageView
        lateinit var ivFeedProfile: ImageView
        lateinit var ivFeedFollow: ImageView
        lateinit var ivFeedBoost: ImageView
        lateinit var ivComment: ImageView


        lateinit var tvCommentFeed: TextView
        lateinit var tvCommentCount: TextView
        lateinit var tvFeedName: TextView
        lateinit var tvFeedLike: TextView
        lateinit var tvFeedJob: TextView
        lateinit var tvFeedDesc: SocialTextView
        lateinit var tvFeedView: TextView
        lateinit var tvFeedShare: TextView
        lateinit var tvFeedBoost: TextView

        lateinit var animationVideoLike: LottieAnimationView
        lateinit var animationBoost: LottieAnimationView

        lateinit var circularSeekBar: CircularSeekBar

        init {
            mediaContainer = parent.findViewById(R.id.mediaContainer)

            mediaCoverImage = parent.findViewById(R.id.ivMediaCoverImage)
            ivCommentFirst = parent.findViewById(R.id.ivCommentFirst)
            ivCommentSecond = parent.findViewById(R.id.ivCommentSecond)
            ivCommentThird = parent.findViewById(R.id.ivCommentThird)
            ivFeedProfile = parent.findViewById(R.id.ivFeedProfile)
            ivFeedFollow = parent.findViewById(R.id.ivFeedFollow)
            ivFeedBoost = parent.findViewById(R.id.ivFeedBoost)
            ivComment = parent.findViewById(R.id.ivComment)

            tvCommentFeed = parent.findViewById(R.id.tvCommentFeed)
            tvCommentCount = parent.findViewById(R.id.tvCommentCount)
            tvFeedName = parent.findViewById(R.id.tvFeedName)
            tvFeedLike = parent.findViewById(R.id.tvFeedLike)
            tvFeedJob = parent.findViewById(R.id.tvFeedJob)
            tvFeedDesc = parent.findViewById(R.id.tvFeedDesc)
            tvFeedView = parent.findViewById(R.id.tvFeedView)
            tvFeedShare = parent.findViewById(R.id.tvFeedShare)
            tvFeedBoost = parent.findViewById(R.id.tvFeedBoost)

            animationVideoLike = parent.findViewById(R.id.animationVideoLike)
            animationBoost = parent.findViewById(R.id.animationBoost)

            circularSeekBar = parent.findViewById(R.id.circularSeekBar)
        }

        fun bind(position: Int, context: Context) /*= with(itemView)*/ {

            parent.tag = this
            val dashboardBean = feedList[position]

            Log.e("FeedAdapter", "itemCount: \t $itemCount")
            Log.e("FeedAdapter", "position: \t $position")

            if (position == itemCount - 1 && feedList.size >= 10) {
                (context as DashboardActivity).getFeedListApi(0,false)
            }

            handlerVideo.removeCallbacksAndMessages(null)

            /* if (!dashboardBean.video_url.isNullOrEmpty()) {
 //                postVideo.setVideoPath(dashboardBean.video_url)
 //                val uri = Uri.parse("https://testyourapp.online/namasteyapp/public/uploads/post_video/c24de581af92c8a9e32a47a84a255d45.mp4")
 //            val mediaController = MediaController(activity)
 //            mediaController.setAnchorView(postVideo)
 //            postVideo.setMediaController(mediaController)
 //                var uri = Uri.parse("android.resource://" + activity.packageName + "/" + R.raw.signupvideo);
 //            postVideo.setVideoURI(Uri.parse("http://testyourapp.online/namasteyapp/public/uploads/post_video/c24de581af92c8a9e32a47a84a255d45.mp4"))
 //                postVideo.setVideoURI(Uri.parse(dashboardBean.video_url))


                 Log.e("FeedAdapter", "VideoUrl: \t ${dashboardBean.video_url}")

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


                 // initializePlayer(itemView, dashboardBean.video_url, position)
             }*/

            if (!dashboardBean.video_url.isNullOrEmpty()) {
                Log.e(
                    "ExoPlayerRecyclerView",
                    "Video URL : " + dashboardBean.video_url
                )
//                customPlayerView.reset()
//                customPlayerView.id = View.generateViewId()
//                customPlayerView.setVideoUri(Uri.parse(dashboardBean.video_url))

                // Log.e("FeedAdapter", "VideoUrl: \t ${dashboardBean.video_url}")

                handlerVideo.postDelayed({
                    onFeedItemClick.onPostViewer(dashboardBean.id)
                }, 5000)
            }
            tvCommentCount.text = dashboardBean.comments.toString()

            if (dashboardBean.who_can_comment == 2) {
                tvCommentFeed.text = activity.getString(R.string.comments_off)
            } else {
                if (dashboardBean.who_can_comment == 1) {
                    if (dashboardBean.is_comment == 0 && dashboardBean.is_follow == 1) {
                        tvCommentFeed.text = dashboardBean.comments.toString().plus(" ")
                            .plus(activity.getString(R.string.comments))
                        showCommentProfilePic(
                            dashboardBean,
                            ivCommentFirst,
                            ivCommentSecond,
                            ivCommentThird
                        )
                    } else {
                        tvCommentFeed.text = activity.getString(R.string.comments_off)
                    }
                } else {
                    if (dashboardBean.is_comment == 0) {
                        tvCommentFeed.text = dashboardBean.comments.toString().plus(" ")
                            .plus(activity.getString(R.string.comments))
                        showCommentProfilePic(
                            dashboardBean,
                            ivCommentFirst,
                            ivCommentSecond,
                            ivCommentThird
                        )
                    } else {
                        tvCommentFeed.text = activity.getString(R.string.comments_off)
                    }
                }
            }

            // if (dashboardBean.casual_name != "")
            tvFeedName.text = dashboardBean.casual_name

//            if (dashboardBean.profile_url.isNotEmpty())
            GlideLib.loadImage(activity, ivFeedProfile, dashboardBean.profile_url)

            if (dashboardBean.is_match == 1) {
                tvFeedLike.text = activity.getString(R.string.match)
                tvFeedLike.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.white
                    )
                )
                tvFeedLike.background =
                    ContextCompat.getDrawable(activity, R.drawable.rounded_btn_red)
                tvFeedLike.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_like_dashboard,
                    0,
                    0,
                    0
                )
            } else {
                if (dashboardBean.is_like == 1) {
                    tvFeedLike.text = activity.getString(R.string.liked)
                    tvFeedLike.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_text_red
                        )
                    )
                    tvFeedLike.background =
                        ContextCompat.getDrawable(activity, R.drawable.rounded_btn)
                    tvFeedLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0)
                } else {
                    tvFeedLike.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.white
                        )
                    )
                    tvFeedLike.text = activity.getString(R.string.match)
                    tvFeedLike.background =
                        ContextCompat.getDrawable(activity, R.drawable.rounded_btn_red)
                    tvFeedLike.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_like_dashboard,
                        0,
                        0,
                        0
                    )
                }
            }

            tvFeedJob.text = dashboardBean.job

            tvFeedDesc.text = dashboardBean.description
            tvFeedDesc.mentionColor = ContextCompat.getColor(context, R.color.colorBlueLight)
            tvFeedDesc.setOnMentionClickListener { view, text ->

                //Log.e("FeedAdapter", "setOnMentionClickListener: $text")
                //  Log.e("FeedAdapter", "setOnMentionClickListener: ${view.mentions}")

                onFeedItemClick.onDescriptionClick(text.toString())
            }

            tvFeedView.text = dashboardBean.viewers.toString()

            if (dashboardBean.is_follow == 1) {
                ivFeedFollow.setImageResource(R.drawable.ic_add_right)
            } else {
                ivFeedFollow.setImageResource(R.drawable.ic_add_follow_from_profile)
            }

            ivFeedFollow.setOnClickListener {

                if (sessionManager.isGuestUser()) {
                    (activity as DashboardActivity).addFragment(
                        SignUpFragment.getInstance(
                            true
                        ),
                        Constants.SIGNUP_FRAGMENT
                    )
                } else if (!sessionManager.getBooleanValue(Constants.KEY_IS_COMPLETE_PROFILE)) {
                    (activity as DashboardActivity).completeSignUpDialog()
                } else {
                    var isFollow = 0
                    val msg: String
                    val btnText: String
                    if (dashboardBean.is_follow == 1) {
                        isFollow = 0
                        msg = context.resources.getString(R.string.msg_remove_post)
                        btnText = context.resources.getString(R.string.remove)
                    } else {
                        isFollow = 1
                        msg = context.resources.getString(R.string.msg_send_follow_request)
                        btnText = context.resources.getString(R.string.send)
                    }
                    object : CustomCommonAlertDialog(
                        activity,
                        dashboardBean.casual_name,
                        msg,
                        dashboardBean.profile_url,
                        btnText,
                        context.resources.getString(R.string.cancel)
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
                }
            }

            tvFeedShare.text = dashboardBean.share.toString()

            tvFeedShare.setOnClickListener {
                onFeedItemClick.onItemClick(position, dashboardBean)
            }

            if (dashboardBean.is_comment == 0 && !sessionManager.isGuestUser()) {
                if (dashboardBean.is_comment == 0 && !tvCommentFeed.text.contains(
                        activity.getString(
                            R.string.comments_off
                        )
                    )
                ) {// && !sessionManager.isGuestUser()) {
                    tvCommentFeed.setOnClickListener {
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
                    tvCommentFeed.setOnClickListener {
                        onFeedItemClick.onCommentClick(position, dashboardBean.id)
                    }
                }
            }

            ivComment.setOnClickListener {
                if (dashboardBean.who_can_comment == 2) {
                    openCustomAlertDialog()
                } else {
                    if (dashboardBean.who_can_comment == 1) {
                        if (dashboardBean.is_comment == 0 && dashboardBean.is_follow == 1) {
                            onFeedItemClick.onCommentClick(position, dashboardBean.id)
                        } else {
                            openCustomAlertDialog()
                        }
                    } else {
                        if (dashboardBean.is_comment == 0) {
                            onFeedItemClick.onCommentClick(position, dashboardBean.id)
                        } else {
                            openCustomAlertDialog()
                        }
                    }
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
            if (sessionManager.getIntegerValue(Constants.KEY_NO_OF_BOOST) != 0)
                tvFeedBoost.text =
                    sessionManager.getIntegerValue(Constants.KEY_NO_OF_BOOST).toString()
            else
                tvFeedBoost.text = "0"

//            tvFeedBoost.setOnClickListener {
//                onFeedItemClick.onFeedBoost(dashboardBean.user_id)
//            }
//
//            ivFeedBoost.setOnClickListener {
//                onFeedItemClick.onFeedBoost(dashboardBean.user_id)
//            }

            if (dashboardBean.is_liked_you == 1 && dashboardBean.is_like != 1 &&
                sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 1
            ) {
                animationVideoLike.visibility = View.VISIBLE
            } else {
                animationVideoLike.visibility = View.GONE
            }

            /* Log.e(
                 "FeedAdapter",
                 "KEY_BOOST_ME: \t ${SessionManager(context).getBooleanValue(Constants.KEY_BOOST_ME)}"
             )*/


            //Todo: Change boost animation time- when start boost
            if (SessionManager(context).getBooleanValue(Constants.KEY_IS_BOOST_ACTIVE)) {
                animationBoost.visibility = View.VISIBLE
                circularSeekBar.visibility = View.VISIBLE
                tvFeedBoost.visibility = View.VISIBLE
                // ivFeedBoost.setImageDrawable(context.resources.getDrawable(R.drawable.ic_boost_brown))
            } else {
                animationBoost.visibility = View.GONE
                circularSeekBar.visibility = View.GONE
                tvFeedBoost.visibility = View.VISIBLE
                // ivFeedBoost.setImageDrawable(context.resources.getDrawable(R.drawable.ic_boost))
            }

            boostAnimationProgress(itemView)
            Log.e(
                "ExoPlayerRecyclerView",
                "Bind : " + Calendar.getInstance().time.toString()
            )
        }
    }


    private fun showCommentProfilePic(
        dashboardBean: DashboardBean,
        ivCommentFirst: ImageView,
        ivCommentSecond: ImageView,
        ivCommentThird: ImageView
    ) {
        when {
            dashboardBean.is_comment == 1 || dashboardBean.who_can_comment == 2 -> {
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
                //Log.e("FeedAdapter", message)
                // Log.e("FeedAdapter", "progress: \t$progress")
                //  Log.e("FeedAdapter", "p: \t$p")
                //  Log.e("FeedAdapter", "fromUser: \t$fromUser")
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar) {
                //  Log.e("FeedAdapter", "onStopTrackingTouch")
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar) {
                //  Log.e("FeedAdapter", "onStartTrackingTouch")
            }
        })
    }

    fun openCustomAlertDialog() {
        object : CustomAlertDialog(
            activity,
            activity.resources.getString(R.string.alert_comment_message),
            activity.getString(R.string.okay),
            ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }
}