package com.namastey.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.model.MatchesListBean
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_message.view.*
import java.util.*

class MessagesAdapter(
    var matchesList: ArrayList<MatchesListBean>,
    var activity: Activity,
    var onMatchesItemClick: OnMatchesItemClick,
    var sessionManager: SessionManager
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_message, parent, false
        )
    )

    override fun getItemCount() = matchesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val matchesListBean = matchesList[position]

            Log.e("MessageAdapter", "MatchesBean: Id: \t ${matchesListBean.id}")

            llMessage.setOnClickListener {
                onMatchesItemClick.onMatchesItemClick(position, matchesListBean, true)
            }

            if (matchesListBean.sub_cat_details.size > 0) {
                Log.e(
                    "MessagesAdapter",
                    "sub_cat_details: \t ${matchesListBean.sub_cat_details[0].name}"
                )
               tvCategory.visibility = View.VISIBLE
                tvCategory.text = matchesListBean.sub_cat_details[0].name

//                Utils.rectangleShapeGradient(
//                    mainCategoryView, intArrayOf(
//                        Color.parseColor(matchesListBean.sub_cat_details[0].start_color),
//                        Color.parseColor(matchesListBean.sub_cat_details[0].end_color)
//                    )
//                )
//                mainCategoryView.alpha = 0.5f
            } else {
//                tvCategory.visibility = View.GONE
                tvCategory.visibility = View.GONE
            }

            if (matchesListBean.id == 0L) {
                tvLastTime.visibility = View.GONE
            } else {
                tvLastTime.visibility = View.VISIBLE
            }

            tvUsername.text = matchesListBean.casual_name
            if (matchesListBean.chatMessage.timestamp != -1L) {
                tvLastTime.text =
                    Utils.convertTimestampToMessageFormat(matchesListBean.chatMessage.timestamp)
            }
            GlideLib.loadImage(activity, ivUserProfile, matchesListBean.profile_pic)

            if (matchesListBean.chatMessage.read == 1 || matchesListBean.chatMessage.sender == sessionManager.getUserId()) {
                tvLastMsg.setTextColor(ContextCompat.getColor(activity, R.color.color_text))
                tvLastTime.setTextColor(ContextCompat.getColor(activity, R.color.color_text))
                tvUnreadMsg.visibility = View.GONE
                tvLastTime.visibility = View.GONE
                tvCategory.visibility = View.VISIBLE
            } else {
                tvCategory.visibility = View.GONE
                tvLastMsg.setTextColor(ContextCompat.getColor(activity, R.color.colorRed))
                tvLastTime.setTextColor(ContextCompat.getColor(activity, R.color.colorRed))
                if (matchesListBean.chatMessage.unreadCount != 0) {
                    tvUnreadMsg.visibility = View.VISIBLE
                    tvLastTime.visibility = View.VISIBLE
                    tvUnreadMsg.text = matchesListBean.chatMessage.unreadCount.toString()
                }
            }

            when (matchesListBean.chatMessage.message) {
                Constants.FirebaseConstant.MSG_TYPE_IMAGE -> {
                    tvLastMsg.text = activity.getString(R.string.image)
                    tvLastMsg.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_camera_blue,
                        0,
                        0,
                        0
                    )
                    tvLastMsg.compoundDrawablePadding = 10
                }
                Constants.FirebaseConstant.MSG_TYPE_VOICE -> {
                    if (matchesListBean.chatMessage.url.isNotEmpty())
                        tvLastMsg.text = Utils.getMediaDuration(matchesListBean.chatMessage.url)
                    else
                        tvLastMsg.text = activity.getString(R.string.voice_message)
                    tvLastMsg.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_microphone_blue,
                        0,
                        0,
                        0
                    )
                    tvLastMsg.compoundDrawablePadding = 10
                }
                else -> {
                    tvLastMsg.text = matchesListBean.chatMessage.message
                    tvLastMsg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }
    }
}