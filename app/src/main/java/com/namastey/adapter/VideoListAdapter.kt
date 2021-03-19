package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.activity.AlbumVideoActivity
import com.namastey.activity.ProfileViewActivity
import com.namastey.listeners.OnItemClick
import com.namastey.model.VideoBean
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.dialog_alert.*
import kotlinx.android.synthetic.main.row_child_video_album.view.*
import java.util.*

class VideoListAdapter(
    var videoList: ArrayList<VideoBean>,
    var activity: Context,
    var onItemClick: OnItemClick,
    var fromCreate: Boolean
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

            val videoBean = videoList[position]
            if (fromCreate)
                ivRemoveVideo.visibility = View.VISIBLE

            if (videoBean.cover_image_url != null)
                GlideLib.loadImage(activity, ivVideoImage, videoBean.cover_image_url)

            ivRemoveVideo.setOnClickListener{
                object : CustomAlertDialog(
                    activity!! as Activity,
                    resources.getString(R.string.msg_remove_post), activity.getString(R.string.yes), activity.getString(R.string.cancel)
                ){
                    override fun onBtnClick(id: Int) {
                        when(id){
                            btnPos.id ->{
                                onItemClick.onItemClick(videoBean.id,position)
                            }
                            btnNeg.id ->{
                                dismiss()
                            }
                        }
                    }
                }.show()
            }

            if (!fromCreate){
                ivVideoImage.setOnClickListener{
                    val intent = Intent(activity, AlbumVideoActivity::class.java)
                    intent.putExtra(Constants.VIDEO_LIST, videoList)
                    intent.putExtra("position", position)
                    (activity as ProfileViewActivity).openActivity(intent)
                }
            }
        }

    }
}