package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.JobBean
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.row_education.view.*

class JobListingAdapter(
    var jobListing: ArrayList<JobBean>,
    var activity: Context,
    var sessionManager: SessionManager
) : androidx.recyclerview.widget.RecyclerView.Adapter<JobListingAdapter.ViewHolder>() {

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

            var jobBean = jobListing[position]

            rbSelected.isChecked = jobBean.isSelect == 1

            tvTitle.text = jobBean.title
            tvSubTitle.text = jobBean.company_name

            rbSelected.setOnClickListener{
                if (jobListing[position].isSelect == 0){
                    rbSelected.isChecked = true
                    jobBean.isSelect = 1
                    sessionManager.setJobBean(jobBean)
                    (activity as Activity).finish()
                }else{
                    rbSelected.isChecked = false
                    jobListing[position].isSelect = 0
                    notifyDataSetChanged()
                }
            }
        }

    }
}