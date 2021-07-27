package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.fragment.SelectFilterFragment
import com.namastey.model.CategoryBean
import com.namastey.utils.Constants
import com.namastey.utils.Utils
import kotlinx.android.synthetic.main.row_category.view.*
import java.util.*


class CategoryAdapter(
    var categoryList: ArrayList<CategoryBean>,
    var context: Activity
) : androidx.recyclerview.widget.RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    var clickPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_category, parent, false
        )
    )

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvCategory.text = categoryList[position].name

//            Utils.rectangleShapeGradient(
//                mainCategoryView, intArrayOf(
//                    Color.parseColor(categoryList[position].startColor),
//                    Color.parseColor(categoryList[position].endColor)
//                )
//            )
//            mainCategoryView.alpha = 0.6f
            tvCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_drop_up, 0)


         /*   mainCategoryView.setOnClickListener {
                val selectFilterFragment =
                    (context as DashboardActivity).supportFragmentManager.findFragmentByTag(
                        Constants.SELECT_FILTER_FRAGMENT
                    )

                notifyItemChanged(clickPosition)
                if (clickPosition != position) {
                    mainCategoryView.alpha = 1f
                    tvCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_white, 0);
                    clickPosition = position
                    (context as DashboardActivity).supportFragmentManager.popBackStackImmediate()
                    (context as DashboardActivity).addFragmentCategory(
                        SelectFilterFragment.getInstance(
                            categoryList[position].sub_category,
                            categoryList[position].startColor,
                            categoryList[position].endColor,
                            categoryList
                        ),
                        Constants.SELECT_FILTER_FRAGMENT
                    )
                } else {
                    if (selectFilterFragment == null) {
                        (context as DashboardActivity).supportFragmentManager.popBackStackImmediate()
                        (context as DashboardActivity).addFragmentCategory(
                            SelectFilterFragment.getInstance(
                                categoryList[position].sub_category,
                                categoryList[position].startColor,
                                categoryList[position].endColor,
                                categoryList
                            ),
                            Constants.SELECT_FILTER_FRAGMENT
                        )
                    } else {
                        clickPosition = -1
                        (context as DashboardActivity).supportFragmentManager.popBackStack()
                    }
                }

            }*/

        }

    }
}