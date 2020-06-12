package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.roomDB.entity.User
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_user_suggested.view.*


class AddFriendUserAdapter(
    var userList: ArrayList<User>,
    var activity: Context,
    var isDisplayCkb: Boolean
) : androidx.recyclerview.widget.RecyclerView.Adapter<AddFriendUserAdapter.ViewHolder>() {

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
            tvFindUser.text = userList.get(position).name

            GlideLib.loadImageUrlRoundCorner(activity, ivFindUser, "")

            if (isDisplayCkb) {
                ckbFindUser.visibility = View.VISIBLE
            } else {
                ckbFindUser.visibility = View.GONE
            }
        }

    }
}