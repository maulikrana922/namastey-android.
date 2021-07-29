package com.namastey.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnItemClick
import com.namastey.listeners.OnSelectUserItemClick
import com.namastey.model.DashboardBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_user_suggested.view.*
import java.util.*


class UserSearchAdapter(
    var userList: ArrayList<DashboardBean>,
    var activity: Context,
    var isDisplayCkb: Boolean,
    var onItemClick: OnItemClick,
    var onSelectUserItemClick: OnSelectUserItemClick
) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_user_suggested, parent, false
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

            tvFindUser.text = dashboardBean.username
            tvUserJob.text = dashboardBean.job

            GlideLib.loadImage(activity, ivFindUser, dashboardBean.profile_url)

            if (isDisplayCkb) {
                ckbFindUser.visibility = View.VISIBLE
                ckbFindUser.isChecked = dashboardBean.isChecked == 1
            } else {
                ckbFindUser.visibility = View.GONE
            }

            ckbFindUser.setOnClickListener {
                Log.e("UserSearchAdapter", "dashboardBean.user_id:\t ${dashboardBean.user_id}")
                Log.e("UserSearchAdapter", "dashboardBean.id:\t ${dashboardBean.id}")
                if (userList[position].isChecked == 1) {
                    ckbFindUser.isChecked = false
                    userList[position].isChecked = 0
                } else {
                    ckbFindUser.isChecked = true
                    userList[position].isChecked = 1
                }
                onSelectUserItemClick.onSelectItemClick(dashboardBean.id, position)

            }
//            ckbFindUser.setOnCheckedChangeListener { compoundButton, b ->
//                if (b){
//                    userList[position].isChecked = 0
//                }else{
//                    userList[position].isChecked = 1
//                }
//                onSelectUserItemClick.onSelectItemClick(dashboardBean.user_id,position)
//            }
            viewSearchUser.setOnClickListener {
                onItemClick.onItemClick(dashboardBean.user_id, position)
                onItemClick.onItemFollowingClick(dashboardBean)
            }
        }

    }

    fun displayRadioButton() {
        isDisplayCkb = true
        notifyDataSetChanged()
    }
}