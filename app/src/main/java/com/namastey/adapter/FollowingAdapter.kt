package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.FollowingBean
import kotlinx.android.synthetic.main.row_following.view.*

class FollowingAdapter(
    var followingList: ArrayList<FollowingBean>,
    var activity: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_following, parent, false
        )
    )

    override fun getItemCount() = followingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvFollowingName.text = followingList.get(position).name


        }

    }
}