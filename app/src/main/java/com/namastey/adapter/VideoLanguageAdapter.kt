package com.namastey.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.VideoLanguageBean
import kotlinx.android.synthetic.main.row_video_language.view.*

class VideoLanguageAdapter(
    var videoLanguageList: ArrayList<VideoLanguageBean>,
    var activity: Context,
    var onItemClick: OnItemClick
) : RecyclerView.Adapter<VideoLanguageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_video_language, parent, false
        )
    )

    override fun getItemCount() = videoLanguageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvFirstLanguage.text = videoLanguageList.get(position).video_lang
            tvSecondLanguage.text = videoLanguageList.get(position).video_lang_name

            ckbLanguage.setOnClickListener { v ->
                onItemClick.onLanguageItemClick(videoLanguageList.get(position))
            }
        }

    }

    interface OnItemClick {
        fun onLanguageItemClick(videoLanguageBean: VideoLanguageBean)
    }
}