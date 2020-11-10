package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_message.view.*

class MessagesAdapter(
    var activity: Activity,
    var onMatchesItemClick: OnMatchesItemClick
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_message, parent, false
        )
    )

    override fun getItemCount() = 7

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        fun bind(position: Int) = with(itemView) {

            llMessageView.setOnClickListener {
                onMatchesItemClick.onMatchesItemClick(0, position,null)
            }

            Utils.rectangleShapeGradient(
                mainCategoryView, intArrayOf(
                    Color.parseColor("#28BAD3"),
                    Color.parseColor("#A19FEE")
                )
            )
            mainCategoryView.alpha = 0.6f

        }

    }
}