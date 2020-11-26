package com.namastey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.namastey.R
import com.namastey.model.MembershipSlide
import kotlinx.android.synthetic.main.row_dialog_membership.view.*

class MembershipDialogSliderAdapter(
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
            inflater.inflate(R.layout.row_dialog_membership, null)

        /*   val tvMemberTitle =
               view.findViewById<View>(R.id.tvMemberTitle) as CustomTextView
           val tvMemberDesc = view.findViewById<View>(R.id.tvMemberDesc) as CustomTextView
   */
        view.conBackground.setBackgroundResource(membershipList[position].background)
        view.tvText1.text = membershipList[position].title
        view.tvText2.text = membershipList[position].description
        view.ivIcon.setImageResource(membershipList[position].profile_url)
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