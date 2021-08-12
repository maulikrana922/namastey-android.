package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.model.MatchesListBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_matches_profile.view.*
import java.util.*


class MatchedProfileAdapter(
    var matchesList: ArrayList<MatchesListBean>,
    var activity: Activity,
    var onMatchesItemClick: OnMatchesItemClick
) : RecyclerView.Adapter<MatchedProfileAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_matches_profile, parent, false
        )
    )

    override fun getItemCount() = matchesList.size

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val matchesListBean = matchesList[position]
            if (matchesListBean.is_read == 1)
                llProfileImage.setBackgroundResource(0)
            else
                llProfileImage.background =
                    activity.getDrawable(R.drawable.circle_pink_hollow)

            tvProfileName.text = matchesListBean.casual_name

            GlideLib.loadImage(activity, ivProfileImage, matchesListBean.profile_pic)

            llMatches.setOnClickListener {
                onMatchesItemClick.onMatchesItemClick(position, matchesListBean,false)
            }

        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}