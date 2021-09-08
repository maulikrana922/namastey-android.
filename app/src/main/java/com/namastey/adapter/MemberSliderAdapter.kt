package com.namastey.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.namastey.R
import com.namastey.customViews.CustomTextView
import com.namastey.model.MembershipSlide
import java.util.*


class MemberSliderAdapter (
    private val context: Context,
    private val membershipList: ArrayList<MembershipSlide>,

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
            inflater.inflate(R.layout.row_member, null)

        val tvDescription =
            view.findViewById<View>(R.id.tv_Text1) as CustomTextView
        val tvDescription1 = view.findViewById<View>(R.id.tv_Text2) as CustomTextView
        val tvcount = view.findViewById<View>(R.id.tv_Text3) as CustomTextView
        val tvtitle = view.findViewById<View>(R.id.tv_slider_title) as CustomTextView
        val iv_slider_Icon = view.findViewById<View>(R.id.iv_slider_Icon) as ImageView

      //  view.conBachground.setBackgroundResource(membershipList[position].background)
        tvDescription.text = membershipList[position].title
        tvDescription1.text = membershipList[position].description
        tvcount.text = membershipList[position].count
        iv_slider_Icon.setImageResource(membershipList[position].profile_url)

        if(membershipList[position].count.equals("")){
            tvcount.visibility = GONE
        }else{
            tvcount.visibility = VISIBLE
        }
        val paint = tvtitle.paint
        val width = paint.measureText(tvtitle.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, width, tvtitle.textSize, intArrayOf(
            Color.parseColor("#D07AE2"),
            Color.parseColor("#F9245A")
        ), floatArrayOf(-2f, 3f), Shader.TileMode.REPEAT)
        tvtitle.paint.setShader(textShader)

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
