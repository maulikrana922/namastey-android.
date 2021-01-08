package com.namastey.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.listeners.OnSocialTextViewClick
import com.namastey.model.CommentBean
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils.covertTimeToText
import kotlinx.android.synthetic.main.row_comment.view.*
import java.util.*

class CommentAdapter(
    var commentList: ArrayList<CommentBean>,
    var activity: Activity,
    var onSelectUserItemClick: OnSelectUserItemClick,
    var onSocialTextViewClick: OnSocialTextViewClick
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val commentBean = commentList[position]
            tvUsername.text = commentBean.username
            tvComment.text = commentBean.comment
            tvComment.mentionColor = ContextCompat.getColor(context, R.color.colorBlueLight)
            tvComment.setOnMentionClickListener { view, text ->
                Log.e("CommentAdapter", "setOnMentionClickListener: $text")
                Log.e("CommentAdapter", "setOnMentionClickListener: ${view.mentions}")

                onSocialTextViewClick.onClickSocialText(text.toString())

                /*val intent = Intent(context, ProfileViewActivity::class.java)
                intent.putExtra(Constants.USERNAME, text.toString())
                context.startActivity(intent)
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);*/
            }

            GlideLib.loadImageUrlRoundCorner(activity, ivCommentUser, commentBean.profile_pic)

            Log.e("CommentAdapter", "mentionColor: ${commentBean.comment}")

            val updatedAt = covertTimeToText(commentBean.updated_at)!!
            if (updatedAt.contains("-")) {
                tvDuration.text = updatedAt.replace("-", "")
            } else {
                tvDuration.text = updatedAt
            }
            holderComment.setOnClickListener {
                onSelectUserItemClick.onSelectItemClick(
                    commentBean.user_id,
                    position,
                      commentBean.user_profile_type
                )
            }
        }
    }
}