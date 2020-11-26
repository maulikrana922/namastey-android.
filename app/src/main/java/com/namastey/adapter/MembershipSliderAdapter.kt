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
import com.namastey.model.MembershipSlide
import kotlinx.android.synthetic.main.row_dialog_membership.view.*
import kotlinx.android.synthetic.main.row_membership.view.*

class MembershipSliderAdapter(
    private val context: Context,
    private val membershipList: ArrayList<MembershipSlide>
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
            inflater.inflate(R.layout.row_membership, null)

        val tvDescription =
            view.findViewById<View>(R.id.tvDescription) as CustomTextView
        val tvDescription1 = view.findViewById<View>(R.id.tvDescription1) as CustomTextView

        view.conBachground.setBackgroundResource(membershipList[position].background)
        tvDescription.text=membershipList[position].title
        tvDescription1.text=membershipList[position].description
        view.ivBoost.setImageResource(membershipList[position].profile_url)

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