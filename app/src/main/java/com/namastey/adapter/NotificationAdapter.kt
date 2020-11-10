package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.model.ActivityListBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_notification.view.*

class NotificationAdapter(
    var activity: Activity,
    var activityList: ArrayList<ActivityListBean>,
    var isActivityList: Int
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

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val activityListBean = activityList[position]
            when (isActivityList) {
                0 -> {
                    if (activityListBean.follow_id != 0) {
                        mainFollowBackView.visibility = View.VISIBLE
                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.following_user_profile_pic
                        )
                        tvNotification.text = activityListBean.follow_message
                    }

                    if (activityListBean.like_id != 0) {
                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.like_user_profile_pic
                        )
                        tvNotification.text = activityListBean.like_message
                    }

                    if (activityListBean.comment_id != 0) {
                        if (activityListBean.comment_user_profile_pic != "") {
                            ivOtherUser.visibility = View.GONE
                        } else {
                            ivOtherUser.visibility = View.VISIBLE
                        }

                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.comment_user_profile_pic
                        )
                        GlideLib.loadImage(
                            activity,
                            ivOtherUser,
                            activityListBean.cover_image_url
                        )
                        tvNotification.text = activityListBean.comment_message
                    } else {

                    }

                    if (activityListBean.mentions_id != 0) {
                        GlideLib.loadImage(
                            activity,
                            ivUserProfile,
                            activityListBean.comment_user_profile_pic
                        )
                        tvNotification.text = activityListBean.menstion_message
                    }



                }
                1 -> {
                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.like_user_profile_pic
                    )
                    tvNotification.text = activityListBean.like_message
                }
                2 -> {
                    ivOtherUser.visibility = View.VISIBLE
                    if (activityListBean.comment_user_profile_pic != "") {
                        ivOtherUser.visibility = View.GONE
                    } else {
                        ivOtherUser.visibility = View.VISIBLE
                    }

                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.comment_user_profile_pic
                    )
                    GlideLib.loadImage(
                        activity,
                        ivOtherUser,
                        activityListBean.cover_image_url
                    )
                    tvNotification.text = activityListBean.comment_message
                }
                3 -> {
                    mainFollowBackView.visibility = View.VISIBLE
                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.following_user_profile_pic
                    )
                    tvNotification.text = activityListBean.follow_message
                }
                4 -> {
                    GlideLib.loadImage(
                        activity,
                        ivUserProfile,
                        activityListBean.comment_user_profile_pic
                    )
                    tvNotification.text = activityListBean.menstion_message
                }
                else -> {

                }
            }

        }

    }
}