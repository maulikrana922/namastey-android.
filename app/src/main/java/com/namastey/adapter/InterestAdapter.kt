package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.fragment.ChooseInterestFragment
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.InterestSubCategoryBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_choose_interest.view.*
import java.util.*

class InterestAdapter(
    //var interestList: ArrayList<InterestBean>,
    var interestList: ArrayList<InterestSubCategoryBean>,
    var activity: Context,
    var onImageItemClick: OnImageItemClick,
    var isDisplayCkb: Boolean
) : androidx.recyclerview.widget.RecyclerView.Adapter<InterestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_choose_interest, parent, false
        )
    )

    override fun getItemCount() = interestList.size


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvInterstTitle.text = interestList.get(position).name

            GlideLib.loadImage(activity,ivInterest,interestList.get(position).sub_cat_img)

            view.alpha = 0.3f
            videoSelectMain.setOnClickListener {
                if (isDisplayCkb){
                    if (ivVideoCheck.visibility == View.VISIBLE){
                        view.alpha = 0.3f
                        ivVideoCheck.visibility = View.GONE
//                        --ChooseInterestFragment.noOfSelectedImage
                    }else{
                        view.alpha = 0.5f
                        ivVideoCheck.visibility = View.VISIBLE
//                        ++ChooseInterestFragment.noOfSelectedImage
                    }
                }
                onImageItemClick.onImageItemClick(interestList.get(position))
            }

        }

    }
}