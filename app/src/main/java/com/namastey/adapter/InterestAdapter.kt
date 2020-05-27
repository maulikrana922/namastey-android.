package com.namastey.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.fragment.ChooseInterestFragment
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.InterestBean
import com.namastey.utils.GlideLib
import kotlinx.android.synthetic.main.row_choose_interest.view.*

class InterestAdapter(
    var interestList: ArrayList<InterestBean>,
    var activity: Context,
    var onImageItemClick: OnImageItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<InterestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_choose_interest, parent, false
        )
    )

    override fun getItemCount() = interestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvInterstTitle.text = interestList.get(position).interest_name

            GlideLib.loadImage(activity,ivInterest,interestList.get(position).image)

            view.alpha = 0.3f
            videoSelectMain.setOnClickListener {
                if (ivVideoCheck.visibility == View.VISIBLE){
                    view.alpha = 0.3f
                    ivVideoCheck.visibility = View.GONE
                    --ChooseInterestFragment.noOfSelectedImage
                }else{
                    view.alpha = 0.5f
                    ivVideoCheck.visibility = View.VISIBLE
                    ++ChooseInterestFragment.noOfSelectedImage
                }
                onImageItemClick.onImageItemClick(interestList.get(position))
            }
//            ckbInterest.setOnClickListener(View.OnClickListener { v ->
//                if (ckbInterest.isChecked){
//                    ++ChooseInterestFragment.noOfSelectedImage
//                }else{
//                    --ChooseInterestFragment.noOfSelectedImage
//                }
//                onImageItemClick.onImageItemClick(interestList.get(position))
//            })

        }

    }
}