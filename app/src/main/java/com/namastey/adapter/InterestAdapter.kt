package com.namastey.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.fragment.ChooseInterestFragment
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.InterestBean
import kotlinx.android.synthetic.main.row_choose_interest.view.*

class InterestAdapter(
    var interestList: ArrayList<InterestBean>,
    var activity: Context,
    var onImageItemClick: OnImageItemClick
) : RecyclerView.Adapter<InterestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_choose_interest, parent, false
        )
    )

    override fun getItemCount() = interestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvInterstTitle.text = interestList.get(position).interest_name

            ckbInterest.setOnClickListener(View.OnClickListener { v ->
                if (ckbInterest.isChecked){
                    ++ChooseInterestFragment.noOfSelectedImage
                }else{
                    --ChooseInterestFragment.noOfSelectedImage
                }
                onImageItemClick.onImageItemClick(interestList.get(position))
            })

        }

    }
}