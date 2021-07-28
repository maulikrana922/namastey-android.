package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.fragment.SelectFilterFragment
import com.namastey.model.CategoryBean
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_category.view.*
import kotlinx.android.synthetic.main.row_sub_category.view.*
import java.util.*

class SubCategoryAdapter(
    var categoryBean: CategoryBean,
    var context: Activity,
    var onItemClick: OnItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_sub_category, parent, false
        )
    )

    override fun getItemCount() = categoryBean.sub_category.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvSubCategory.text = categoryBean.sub_category[position].name

            subCategoryHolder.setOnClickListener{
                onItemClick.onItemClick(categoryBean.sub_category[position].id)
            }

            Utils.roundShapeGradient(
                tvSubCategory, intArrayOf(
                    Color.parseColor(categoryBean.startColor),
                    Color.parseColor(categoryBean.endColor)
                )
            )
            tvSubCategory.alpha = 0.6f


        }

    }

    public interface OnItemClick {
        fun onItemClick(subCategoryId: Int)
    }
}