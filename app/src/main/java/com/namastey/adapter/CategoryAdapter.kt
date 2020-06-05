package com.namastey.adapter

import android.app.Activity
import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namastey.R
import com.namastey.activity.DashboardActivity
import com.namastey.fragment.SelectFilterFragment
import com.namastey.model.CategoryBean
import com.namastey.utils.Constants
import kotlinx.android.synthetic.main.row_category.view.*

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

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvCategory.text = categoryList.get(position).name

            mainCategoryView.setOnClickListener{
                val selectFilterFragment =
                    (context as DashboardActivity).supportFragmentManager.findFragmentByTag(Constants.SELECT_FILTER_FRAGMENT)


                if (clickPosition != position){
//                    if (selectFilterFragment == null){
                        clickPosition = position
                        (context as DashboardActivity).supportFragmentManager.popBackStackImmediate()
                        (context as DashboardActivity).addFragment(
                            SelectFilterFragment.getInstance(
                                categoryList[position].sub_category
                            ),
                            Constants.SELECT_FILTER_FRAGMENT
                        )
//                    }
                }else{
                    clickPosition = -1
                    (context as DashboardActivity).supportFragmentManager.popBackStack()
                }

            }

        }

    }
}