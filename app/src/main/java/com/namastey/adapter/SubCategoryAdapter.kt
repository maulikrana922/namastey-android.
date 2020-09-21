package com.namastey.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.model.CategoryBean
import kotlinx.android.synthetic.main.row_sub_category.view.*

class SubCategoryAdapter(
    var categoryList: ArrayList<CategoryBean>,
    var context: Activity
) : androidx.recyclerview.widget.RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_sub_category, parent, false
        )
    )

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvSubCategory.text = categoryList.get(position).name

//            GlideLib.loadImageUrlRoundCorner(context, ivSubCategory, "")
        }

    }
}