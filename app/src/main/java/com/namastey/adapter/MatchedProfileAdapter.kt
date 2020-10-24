package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.namastey.R
import com.namastey.dagger.module.GlideApp
import com.namastey.model.MatchesListBean


class MatchedProfileAdapter(var matchesList: ArrayList<MatchesListBean>, var activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MatchesListBean.firstImage) {
            val layoutOne: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_matches_profile_first, parent, false)
            return LikesViewHolder(layoutOne)
        } else if (viewType == MatchesListBean.allImages) {
            val layoutTwo: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_matches_profile, parent, false)
            return LikesProfileViewHolder(layoutTwo)
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MatchesListBean.firstImage) {
            val layoutOne: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_matches_profile_first, parent, false)
            return LikesViewHolder(layoutOne)
        } else {
            val layoutTwo: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_matches_profile, parent, false)
            return LikesProfileViewHolder(layoutTwo)
        }
    }

    override fun getItemCount() = matchesList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val matches = matchesList[position]
        when (position) {
            0 -> {
                val likeViewHolder = holder as LikesViewHolder
                val likeCount = matchesList.size - 1
                likeViewHolder.tvLikesCount.text = likeCount.toString()
            }
            1 -> {
                val likeProfileViewHolder = holder as LikesProfileViewHolder
                likeProfileViewHolder.tvProfileName.text = matches.first_name
                GlideApp
                    .with(activity)
                    .load(matches.image)
                    .into(likeProfileViewHolder.ivProfileImage)

            }
            else -> return
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == 0) {
            MatchesListBean.firstImage
        } else {
            MatchesListBean.allImages
        }
        /* return when (matchesList.get(position).viewType) {
             0 -> MatchesListBean.firstImage
             1 -> MatchesListBean.allImages
             else -> -1
         }*/
    }

    internal class LikesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvLikesCount: TextView

        init {
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount)
        }
    }

    internal class LikesProfileViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val ivProfileImage: ImageView
        val tvProfileName: TextView

        init {
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage)
            tvProfileName = itemView.findViewById(R.id.tvProfileName)
        }
    }

/*override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.row_matches_profile, parent, false
    )
)

override fun getItemCount() = 7


inner class ViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(position: Int) = with(itemView) {

    }
}

override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(position)
}*/
}