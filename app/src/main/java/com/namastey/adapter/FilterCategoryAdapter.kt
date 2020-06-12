package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.drawable.DrawableCompat
import com.namastey.R
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_filter_category.view.*

class FilterCategoryAdapter(
    var categoryList: ArrayList<CategoryBean>,
    var context: Activity,
    var onCategoryItemClick: OnCategoryItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

    var gradient_color_start = context.resources.getIntArray(R.array.gradient_color_start)
    var gradient_color_end = context.resources.getIntArray(R.array.gradient_color_end)

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

            if (categoryList[position].is_selected == 1){
                tvFilterCategory.setTextColor(Color.WHITE)
//                tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_green_top_right_solid)

                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TR_BL,
                    intArrayOf(
                        gradient_color_start[position % 6],
                        gradient_color_end[position % 6]
                    )
                )

                gd.shape = GradientDrawable.RECTANGLE
                gd.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
                tvFilterCategory.background = gd
            }else{
                tvFilterCategory.setTextColor(getColor(context,R.color.colorDarkGray))
                Utils.customView(tvFilterCategory,gradient_color_end[position % 6])

//                tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_border_top_right)
            }

            itemView.setOnClickListener{
                if (categoryList[position].is_selected == 0) {
                    categoryList[position].is_selected = 1
                    tvFilterCategory.setTextColor(Color.WHITE)
//                    tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_green_top_right_solid)
                    val gd = GradientDrawable(
                        GradientDrawable.Orientation.TR_BL,
                        intArrayOf(
                            gradient_color_start[position % 6],
                            gradient_color_end[position % 6]
                        )
                    )

                    gd.shape = GradientDrawable.RECTANGLE
                    gd.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
                    tvFilterCategory.background = gd
                }else{
                    categoryList[position].is_selected = 0
                    tvFilterCategory.setTextColor(getColor(context,R.color.colorDarkGray))
                    Utils.customView(tvFilterCategory,gradient_color_end[position % 6])

//                    tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_border_top_right)
                }
                onCategoryItemClick.onCategoryItemClick(categoryList.get(position))
            }
        }

    }
}