package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnRecentUserItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.roomDB.entity.RecentUser
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_user_suggested.view.*
import java.util.*


class RecentUserAdapter(
    var recentUserList: ArrayList<RecentUser>,
    var activity: Context,
    var isDisplayCkb: Boolean,
    var onItemClick: OnItemClick,
    var onSelectUserItemClick: OnSelectUserItemClick,
    var onRecentUserItemClick: OnRecentUserItemClick
) : RecyclerView.Adapter<RecentUserAdapter.ViewHolder>() {

    var num = 1

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_user_suggested, parent, false
        )
    )

    /*override fun getCount(): Int {
        return if (num * 10 > recentUserList.size) {
            recentUserList.size
        } else {
            num * 10
        }
    }*/

    override fun getItemCount() = recentUserList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val recentUser = recentUserList[position]
            tvFindUser.text = recentUser.username
            tvUserJob.text = recentUser.job

            GlideLib.loadImageUrlRoundCorner(activity, ivFindUser, recentUser.profile_url)

            if (isDisplayCkb) {
                ckbFindUser.visibility = View.VISIBLE
                ckbFindUser.isChecked = recentUser.isChecked == 1
            } else {
                ckbFindUser.visibility = View.GONE
            }

            ckbFindUser.setOnClickListener {
                if (recentUserList[position].isChecked == 1) {
                    ckbFindUser.isChecked = false
                    recentUserList[position].isChecked = 0
                } else {
                    ckbFindUser.isChecked = true
                    recentUserList[position].isChecked = 1
                }
                onSelectUserItemClick.onSelectItemClick(recentUser.user_id, position)

            }
            viewSearchUser.setOnClickListener {
                onItemClick.onItemClick(recentUser.user_id, position)
                onRecentUserItemClick.onItemRecentUserClick(recentUser)
            }
        }

    }

    fun displayRadioButton() {
        isDisplayCkb = true
        notifyDataSetChanged()
    }
}