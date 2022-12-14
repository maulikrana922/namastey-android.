package com.namastey.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.namastey.R
import com.namastey.listeners.OnCategoryItemClick
import com.namastey.model.CategoryBean
import com.namastey.utils.SessionManager
import kotlinx.android.synthetic.main.row_select_category.view.*


class SelectCategoryAdapter(
    var categoryList: ArrayList<CategoryBean>,
    var context: Activity,
    var onCategoryItemClick: OnCategoryItemClick,
    var sessionManager: SessionManager
) : androidx.recyclerview.widget.RecyclerView.Adapter<SelectCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.row_select_category, parent, false
        )
    )

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) = with(itemView) {
            tvSelectCategory.text = categoryList[position].name

            if (sessionManager.getCategoryList().any { it.id == categoryList[position].id }) {
                tvSelectCategory.setTextColor(Color.RED)
            } else {
                tvSelectCategory.setTextColor(Color.BLACK)
            }

            llMainSelectCategory.setOnClickListener {
                if (tvSelectCategory.currentTextColor == Color.BLACK) {
                    tvSelectCategory.setTextColor(Color.RED)
                } else {
                    tvSelectCategory.setTextColor(Color.BLACK)
                }
                when {
                    position % 6 == 0 -> {
                        categoryList[position].startColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_one_start
                            )
                        )
                        categoryList[position].endColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_one_end
                            )
                        )
                    }
                    position % 6 == 1 -> {
                        categoryList[position].startColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_two_start
                            )
                        )
                        categoryList[position].endColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_two_end
                            )
                        )
                    }
                    position % 6 == 2 -> {
                        categoryList[position].startColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_three_start
                            )
                        )
                        categoryList[position].endColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_three_end
                            )
                        )
                    }
                    position % 6 == 3 -> {
                        categoryList[position].startColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_four_start
                            )
                        )
                        categoryList[position].endColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_four_end
                            )
                        )
                    }
                    position % 6 == 4 -> {
                        categoryList[position].startColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_five_start
                            )
                        )
                        categoryList[position].endColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_five_end
                            )
                        )
                    }
                    position % 6 == 5 -> {
                        categoryList[position].startColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_six_start
                            )
                        )
                        categoryList[position].endColor = "#" + Integer.toHexString(
                            ContextCompat.getColor(
                                context,
                                R.color.gradient_six_end
                            )
                        )
                    }
                }
                onCategoryItemClick.onCategoryItemClick(categoryList[position])
            }
        }

    }
}