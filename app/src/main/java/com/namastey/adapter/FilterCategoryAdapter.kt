package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import com.namastey.R
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_filter_category.view.*
import java.util.*

class FilterCategoryAdapter(
    var categoryList: ArrayList<CategoryBean>,
    var context: Activity,
    var onCategoryItemClick: OnCategoryItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

    //    var gradient_color_start = context.resources.getIntArray(R.array.gradient_color_start)
//    var gradient_color_end = context.resources.getIntArray(R.array.gradient_color_end)
    var lastSelectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_filter_category, parent, false
        )
    )

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvFilterCategory.text = categoryList[position].name

            if (categoryList[position].is_selected == 1) {
                tvFilterCategory.setTextColor(Color.WHITE)

                Utils.rectangleShapeGradient(
                    tvFilterCategory, intArrayOf(
                        Color.parseColor(categoryList[position].startColor),
                        Color.parseColor(categoryList[position].endColor)
                    )
                )
            } else {
                tvFilterCategory.setTextColor(getColor(context, R.color.colorDarkGray))
                Utils.rectangleShapeBorder(
                    tvFilterCategory,
                    Color.parseColor(categoryList[position].endColor),false
                )

            }

            itemView.setOnClickListener {
                if (position != lastSelectedPos) {
                    categoryList[position].is_selected = 1
                    tvFilterCategory.setTextColor(Color.WHITE)

                    Utils.rectangleShapeGradient(
                        tvFilterCategory, intArrayOf(
                            Color.parseColor(categoryList[position].startColor),
                            Color.parseColor(categoryList[position].endColor)
                        )
                    )
//                    onCategoryItemClick.onSubCategoryItemClick(position)
                    // Added this condition because of no need to reload entire adapter
                    notifyItemChanged(position)
                    if (lastSelectedPos != -1) {
                        categoryList[lastSelectedPos].is_selected = 0
                        tvFilterCategory.setTextColor(getColor(context, R.color.colorDarkGray))
                        Utils.rectangleShapeBorder(
                            tvFilterCategory,
                            Color.parseColor(categoryList[lastSelectedPos].endColor),false
                        )
                        notifyItemChanged(lastSelectedPos)
                    }
                    lastSelectedPos = position
                } else {
                    lastSelectedPos = -1
                    categoryList[position].is_selected = 0
                    tvFilterCategory.setTextColor(getColor(context, R.color.colorDarkGray))
                    Utils.rectangleShapeBorder(
                        tvFilterCategory,
                        Color.parseColor(categoryList[position].endColor),false
                    )
                }
                onCategoryItemClick.onCategoryItemClick(categoryList[position])
            }
        }

    }
}