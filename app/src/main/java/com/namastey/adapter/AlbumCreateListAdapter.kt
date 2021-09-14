package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnCreateAlbumItemClick
import com.namastey.listeners.OnItemClick
import com.namastey.model.AlbumBean
import com.namastey.model.ProfileBean
import kotlinx.android.synthetic.main.row_parent_album.view.*
import java.util.*

class AlbumCreateListAdapter(
    var albumList: ArrayList<AlbumBean>,
    var activity: Context,
    var onCreateAlbumItemClick: OnCreateAlbumItemClick,
    var onItemClick: OnItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<AlbumCreateListAdapter.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_parent_album, parent, false
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
            edtAlbumName.setText(albumBean.name)

//            if (position == 0) {
//                edtAlbumName.isEnabled = false
//                ivEditAlbum.visibility = View.INVISIBLE
//            } else {
                ivEditAlbum.visibility = View.VISIBLE
                if (albumBean.is_created == 1) {
                    edtAlbumName.isEnabled = true
                    ivEditAlbum.setImageResource(R.drawable.ic_done_red)
                } else {
                    edtAlbumName.isEnabled = false
                    ivEditAlbum.setImageResource(R.drawable.ic_edit_gray)
                }
//            }
            ivEditAlbum.setOnClickListener {
                if (albumList[position].is_created == 1) {
//                        albumList[position].is_created = 0
//                        edtAlbumName.isEnabled = false
//                        ivEditAlbum.setImageResource(R.drawable.ic_edit_gray)
                    albumBean.name = edtAlbumName.text.toString().trim()
                    if (albumList[position].id == 0L)       // If id == 0 then create new album otherwise update album
                        onCreateAlbumItemClick.onCreateAlbumItemClick(albumBean, position, true)
                    else
                        onCreateAlbumItemClick.onCreateAlbumItemClick(albumBean, position, false)
                } else {
                    albumList[position].is_created = 1
                    edtAlbumName.isEnabled = true
                    ivEditAlbum.setImageResource(R.drawable.ic_done_red)

                }
            }

            ivAddVideo.setOnClickListener {
                onCreateAlbumItemClick.onClickAddVideo(albumBean)
            }
            rvChildAlbumPost.apply {
                adapter = VideoListAdapter(albumBean.post_video_list, activity, onItemClick,true,false,0,0)
                setRecycledViewPool(viewPool)
            }


        }

    }
}