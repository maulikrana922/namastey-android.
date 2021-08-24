package com.namastey.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.namastey.R
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import com.namastey.utils.GlideLib
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_filter_subcategory.view.*
import java.util.*

class FilterSubcategoryAdapter(
    var subCategoryList: ArrayList<CategoryBean>,
    var activity: Context,
    var onCategoryItemClick: OnCategoryItemClick,
    var isDisplayCkb: Boolean,
    var startColor: String,
    var endColor: String
) : androidx.recyclerview.widget.RecyclerView.Adapter<FilterSubcategoryAdapter.ViewHolder>() {

    var isshow:Boolean = false

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
            Log.e("KKKKK", "bind: ${Gson().toJson(subCategoryList[position])  }" )
            tvSubCategory.text = subCategoryList[position].name

            GlideLib.loadImage(activity, ivSubcategory, subCategoryList[position].sub_cat_img)
            ivSubcategory.visibility = View.GONE

            Utils.imageOverlayGradient(ivSubcategory, startColor, endColor)

            if(isshow ==  true) {

            if (subCategoryList[position].is_selected == 1) {
                Utils.filterrectangleCornerShapeGradient(
                    tvSubCategory, intArrayOf(
                        Color.parseColor(startColor),
                        Color.parseColor(endColor)
                    )
                )

            } else {

                Utils.filterrectangleShapeBorder(
                    tvSubCategory,
                    ContextCompat.getColor(
                        context,
                        R.color.colorFilterBorder
                    ), true
                )
            }
            } else {

                Utils.filterrectangleShapeBorder(
                    tvSubCategory,
                    ContextCompat.getColor(
                        context,
                        R.color.colorFilterBorder
                    ), true
                )
            }

            view.alpha = 0.3f
            holderSubcategory.setOnClickListener {
                isshow = true
                subCategoryList.forEach { subcategory ->
                    subcategory.is_selected = 0
                }
                subCategoryList[position].is_selected = 1
                onCategoryItemClick.onSubCategoryItemClick(subCategoryList[position].id)
                notifyDataSetChanged()
            }

        }

    }
}