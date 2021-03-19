package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnMentionUserItemClick
import com.namastey.model.MentionListBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_mention.view.*
import java.util.*

class MentionListAdapter(
    var mentionList: ArrayList<MentionListBean>,
    var activity: Context,
    var onMentionUserItemClick: OnMentionUserItemClick
) : RecyclerView.Adapter<MentionListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_mention, parent, false
        )
    )

    override fun getItemCount() = mentionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val mentionListBean = mentionList[position]
            tvUsername.text = mentionListBean.username

            GlideLib.loadImageUrlRoundCorner(activity, ivMentionUser, mentionListBean.profile_url)

            holderMention.setOnClickListener {
                onMentionUserItemClick.onMentionItemClick(
                    mentionListBean.user_id,
                    position,
                    mentionListBean.username
                )
            }
        }

    }
}