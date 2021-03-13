package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnPostImageClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.VideoBean
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.row_album_detail.view.*
import java.util.*

class AlbumDetailAdapter(
    var videoList: ArrayList<VideoBean>,
    var activity: Context,
    var onItemClick: OnItemClick,
    var onPostImageClick: OnPostImageClick,
    var onSelectUserItemClick: OnSelectUserItemClick,
    var fromEdit: Boolean,
    var fromFilter: Boolean,
    var isSavedAlbum: Boolean
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

            if (position == 0 && fromEdit && !isSavedAlbum) {
                viewAlbumDetails.visibility = View.GONE
                llAddAlbum.visibility = View.VISIBLE

                llAddAlbum.setOnClickListener {
                    onItemClick.onItemClick(0, 0)
                }
            } else {
                val videoBean = videoList[position]
                if (fromFilter) {
                    tvUsername.visibility = View.VISIBLE
                    ivUserProfile.visibility = View.VISIBLE
                    tvVideoViewers.visibility = View.GONE
                    tvUsername.text = videoBean.username
                } else {
                    tvUsername.visibility = View.GONE
                    ivUserProfile.visibility = View.GONE
                    tvVideoViewers.visibility = View.VISIBLE
                }
                viewAlbumDetails.visibility = View.VISIBLE
                llAddAlbum.visibility = View.GONE
                tvVideoViewers.text = videoBean.viewers.toString()
                tvVideoComment.text =
                    videoBean.comments.toString().plus(" ")
                        .plus(activity.getString(R.string.comments))

                if (videoBean.cover_image_url != null)
                    GlideLib.loadImage(activity, ivVideoImage, videoBean.cover_image_url)

                GlideLib
                    .loadImage(
                        activity, ivUserProfile, videoBean.profile_url
                    )

                ivUserProfile.setOnClickListener {
                    onSelectUserItemClick.onSelectItemClick(videoBean.user_id, position)
                }
                tvUsername.setOnClickListener {
                    onSelectUserItemClick.onSelectItemClick(videoBean.user_id, position)
                }

                if (fromEdit) {
                    ivRemoveVideo.visibility = View.VISIBLE
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

                when {
                    videoBean.profile_pic.size >= 3 -> {
                        ivCommentFirst.visibility = View.VISIBLE
                        ivCommentSecond.visibility = View.VISIBLE
                        ivCommentThird.visibility = View.VISIBLE

                        GlideLib.loadImage(activity, ivCommentFirst, videoBean.profile_pic[0])
                        GlideLib.loadImage(activity, ivCommentSecond, videoBean.profile_pic[1])
                        GlideLib.loadImage(activity, ivCommentThird, videoBean.profile_pic[2])
                    }
                    videoBean.profile_pic.size == 2 -> {
                        ivCommentFirst.visibility = View.VISIBLE
                        ivCommentSecond.visibility = View.VISIBLE
                        ivCommentThird.visibility = View.GONE

                        GlideLib.loadImage(activity, ivCommentFirst, videoBean.profile_pic[0])
                        GlideLib.loadImage(activity, ivCommentSecond, videoBean.profile_pic[1])
                    }
                    videoBean.profile_pic.size == 1 -> {
                        ivCommentFirst.visibility = View.VISIBLE
                        ivCommentSecond.visibility = View.GONE
                        ivCommentThird.visibility = View.GONE

                        GlideLib.loadImage(activity, ivCommentFirst, videoBean.profile_pic[0])
                    }
                    else -> {
                        ivCommentFirst.visibility = View.GONE
                        ivCommentSecond.visibility = View.GONE
                        ivCommentThird.visibility = View.GONE
                    }
                }
            }
            ivVideoImage.setOnClickListener {
                if (fromEdit) {
                    val videoListTemp = ArrayList(videoList)
                    videoListTemp.removeAt(0)  // Remove first position plus element
                    onPostImageClick.onItemPostImageClick(position - 1, videoListTemp)

                } else {
                    onPostImageClick.onItemPostImageClick(position, videoList)

                }

            }

        }

    }
}