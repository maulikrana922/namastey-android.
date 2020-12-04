package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.model.MatchesListBean
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_message.view.*

class MessagesAdapter(
    var matchesList: List<MatchesListBean>,
    var activity: Activity,
    var onMatchesItemClick: OnMatchesItemClick
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

            Log.e("MessagesAdapter", "matchesListBean: \t ${matchesListBean.id}")

            llMessageView.setOnClickListener {
                onMatchesItemClick.onMatchesItemClick(0, position, matchesListBean)
            }

          /*  if (matchesListBean.is_read == 1)
                llMessage.visibility = View.VISIBLE
            else
                llMessage.visibility = View.GONE*/

            if (matchesListBean.sub_cat_details != null && matchesListBean.sub_cat_details.size > 0) {
                Log.e(
                    "MessagesAdapter",
                    "sub_cat_details: \t ${matchesListBean.sub_cat_details[0].name}"
                )
                tvCategory.text = matchesListBean.sub_cat_details[0].name

                Utils.rectangleShapeGradient(
                    mainCategoryView, intArrayOf(
                        Color.parseColor(matchesListBean.sub_cat_details[0].start_color),
                        Color.parseColor(matchesListBean.sub_cat_details[0].end_color)
                    )
                )
                mainCategoryView.alpha = 0.5f
            } else {
                tvCategory.visibility = View.GONE
                mainCategoryView.visibility = View.GONE
            }

            tvUsername.text = matchesListBean.username

            GlideLib.loadImage(activity, ivUserProfile, matchesListBean.profile_pic)

            /*Utils.rectangleShapeGradient(
                mainCategoryView, intArrayOf(
                    Color.parseColor("#28BAD3"),
                    Color.parseColor("#A19FEE")
                )
            )*/

        }

    }
}