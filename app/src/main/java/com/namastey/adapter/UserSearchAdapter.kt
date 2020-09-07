package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_user_suggested.view.*


class UserSearchAdapter(
    var userList: ArrayList<DashboardBean>,
    var activity: Context,
    var isDisplayCkb: Boolean,
    var onItemClick: OnItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_user_suggested, parent, false
        )
    )

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val dashboardBean = userList[position]
            tvFindUser.text = dashboardBean.username
            tvUserJob.text = dashboardBean.job

            GlideLib.loadImageUrlRoundCorner(activity, ivFindUser, dashboardBean.profile_url)

            if (isDisplayCkb) {
                ckbFindUser.visibility = View.VISIBLE
            } else {
                ckbFindUser.visibility = View.GONE
            }

            viewSearchUser.setOnClickListener{
                onItemClick.onItemClick(dashboardBean.user_id,position)
            }
        }

    }
}