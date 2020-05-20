package com.namastey.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.CategoryBean
import kotlinx.android.synthetic.main.row_category.view.*

class CategoryAdapter(
    var categoryList: ArrayList<CategoryBean>,
    var activity: Context
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_category, parent, false
        )
    )

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvCategory.text = categoryList.get(position).name


        }

    }
}