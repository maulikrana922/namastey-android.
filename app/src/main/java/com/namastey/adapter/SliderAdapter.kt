package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.namastey.R
import com.namastey.customViews.CustomTextView
import com.namastey.model.MembershipBean

class SliderAdapter(
    private val context: Context,
    private val membershipList: ArrayList<MembershipBean>
) : PagerAdapter() {
    override fun getCount(): Int {
        return membershipList.size
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }

    override fun instantiateItem(
        container: ViewGroup,
        position: Int
    ): Any {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view =
            inflater.inflate(R.layout.row_membership_message, null)

        val tvMemberTitle =
            view.findViewById<View>(R.id.tvMemberTitle) as CustomTextView
        val tvMemberDesc = view.findViewById<View>(R.id.tvMemberDesc) as CustomTextView


        tvMemberTitle.text = membershipList[position].name
        tvMemberDesc.text = membershipList[position].description

        val viewPager = container as ViewPager

        viewPager.addView(view)
        return view
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }

}