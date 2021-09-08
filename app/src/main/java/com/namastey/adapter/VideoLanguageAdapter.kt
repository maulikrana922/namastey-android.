package com.namastey.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.model.VideoLanguageBean
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.row_video_language.view.*
import java.util.*
import javax.inject.Inject


class VideoLanguageAdapter(
    var videoLanguageList: ArrayList<VideoLanguageBean>,
    var activity: Context,
    var selectLanguageList: ArrayList<Int>,
    var onItemClick: OnItemClick,
    var sessionManager: SessionManager
) : androidx.recyclerview.widget.RecyclerView.Adapter<VideoLanguageAdapter.ViewHolder>() {
    var colors = intArrayOf(
        Color.BLACK,
        ContextCompat.getColor(activity, R.color.colorBlueLight),
        ContextCompat.getColor(activity, R.color.colorButtonRed),
        ContextCompat.getColor(activity, R.color.colorGreenLight),
        ContextCompat.getColor(activity, R.color.colorBlue)
    )

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_video_language, parent, false
        )
    )

    override fun getItemCount() = videoLanguageList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


        fun bind(position: Int) = with(itemView) {
            tvFirstLanguage.text = videoLanguageList.get(position).video_lang_name
            tvSecondLanguage.text = videoLanguageList.get(position).video_lang

            tvFirstLanguage.setTextColor(colors[position % 5])

            selectLanguageList = sessionManager.getLanguageIdList()

            if (selectLanguageList.contains(videoLanguageList.get(position).id)){
                ckbLanguage.isChecked = true
            }else{
                ckbLanguage.isChecked = false
            }

            ckbLanguage.setOnClickListener { v ->
                onItemClick.onLanguageItemClick(videoLanguageList.get(position))
            }
        }

    }

    interface OnItemClick {
        fun onLanguageItemClick(videoLanguageBean: VideoLanguageBean)
    }
}