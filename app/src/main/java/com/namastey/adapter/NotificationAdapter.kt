package com.namastey.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnNotificationClick
import com.namastey.model.ActivityListBean
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_notification.view.*

class NotificationAdapter(
    var activity: Activity,
    var activityList: ArrayList<ActivityListBean>,
    var isActivityList: Int,
    var onItemClick: OnNotificationClick
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_notification, parent, false
        )
    )

    override fun getItemCount() = activityList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val activityListBean = activityList[position]
            when (isActivityList) {
                //For All-Activity List
                0 -> {
                    if (activityListBean.follow_id != 0) {
                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.following_user_profile_pic
                        )

                        if (activityListBean.is_follow == 0) {
                            mainFollowBackView.visibility = View.VISIBLE
                        } else {
                            mainFollowBackView.visibility = View.GONE
                        }

                        mainFollowBackView.setOnClickListener {
                            onItemClick.onClickFollowRequest(
                                activityListBean.user_id,
                                activityListBean.is_follow
                            )
                            mainFollowBackView.visibility = View.GONE
                        }
                        // tvNotification.text = activityListBean.follow_message
                        Utils.setHtmlText(tvNotification, activityListBean.follow_message)
                    }

                    if (activityListBean.like_id != 0) {
                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.like_user_profile_pic
                        )
                        // tvNotification.text = activityListBean.like_message
                        Utils.setHtmlText(tvNotification, activityListBean.like_message)
                    }

                    if (activityListBean.comment_id != 0) {
                        if (activityListBean.cover_image_url != "") {
                            ivCommentCover.visibility = View.VISIBLE
                        } else {
                            ivCommentCover.visibility = View.GONE
                        }

                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.comment_user_profile_pic
                        )
                        GlideLib.loadImage(
                            activity,
                            ivCommentCover,
                            activityListBean.cover_image_url
                        )
                        //tvNotification.text = activityListBean.comment_message
                        Utils.setHtmlText(tvNotification, activityListBean.comment_message)

                    } else {

                    }

                    if (activityListBean.mentions_id != 0) {
                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.comment_user_profile_pic
                        )
                        // tvNotification.text = activityListBean.menstion_message
                        Utils.setHtmlText(tvNotification, activityListBean.menstion_message)
                    }
                }

                //For Like Profile
                1 -> {
                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.like_user_profile_pic
                    )
                    // tvNotification.text = activityListBean.like_message
                    Utils.setHtmlText(tvNotification, activityListBean.like_message)
                }

                //For Comment(Post video comment)
                2 -> {
                    //ivCommentCover.visibility = View.VISIBLE
                    if (activityListBean.cover_image_url != "") {
                        ivCommentCover.visibility = View.VISIBLE
                    } else {
                        ivCommentCover.visibility = View.GONE
                    }

                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.comment_user_profile_pic
                    )
                    GlideLib.loadImage(
                        activity,
                        ivCommentCover,
                        activityListBean.cover_image_url
                    )
                    // tvNotification.text = activityListBean.comment_message
                    Utils.setHtmlText(tvNotification, activityListBean.comment_message)
                }

                //For Followers
                3 -> {
                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.following_user_profile_pic
                    )
                    if (activityListBean.is_follow == 0) {
                        mainFollowBackView.visibility = View.VISIBLE
                    } else {
                        mainFollowBackView.visibility = View.GONE
                    }

                    mainFollowBackView.setOnClickListener {
                        onItemClick.onClickFollowRequest(
                            activityListBean.user_id,
                            activityListBean.is_follow
                        )
                        mainFollowBackView.visibility = View.GONE
                    }

                    //   tvNotification.text = activityListBean.follow_message
                    Utils.setHtmlText(tvNotification, activityListBean.follow_message)
                }

                //For Mentions
                4 -> {
                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.mentions_user_profile_pic
                    )
                    //tvNotification.text = activityListBean.menstion_message
                    Utils.setHtmlText(tvNotification, activityListBean.menstion_message)
                }
                else -> {

                }
            }

            ivUserProfile.setOnClickListener {
                Log.e("NotificationAdapter", "user_id: \t ${activityListBean.user_id}")
                if (activityListBean.following_user_id != 0L) {
                    onItemClick.onNotificationClick(activityListBean.following_user_id, position)
                } else {
                    onItemClick.onNotificationClick(activityListBean.user_id, position)
                }
            }
        }
    }
}