package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.DashboardBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_feed.view.*

class FeedAdapter(
    var feedList: ArrayList<DashboardBean>,
    var activity: Context
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
            tvFeedName.text = feedList.get(position).name

            GlideLib.loadImageUrlRound(activity, ivCommentFirst, "")
            GlideLib.loadImageUrlRound(activity, ivCommentSecond, "")
            GlideLib.loadImageUrlRound(activity, ivCommentThird, "")
            GlideLib.loadImageUrlRound(activity, ivCommentFourth, "")

            // Need to change as per api response
            ivFeedFollow.tag = R.drawable.ic_add_follow_from_profile
            ivFeedFollow.setOnClickListener {
            if (ivFeedFollow.tag == R.drawable.ic_add_follow_from_profile) {
                ivFeedFollow.tag = R.drawable.ic_check
                ivFeedFollow.setImageResource(R.drawable.ic_check)
            } else {
                ivFeedFollow.tag = R.drawable.ic_add_follow_from_profile
                ivFeedFollow.setImageResource(R.drawable.ic_add_follow_from_profile)
            }
        }
        }

    }
}