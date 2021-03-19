package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.model.AlbumBean
import kotlinx.android.synthetic.main.row_album_list.view.*
import java.util.*

class AlbumListAdapter(
    var albumList: ArrayList<AlbumBean>,
    var activity: Activity,
    var onItemClick: OnItemClick
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

            val albumBean = albumList[position]
            tvAlbumName.text = albumBean.name
            tvPostCount.text =
                albumBean.post_count.toString().plus(" ").plus(context.getString(R.string.posts))
//            if (videoBean.cover_image_url != null)
//                GlideLib.loadImage(activity, ivVideoImage, videoBean.cover_image_url)

            if (albumBean.name == context.getString(R.string.saved)) {
                viewUpload.visibility = View.VISIBLE
                ivSaveAlbum.visibility = View.VISIBLE
                ivAlbumNormal.visibility = View.GONE
                tvAlbumName.setTextColor(Color.BLACK)
                tvPostCount.setTextColor(Color.BLACK)
            }
            itemView.setOnClickListener {
                onItemClick.onItemClick(albumBean.id, position)
            }
        }

    }
}