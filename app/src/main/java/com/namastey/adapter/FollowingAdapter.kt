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
    var onFollowItemClick: OnFollowItemClick,
    var userId: Long,
    var isMyProfile: Boolean
) : androidx.recyclerview.widget.RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {/*, Filterable {

    var followingFullList = ArrayList<DashboardBean>()

    init {
        followingFullList = followingList as ArrayList<DashboardBean>
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_following, parent, false
        )
    )

    override fun getItemCount() = followingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun filterList(filteredName: ArrayList<DashboardBean>) {
        this.followingList = filteredName
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val dashboardBean = followingList[position]
            tvFollowingName.text = dashboardBean.username
            tvFollowingJob.text = dashboardBean.job

            GlideLib.loadImageUrlRoundCorner(activity, ivFollowingUser, dashboardBean.profile_url)

            if (userId == dashboardBean.id) {
                tvFollowingLabel.visibility = View.GONE
            } else {
                tvFollowingLabel.visibility = View.VISIBLE
                if (isMyProfile) {
                    if (isFollowing)
                        tvFollowingLabel.text = activity.getString(R.string.following)
                    else
                        tvFollowingLabel.text = activity.getString(R.string.remove)
                } else {
                    if (dashboardBean.is_follow == 0)
                        tvFollowingLabel.text = activity.getString(R.string.follow)
                    else
                        tvFollowingLabel.text = activity.getString(R.string.following)
                }

            }

            viewFollowMain.setOnClickListener {
                onFollowItemClick.onUserItemClick(dashboardBean.id)
            }
            tvFollowingLabel.setOnClickListener {

//                val msg1: String = if (isFollowing) {
//                    resources.getString(R.string.msg_unfollow_user)
//                } else {
//                    resources.getString(R.string.msg_remove_followers)
//                }

                var msg = ""
                var isFollow = 0
                if (isMyProfile) {
                    isFollow = 0
                    msg = if (isFollowing) {
                        resources.getString(R.string.msg_unfollow_user)
                    } else {
                        resources.getString(R.string.msg_remove_followers)
                    }
                } else {
                    if (dashboardBean.is_follow == 1) {
                        isFollow = 0
                        msg = resources.getString(R.string.msg_unfollow_user)
                    } else {
                        isFollow = 1
                        msg = resources.getString(R.string.msg_send_follow_request)
                    }
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
                                    isFollow,
                                    position
                                )
                            }
                        }
                    }
                }.show()
            }
        }

    }

    /*override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    followingFullList = followingList as ArrayList<DashboardBean>
                } else {
                    val resultList = ArrayList<DashboardBean>()
                    for (row in followingList) {
                        if (row.username.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    followingFullList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = followingFullList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                followingFullList = results?.values as ArrayList<DashboardBean>
                notifyDataSetChanged()
            }
        }
    }*/


}