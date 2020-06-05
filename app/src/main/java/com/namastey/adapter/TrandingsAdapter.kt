package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.TrandingBean
import kotlinx.android.synthetic.main.row_tranding.view.*

class TrandingsAdapter(
    var trandingsList: ArrayList<TrandingBean>,
    var activity: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<TrandingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_tranding, parent, false
        )
    )

    override fun getItemCount() = trandingsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvTrandingUsername.text = trandingsList.get(position).name

        }

    }
}