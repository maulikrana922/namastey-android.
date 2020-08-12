package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnEducationItemClick
import com.namastey.model.EducationBean
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.row_education.view.*

class EducationAdapter(
    var educationList: ArrayList<EducationBean>,
    var activity: Context,
    var sessionManager: SessionManager,
    var onItemClick: OnEducationItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<EducationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_education, parent, false
        )
    )

    override fun getItemCount() = educationList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {

            var educationBean = educationList[position]

            rbSelected.isChecked = educationBean.isSelect == 1

            tvTitle.text = educationBean.college
            tvSubTitle.text = educationBean.course

            mainViewHolder.setOnClickListener {
                onItemClick.onEducationItemClick(educationBean, position)
            }
            rbSelected.setOnClickListener {
                if (educationList[position].isSelect == 0) {
                    rbSelected.isChecked = true
                    educationBean.isSelect = 1
                    sessionManager.setEducationBean(educationBean)
                    (activity as Activity).finish()
                } else {
                    rbSelected.isChecked = false
                    educationList[position].isSelect = 0
                    notifyDataSetChanged()
                }
            }
        }

    }
}