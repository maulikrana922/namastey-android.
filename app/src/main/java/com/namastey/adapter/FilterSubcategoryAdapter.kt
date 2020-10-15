package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.listeners.OnImageItemClick
import com.namastey.model.CategoryBean
import kotlinx.android.synthetic.main.row_filter_subcategory.view.*

class FilterSubcategoryAdapter(
    var subCategoryList: ArrayList<CategoryBean>,
    var activity: Context,
    var onCategoryItemClick: OnCategoryItemClick,
    var isDisplayCkb: Boolean
) : androidx.recyclerview.widget.RecyclerView.Adapter<FilterSubcategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_filter_subcategory, parent, false
        )
    )

    override fun getItemCount() = subCategoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvSubCategory.text = subCategoryList[position].name

//            GlideLib.loadImage(activity,ivInterest,subCategoryList.get(position).image)

            view.alpha = 0.3f
            holderSubcategory.setOnClickListener {
                onCategoryItemClick.onSubCategoryItemClick(subCategoryList[position].id)
            }

        }

    }
}