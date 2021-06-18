package com.namastey.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.activity.AlbumDetailActivity
import com.namastey.activity.ProfileViewActivity
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnViewAlbumClick
import com.namastey.model.AlbumBean
import com.namastey.utils.Constants
import kotlinx.android.synthetic.main.row_parent_album_profile.view.*
import java.util.*

class AlbumListProfileAdapter(
    var albumList: ArrayList<AlbumBean>,
    var activity: Context,
    var onViewAlbumClick: OnViewAlbumClick,
    var onItemClick: OnItemClick,
    var gender: String,
    var isMyProfile: Boolean
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
                        VideoListAdapter(albumBean.post_video_list, activity, onItemClick, false,isMyProfile)
                    setRecycledViewPool(viewPool)
                }
            }else{
                rvChildAlbumPost.visibility = View.GONE
            }

            tvAlbumViewall.setOnClickListener{
                val intent = Intent(activity, AlbumDetailActivity::class.java)
                intent.putExtra(Constants.ALBUM_BEAN, albumBean)
                intent.putExtra(Constants.FROM_EDIT, false)
                intent.putExtra(Constants.GENDER, gender)
                intent.putExtra("isMyProfile", isMyProfile)
                (activity as ProfileViewActivity).openActivity(intent)
            }

        }

    }
}