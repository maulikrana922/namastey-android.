package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.namastey.R
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import kotlinx.android.synthetic.main.row_filter_category.view.*

class FilterCategoryAdapter(
    var categoryList: ArrayList<CategoryBean>,
    var context: Activity,
    var onCategoryItemClick: OnCategoryItemClick
) : androidx.recyclerview.widget.RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

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
            tvFilterCategory.text = categoryList.get(position).name

            if (categoryList[position].is_selected == 1){
                tvFilterCategory.setTextColor(Color.WHITE)
                tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_green_top_right_solid)

//                val gd = GradientDrawable(
//                    GradientDrawable.Orientation.TR_BL,
//                    intArrayOf(
//                        ContextCompat.getColor(context, R.color.colorBlueLight),
//                        ContextCompat.getColor(context, R.color.colorGreenLight)
//                    )
//                )
//
//                gd.shape = GradientDrawable.RECTANGLE
//                gd.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
//                llMainFilterCategory.background = gd
            }else{
                tvFilterCategory.setTextColor(ContextCompat.getColor(context,R.color.colorDarkGray))
                tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_border_top_right)
            }

            itemView.setOnClickListener{
                if (categoryList[position].is_selected == 0) {
                    categoryList[position].is_selected = 1
                    tvFilterCategory.setTextColor(Color.WHITE)
                    tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_green_top_right_solid)
//                    tvFilterCategory.setTextColor(Color.WHITE)
//                    val gd = GradientDrawable(
//                        GradientDrawable.Orientation.TR_BL,
//                        intArrayOf(
//                            ContextCompat.getColor(context, R.color.colorBlueLight),
//                            ContextCompat.getColor(context, R.color.colorGreenLight)
//                        )
//                    )
//
//                    gd.shape = GradientDrawable.RECTANGLE
//                    gd.cornerRadii = floatArrayOf(0f, 0f, 54f, 54f, 0f, 0f, 54f, 54f)
//                    llMainFilterCategory.background = gd
                }else{
                    categoryList[position].is_selected = 0
                    tvFilterCategory.setTextColor(ContextCompat.getColor(context,R.color.colorDarkGray))
                    tvFilterCategory.setBackgroundResource(R.drawable.rounded_blue_border_top_right)
                }
//                notifyItemChanged(position)
                onCategoryItemClick.onCategoryItemClick(categoryList.get(position))
            }
        }

    }
}