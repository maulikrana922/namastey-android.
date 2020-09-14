package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnFollowItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.CustomCommonAlertDialog
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.row_following.view.*

class FollowingAdapter(
    var followingList: ArrayList<DashboardBean>,
    var activity: Activity,
    var isFollowing: Boolean,
    var onFollowItemClick: OnFollowItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_following, parent, false
        )
    )

    override fun getItemCount() = followingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val dashboardBean = followingList[position]
            tvFollowingName.text = dashboardBean.username
            tvFollowingJob.text = dashboardBean.job

            GlideLib.loadImageUrlRoundCorner(activity, ivFollowingUser, dashboardBean.profile_url)

            if (isFollowing)
                tvFollowingLabel.text = activity.getString(R.string.following)
            else
                tvFollowingLabel.text = activity.getString(R.string.remove)

            tvFollowingLabel.setOnClickListener {

                val msg: String = if (isFollowing) {
                    resources.getString(R.string.msg_unfollow_user)
                } else {
                    resources.getString(R.string.msg_remove_followers)
                }

                object : CustomCommonAlertDialog(
                    activity,
                    dashboardBean.username,
                    msg,
                    dashboardBean.profile_url,
                    activity.resources.getString(R.string.yes),
                    resources.getString(R.string.cancel)
                ) {
                    override fun onBtnClick(id: Int) {
                        when (id) {
                            btnAlertOk.id -> {
                                onFollowItemClick.onItemRemoveFollowersClick(
                                    dashboardBean.id,
                                    0,
                                    position
                                )
                            }
                        }
                    }
                }.show()
            }
        }

    }
}