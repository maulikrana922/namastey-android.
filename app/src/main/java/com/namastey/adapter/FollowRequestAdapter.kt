package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
) : androidx.recyclerview.widget.RecyclerView.Adapter<FollowRequestAdapter.ViewHolder>() {

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
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val followRequest = followRequestList[position]

            GlideLib
                .loadImage(
                    activity, ivUserProfile, followRequest.profile_url
                )

            tvUsername.text = followRequest.username
            tvUserProfession.text = followRequest.job

            Utils.rectangleShapeGradient(
                mainCategoryView, intArrayOf(
                    Color.parseColor("#28BAD3"),
                    Color.parseColor("#A19FEE")
                )
            )
            mainCategoryView.alpha = 0.6f


            tvAllow.setOnClickListener {
                onFollowRequestClick.onItemAllowDenyClick(followRequest.follow_id, 1,position)
            }
            tvDeny.setOnClickListener {
                onFollowRequestClick.onItemAllowDenyClick(followRequest.follow_id, 0, position)
            }
        }

    }
}