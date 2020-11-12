package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.CommentBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_comment.view.*

class CommentAdapter(
    var commentList: ArrayList<CommentBean>,
    var activity: Context,
    var onSelectUserItemClick: OnSelectUserItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_comment, parent, false
        )
    )

    override fun getItemCount() = commentList.size

    fun addCommentLastPosition(commentBean: CommentBean) {
        commentList.add(commentBean)
        notifyItemInserted(itemCount)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val commentBean = commentList[position]
            tvUsername.text = commentBean.username
            tvComment.text = commentBean.comment

            GlideLib.loadImageUrlRoundCorner(activity, ivCommentUser, commentBean.profile_pic)

            holderComment.setOnClickListener {
                onSelectUserItemClick.onSelectItemClick(commentBean.user_id, position, commentBean.user_profile_type)
            }
        }

    }
}