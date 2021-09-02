package com.namastey.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_share_profile.view.*
import kotlinx.android.synthetic.main.row_share_profile.view.ivFindUser
import java.util.ArrayList

class UserShareAdapter(
    var userList: ArrayList<DashboardBean>,
    var activity: Context,
    var isDisplayCkb: Boolean,
    var onItemClick: OnItemClick,
    var onSelectUserItemClick: OnSelectUserItemClick
) : RecyclerView.Adapter<UserShareAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_share_profile, parent, false
        )
    )

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun filterList(filteredName: ArrayList<DashboardBean>) {
        this.userList = filteredName
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            val dashboardBean = userList[position]

            tvFindName.text = dashboardBean.username

            GlideLib.loadImage(activity, ivFindUser, dashboardBean.profile_url)

            viewSearchUser.setOnClickListener {
                onItemClick.onItemClick(dashboardBean.user_id, position)
                onItemClick.onItemFollowingClick(dashboardBean)
            }
            tvSendLabel.setOnClickListener {
                if(tvSendLabel.isSelected) {
                    tvSendLabel.text = context.getString(R.string.send)
                    tvSendLabel.setTextColor(ContextCompat.getColorStateList(context,R.color.white))
                    tvSendLabel.isSelected = false
                }
                else {
                    tvSendLabel.text = context.getString(R.string.undo)
                    tvSendLabel.setTextColor(ContextCompat.getColorStateList(context,R.color.color_text_red))
                    tvSendLabel.isSelected = true
                }
                onItemClick.onItemFollowingClick(dashboardBean)
            }
        }

    }
}