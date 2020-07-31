package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.AlbumBean
import com.namastey.model.VideoBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_album_list.view.*
import kotlinx.android.synthetic.main.row_child_video_album.view.*

class AlbumListAdapter(
    var albumList: ArrayList<AlbumBean>,
    var activity: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<AlbumListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_album_list, parent, false
        )
    )

    override fun getItemCount() = albumList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            var albumBean = albumList[position]
            tvAlbumName.text = albumBean.name
            tvPostCount.text = albumBean.post_count.toString() + " " + context.getString(R.string.posts)
//            if (videoBean.cover_image_url != null)
//                GlideLib.loadImage(activity, ivVideoImage, videoBean.cover_image_url)
        }

    }
}