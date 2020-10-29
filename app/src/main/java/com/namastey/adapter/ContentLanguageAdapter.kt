package com.namastey.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.model.VideoLanguageBean
import kotlinx.android.synthetic.main.row_cocntent_language.view.*

class ContentLanguageAdapter(
    var activity: Activity,
    var languageList: ArrayList<VideoLanguageBean>
) : RecyclerView.Adapter<ContentLanguageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_cocntent_language, parent, false
        )
    )

    override fun getItemCount() = languageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            Log.e("ContentLanguage", "video_lang: ${languageList[position].video_lang}")
            Log.e("ContentLanguage", "video_lang_name: ${languageList[position].video_lang_name}")

            tvContentLanguage.text = languageList[position].video_lang_name

        }

    }
}