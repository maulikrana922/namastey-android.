package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.model.AlbumBean
import kotlinx.android.synthetic.main.row_post_album.view.*
import java.util.*

class PostVideoAlbumAdapter(
    var activity: Activity,
    var albumList: ArrayList<AlbumBean>,
    var selectedAlbumId: Long,
    var onItemClick: OnItemClick
) : RecyclerView.Adapter<PostVideoAlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_post_album, parent, false
        )
    )

    override fun getItemCount() = albumList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

//            Log.e("ContentLanguage", "video_lang: ${languageList[position].video_lang}")
//            Log.e("ContentLanguage", "video_lang_name: ${languageList[position].video_lang_name}")

            tvAlbumName.text = albumList[position].name

            if (selectedAlbumId == albumList[position].id) {
                ivSelected.visibility = View.VISIBLE
            } else {
                ivSelected.visibility = View.GONE
            }

            viewPostAlbum.setOnClickListener { v ->
                if (ivSelected.visibility == View.VISIBLE) {
                    ivSelected.visibility = View.GONE
                } else {
                    ivSelected.visibility = View.VISIBLE
                }
                onItemClick.onAlbumItemClick(albumList[position])
            }
        }
    }

    interface OnItemClick {
        fun onAlbumItemClick(tempAlbumBean: AlbumBean)
    }
}