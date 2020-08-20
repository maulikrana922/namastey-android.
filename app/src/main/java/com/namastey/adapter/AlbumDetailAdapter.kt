package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.model.VideoBean
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.row_album_detail.view.*

class AlbumDetailAdapter(
    var videoList: ArrayList<VideoBean>,
    var activity: Context,
    var onItemClick: OnItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<AlbumDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_album_detail, parent, false
        )
    )

    override fun getItemCount() = videoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            if (position == 0) {
                viewAlbumDetails.visibility = View.GONE
                llAddAlbum.visibility = View.VISIBLE

                llAddAlbum.setOnClickListener{

                }
            } else {
                viewAlbumDetails.visibility = View.VISIBLE
                llAddAlbum.visibility = View.GONE
                val videoBean = videoList[position]
                tvVideoViewers.text = videoBean.viewers.toString()
                tvVideoComment.text =
                    videoBean.comments.toString().plus(" ")
                        .plus(activity.getString(R.string.comments))

                if (videoBean.cover_image_url != null)
                    GlideLib.loadImage(activity, ivVideoImage, videoBean.cover_image_url)

                ivRemoveVideo.setOnClickListener {
                    object : CustomAlertDialog(
                        activity as Activity,
                        resources.getString(R.string.msg_remove_post),
                        activity.getString(R.string.yes),
                        activity.getString(R.string.cancel)
                    ) {
                        override fun onBtnClick(id: Int) {
                            when (id) {
                                btnPos.id -> {
                                    onItemClick.onItemClick(videoBean.id, position)
                                }
                                btnNeg.id -> {
                                    dismiss()
                                }
                            }
                        }
                    }.show()
                }
            }

        }

    }
}