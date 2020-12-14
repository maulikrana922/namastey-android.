package com.namastey.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.model.VideoBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_like_profile.view.*

class LikeProfileAdapter(
    var videoBeanList: ArrayList<VideoBean>,
    var activity: Activity,
    var onItemClick: OnItemClick
) : RecyclerView.Adapter<LikeProfileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_like_profile, parent, false
        )
    )

    override fun getItemCount() = videoBeanList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val videoBean = videoBeanList[position]

            Log.e("LikeProfileAdapter", "profile_url: ${videoBean.profile_url}")
            Log.e("LikeProfileAdapter", "profile_url: ${videoBean.profile_pic}")

            GlideLib
                .loadImage(
                    activity, ivVideoImage, videoBean.cover_image_url
                )

           GlideLib
                .loadImage(
                    activity, ivProfile, videoBean.profile_url
                )

            tvUsername.text = videoBean.username
        }
    }
}