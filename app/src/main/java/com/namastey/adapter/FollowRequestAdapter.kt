package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnFollowRequestClick
import com.namastey.model.FollowRequestBean
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_follow_request.view.*

class FollowRequestAdapter(
    var followRequestList: ArrayList<FollowRequestBean>,
    var activity: Activity,
    var onFollowRequestClick: OnFollowRequestClick
) : RecyclerView.Adapter<FollowRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_follow_request, parent, false
        )
    )

    override fun getItemCount() = followRequestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val followRequest = followRequestList[position]

            GlideLib
                .loadImage(
                    activity, ivUserProfile, followRequest.profile_url
                )

            if (followRequest.sub_cat_details != null && followRequest.sub_cat_details.size > 0) {
                Log.e(
                    "FollowRequestAdapter",
                    "sub_cat_details: \t ${followRequest.sub_cat_details[0].name}"
                )
                tvCategory.text = followRequest.sub_cat_details[0].name

                Utils.rectangleShapeGradient(
                    mainCategoryView, intArrayOf(
                        Color.parseColor(followRequest.sub_cat_details[0].start_color),
                        Color.parseColor(followRequest.sub_cat_details[0].end_color)
                    )
                )
            } else {
                tvCategory.visibility = View.GONE
                mainCategoryView.visibility = View.GONE
            }

            tvUsername.text = followRequest.username
            tvUserProfession.text = followRequest.job

            mainCategoryView.alpha = 0.5f

            llFollowRequest.setOnClickListener {
                onFollowRequestClick.onFollowRequestItemClick(followRequest.id, position)
            }

            tvAllow.setOnClickListener {
                onFollowRequestClick.onItemAllowDenyClick(followRequest.follow_id, 1, position)
            }
            tvDeny.setOnClickListener {
                onFollowRequestClick.onItemAllowDenyClick(followRequest.follow_id, 0, position)
            }
        }

    }
}