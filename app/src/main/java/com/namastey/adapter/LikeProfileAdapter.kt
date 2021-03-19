package com.namastey.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnPostImageClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.VideoBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_like_profile.view.*
import java.util.*

class LikeProfileAdapter(
    var videoBeanList: ArrayList<VideoBean>,
    var activity: Activity,
    var onSelectUserItemClick: OnSelectUserItemClick,
    var onPostImageClick: OnPostImageClick
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

//            Log.e("LikeProfileAdapter", "profile_url: ${videoBean.profile_url}")
//            Log.e("LikeProfileAdapter", "profile_url: ${videoBean.profile_pic}")

            GlideLib
                .loadImage(
                    activity, ivVideoImage, videoBean.cover_image_url
                )

            GlideLib
                .loadImage(
                    activity, ivProfile, videoBean.profile_url
                )

            tvUsername.text = videoBean.username

            ivProfile.setOnClickListener {
                onSelectUserItemClick.onSelectItemClick(videoBean.user_id, position)
            }
            tvUsername.setOnClickListener {
                onSelectUserItemClick.onSelectItemClick(videoBean.user_id, position)
            }

            ivVideoImage.setOnClickListener {
                onPostImageClick.onItemPostImageClick(position, videoBeanList)
            }

        }
    }
}