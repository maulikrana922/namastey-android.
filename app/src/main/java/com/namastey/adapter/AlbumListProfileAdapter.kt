package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnViewAlbumClick
import com.namastey.model.AlbumBean
import kotlinx.android.synthetic.main.row_parent_album_profile.view.*

class AlbumListProfileAdapter(
    var albumList: ArrayList<AlbumBean>,
    var activity: Context,
    var onViewAlbumClick: OnViewAlbumClick,
    var onItemClick: OnItemClick
) : RecyclerView.Adapter<AlbumListProfileAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_parent_album_profile, parent, false
        )
    )

    override fun getItemCount() = albumList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val albumBean = albumList[position]
            tvAlbumName.text = albumBean.name

            if (albumBean.post_video_list.size > 0) {
                rvChildAlbumPost.visibility = View.VISIBLE
                rvChildAlbumPost.apply {
                    adapter =
                        VideoListAdapter(albumBean.post_video_list, activity, onItemClick, false)
                    setRecycledViewPool(viewPool)
                }
            }else{
                rvChildAlbumPost.visibility = View.GONE
            }

        }

    }
}