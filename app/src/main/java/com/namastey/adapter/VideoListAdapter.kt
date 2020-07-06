package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.VideoBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_child_video_album.view.*

class VideoListAdapter(
    var videoList: ArrayList<VideoBean>,
    var activity: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_child_video_album, parent, false
        )
    )

    override fun getItemCount() = videoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            var videoBean = videoList[position]
            GlideLib.loadImage(activity, ivVideoImage, videoBean.cover_image_url)
        }

    }
}