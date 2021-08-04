package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnJobItemClick
import com.namastey.model.JobBean
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.row_education.view.*
import java.util.*

class JobListingAdapter(
    var jobListing: ArrayList<JobBean>,
    var activity: Context,
    var sessionManager: SessionManager,
    var onItemClick: OnJobItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<JobListingAdapter.ViewHolder>() {
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_education, parent, false
        )
    )

    override fun getItemCount() = jobListing.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            val jobBean = jobListing[position]

            if (jobListing[position].isSelect == 1){
                selectedPosition = position
                rbSelected.isChecked = true
            }else{
                rbSelected.isChecked = false
            }

            tvTitle.text = jobBean.title
            tvSubTitle.text = jobBean.company_name

            mainViewHolder.setOnClickListener{
                onItemClick.onJobItemClick(jobBean,position)
            }
            rbSelected.setOnClickListener{
                if (jobListing[position].isSelect == 0){
                    rbSelected.isChecked = true
                    jobBean.isSelect = 1
//                    sessionManager.setJobBean(jobBean)
                    jobListing[position].isSelect = 1
                    jobListing[selectedPosition].isSelect = 0
                    selectedPosition = position
                }else{
                    rbSelected.isChecked = false
                    jobListing[position].isSelect = 0
                }
                notifyDataSetChanged()
            }
        }

    }
}